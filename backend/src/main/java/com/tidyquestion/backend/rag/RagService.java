package com.tidyquestion.backend.rag;

import com.tidyquestion.backend.doc.ChunkRecord;
import com.tidyquestion.backend.doc.DocumentChunkRepository;
import com.tidyquestion.backend.rag.dto.AskResponse;
import com.tidyquestion.backend.rag.dto.Citation;
import com.tidyquestion.backend.security.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RAG 问答服务。
 *
 * 采用向量 TopK 召回后拼接上下文，再交给大模型生成回答。
 */
@Service
public class RagService {

    private static final Logger log = LoggerFactory.getLogger(RagService.class);

    private static final String SYSTEM_PROMPT = """
            你是一个学习助手，请严格基于给定资料回答。
            要求：
            1) 优先使用资料中的事实，不要编造。
            2) 若资料不足，明确说明“资料中未覆盖”。
            3) 给出结构化、可复习的回答。
            """;

    private final ModelClient modelClient;
    private final DocumentChunkRepository documentChunkRepository;
    private final UserContext userContext;
    private final int embeddingDimension;
    private final int defaultTopK;

    public RagService(ModelClient modelClient,
                      DocumentChunkRepository documentChunkRepository,
                      UserContext userContext,
                      @Value("${app.rag.embedding-dimension}") int embeddingDimension,
                      @Value("${app.rag.top-k}") int defaultTopK) {
        this.modelClient = modelClient;
        this.documentChunkRepository = documentChunkRepository;
        this.userContext = userContext;
        this.embeddingDimension = embeddingDimension;
        this.defaultTopK = defaultTopK;
    }

    /**
     * 基于知识库进行问答。
     */
    public AskResponse ask(String question, Integer topK) {
        long start = System.currentTimeMillis();
        int finalTopK = topK == null || topK <= 0 ? defaultTopK : topK;
        Long userId = userContext.getCurrentUserId();

        List<Double> qEmbedding = modelClient.embedding(question);
        String vector = VectorUtils.toPgVectorLiteral(VectorUtils.normalizeDimension(qEmbedding, embeddingDimension));

        List<ChunkRecord> chunks = documentChunkRepository.searchSimilar(userId, vector, finalTopK);
        if (chunks.isEmpty()) {
            long cost = System.currentTimeMillis() - start;
            log.warn("module=rag action=ask result=noContext userId={} topK={} costMs={}", userId, finalTopK, cost);
            return new AskResponse("当前知识库暂无命中内容。请先上传并索引相关资料。", List.of(), cost, "vector_topk_v1");
        }

        String context = chunks.stream()
                .map(c -> "片段" + c.chunkIndex() + ":\n" + c.content())
                .collect(Collectors.joining("\n\n"));

        String userPrompt = """
                用户问题：
                %s

                检索到的资料片段：
                %s

                请基于以上片段回答，并在最后给出“复习建议”。
                """.formatted(question, context);

        String answer = modelClient.chat(SYSTEM_PROMPT, userPrompt);
        List<Citation> citations = chunks.stream()
                .map(c -> new Citation(c.id(), c.documentId(), c.chunkIndex(), c.content(), c.score()))
                .toList();

        long cost = System.currentTimeMillis() - start;
        log.info("module=rag action=ask result=success userId={} topK={} citations={} costMs={}",
                userId, finalTopK, citations.size(), cost);
        return new AskResponse(answer, citations, cost, "vector_topk_v1");
    }
}
