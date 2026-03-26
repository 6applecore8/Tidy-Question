package com.tidyquestion.backend.doc;

import com.tidyquestion.backend.common.BusinessException;
import com.tidyquestion.backend.common.ErrorCode;
import com.tidyquestion.backend.doc.dto.DocumentContentResponse;
import com.tidyquestion.backend.doc.dto.DocumentIndexResponse;
import com.tidyquestion.backend.doc.dto.DocumentUploadResponse;
import com.tidyquestion.backend.doc.dto.DocumentView;
import com.tidyquestion.backend.rag.ModelClient;
import com.tidyquestion.backend.rag.VectorUtils;
import com.tidyquestion.backend.security.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文档服务。
 */
@Service
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);
    private static final int DEFAULT_PREVIEW_MAX_CHARS = 12000;

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository documentChunkRepository;
    private final DocumentParserService documentParserService;
    private final TextChunker textChunker;
    private final ModelClient modelClient;
    private final UserContext userContext;
    private final Path localStoragePath;
    private final int embeddingDimension;

    public DocumentService(DocumentRepository documentRepository,
                           DocumentChunkRepository documentChunkRepository,
                           DocumentParserService documentParserService,
                           TextChunker textChunker,
                           ModelClient modelClient,
                           UserContext userContext,
                           @Value("${app.storage.local-dir}") String localDir,
                           @Value("${app.rag.embedding-dimension}") int embeddingDimension) {
        this.documentRepository = documentRepository;
        this.documentChunkRepository = documentChunkRepository;
        this.documentParserService = documentParserService;
        this.textChunker = textChunker;
        this.modelClient = modelClient;
        this.userContext = userContext;
        this.localStoragePath = Paths.get(localDir);
        this.embeddingDimension = embeddingDimension;
    }

    /**
     * 上传文档并创建文档记录。
     */
    public DocumentUploadResponse upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.DOC_UPLOAD_FAILED, "上传文件不能为空");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            originalName = "unknown-file";
        }

        Long userId = userContext.getCurrentUserId();
        try {
            Files.createDirectories(localStoragePath);
            String generatedName = UUID.randomUUID() + "_" + originalName;
            Path filePath = localStoragePath.resolve(generatedName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Long docId = documentRepository.insert(
                    userId,
                    originalName,
                    file.getContentType(),
                    filePath.toAbsolutePath().toString(),
                    "UPLOADED"
            );
            log.info("module=document action=upload result=success docId={} userId={} fileName={} size={}",
                    docId, userId, originalName, file.getSize());
            return new DocumentUploadResponse(docId, "UPLOADED");
        } catch (IOException ex) {
            log.error("module=document action=upload result=failed userId={} fileName={} message={}",
                    userId, originalName, ex.getMessage(), ex);
            throw new BusinessException(ErrorCode.DOC_UPLOAD_FAILED, "文档上传失败：" + ex.getMessage());
        }
    }

    /**
     * 将文档索引到向量库。
     */
    @Transactional
    public DocumentIndexResponse index(Long docId) {
        Long userId = userContext.getCurrentUserId();
        DocumentRecord doc = documentRepository.findById(docId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DOC_NOT_FOUND, "文档不存在: " + docId));

        if (!doc.userId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限索引该文档");
        }

        log.info("module=document action=index start userId={} docId={} fileName={}", userId, docId, doc.fileName());
        documentRepository.updateStatus(docId, "INDEXING", 0);

        try {
            String content = documentParserService.parse(Path.of(doc.storagePath()), doc.fileName());
            List<String> chunks = textChunker.split(content);
            if (chunks.isEmpty()) {
                throw new BusinessException(ErrorCode.DOC_INDEX_FAILED, "文档可解析内容为空");
            }

            List<String> vectorLiterals = new ArrayList<>(chunks.size());
            for (String chunk : chunks) {
                List<Double> embedding = modelClient.embedding(chunk);
                List<Double> normalized = VectorUtils.normalizeDimension(embedding, embeddingDimension);
                vectorLiterals.add(VectorUtils.toPgVectorLiteral(normalized));
            }

            documentChunkRepository.deleteByDocumentId(docId);
            documentChunkRepository.batchInsert(docId, userId, chunks, vectorLiterals);
            documentRepository.updateStatus(docId, "INDEXED", chunks.size());
            log.info("module=document action=index result=success userId={} docId={} chunkCount={}",
                    userId, docId, chunks.size());
            return new DocumentIndexResponse(docId, "INDEXED", chunks.size());
        } catch (Exception ex) {
            documentRepository.updateStatus(docId, "FAILED", 0);
            log.error("module=document action=index result=failed userId={} docId={} message={}",
                    userId, docId, ex.getMessage(), ex);
            if (ex instanceof BusinessException be) {
                throw be;
            }
            throw new BusinessException(ErrorCode.DOC_INDEX_FAILED, "文档索引失败：" + ex.getMessage());
        }
    }

    /**
     * 查询当前用户文档列表。
     */
    public List<DocumentView> list() {
        Long userId = userContext.getCurrentUserId();
        return documentRepository.listByUserId(userId)
                .stream()
                .map(it -> new DocumentView(
                        it.id(),
                        it.fileName(),
                        it.contentType(),
                        it.status(),
                        it.chunkCount(),
                        it.createdAt(),
                        it.updatedAt()
                ))
                .toList();
    }

    /**
     * 查看文档原始解析内容（用于前端预览）。
     */
    public DocumentContentResponse content(Long docId, Integer maxChars) {
        Long userId = userContext.getCurrentUserId();
        DocumentRecord doc = documentRepository.findById(docId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DOC_NOT_FOUND, "文档不存在: " + docId));

        if (!doc.userId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限查看该文档");
        }

        int finalMaxChars = (maxChars == null || maxChars <= 0) ? DEFAULT_PREVIEW_MAX_CHARS : maxChars;
        String rawContent = documentParserService.parse(Path.of(doc.storagePath()), doc.fileName());
        int totalChars = rawContent.length();
        boolean truncated = totalChars > finalMaxChars;
        String previewContent = truncated ? rawContent.substring(0, finalMaxChars) : rawContent;

        log.info("module=document action=content result=success userId={} docId={} totalChars={} returnedChars={} truncated={} maxChars={}",
                userId, docId, totalChars, previewContent.length(), truncated, finalMaxChars);

        return new DocumentContentResponse(
                doc.id(),
                doc.fileName(),
                doc.status(),
                previewContent,
                totalChars,
                previewContent.length(),
                truncated
        );
    }
}
