package com.tidyquestion.backend.doc;

import com.tidyquestion.backend.common.BusinessException;
import com.tidyquestion.backend.common.ErrorCode;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * 文档解析器：支持 PDF、DOCX 和 Markdown/TXT。
 */
@Component
public class DocumentParserService {

    /**
     * 解析文件内容为纯文本。
     */
    public String parse(Path filePath, String fileName) {
        String lower = fileName.toLowerCase(Locale.ROOT);
        try {
            if (lower.endsWith(".pdf")) {
                return parsePdf(filePath);
            }
            if (lower.endsWith(".docx")) {
                return parseDocx(filePath);
            }
            if (lower.endsWith(".md") || lower.endsWith(".markdown") || lower.endsWith(".txt")) {
                return Files.readString(filePath);
            }
            throw new BusinessException(ErrorCode.DOC_INDEX_FAILED, "暂不支持该文件格式：" + fileName);
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.DOC_INDEX_FAILED, "文件解析失败：" + ex.getMessage());
        }
    }

    private String parsePdf(Path filePath) throws IOException {
        try (PDDocument document = Loader.loadPDF(filePath.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String parseDocx(Path filePath) throws IOException {
        try (InputStream inputStream = Files.newInputStream(filePath);
             XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }
}
