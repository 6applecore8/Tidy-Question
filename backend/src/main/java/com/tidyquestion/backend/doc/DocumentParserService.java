package com.tidyquestion.backend.doc;

import com.tidyquestion.backend.common.BusinessException;
import com.tidyquestion.backend.common.ErrorCode;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

    /**
     * DOCX 实现为 OpenXML zip + document.xml 提取，避免运行时依赖缺失导致索引失败。
     */
    private String parseDocx(Path filePath) throws IOException {
        String xml = readDocxMainDocumentXml(filePath);
        if (xml == null || xml.isBlank()) {
            return "";
        }
        try {
            return extractTextFromDocxXml(xml);
        } catch (ParserConfigurationException | SAXException ex) {
            throw new IOException("DOCX XML 解析失败：" + ex.getMessage(), ex);
        }
    }

    private String readDocxMainDocumentXml(Path filePath) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(filePath), StandardCharsets.UTF_8)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if ("word/document.xml".equals(entry.getName())) {
                    byte[] bytes = zipInputStream.readAllBytes();
                    return new String(bytes, StandardCharsets.UTF_8);
                }
            }
        }
        return null;
    }

    private String extractTextFromDocxXml(String xml)
            throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xml)));

        NodeList paragraphs = document.getElementsByTagNameNS("*", "p");
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < paragraphs.getLength(); i++) {
            Node paragraph = paragraphs.item(i);
            if (paragraph instanceof Element paragraphElement) {
                appendParagraphText(paragraphElement, text);
                if (i < paragraphs.getLength() - 1) {
                    text.append(System.lineSeparator());
                }
            }
        }

        return text.toString().trim();
    }

    private void appendParagraphText(Element paragraphElement, StringBuilder out) {
        NodeList children = paragraphElement.getElementsByTagNameNS("*", "*");
        for (int j = 0; j < children.getLength(); j++) {
            Node node = children.item(j);
            String localName = node.getLocalName();
            if ("t".equals(localName)) {
                out.append(node.getTextContent());
            } else if ("tab".equals(localName)) {
                out.append('\t');
            } else if ("br".equals(localName) || "cr".equals(localName)) {
                out.append(System.lineSeparator());
            }
        }
    }
}
