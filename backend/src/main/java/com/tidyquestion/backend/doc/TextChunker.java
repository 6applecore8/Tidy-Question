package com.tidyquestion.backend.doc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 文本分块器。
 *
 * 设计目标：
 * 1. 避免一次传给模型的上下文过大；
 * 2. 通过 overlap 保留跨段语义连续性。
 */
@Component
public class TextChunker {

    private final int chunkSize;
    private final int chunkOverlap;

    public TextChunker(@Value("${app.rag.chunk-size}") int chunkSize,
                       @Value("${app.rag.chunk-overlap}") int chunkOverlap) {
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
    }

    /**
     * 按字符窗口切分文本。
     */
    public List<String> split(String text) {
        String normalized = text == null ? "" : text.replace("\r\n", "\n").trim();
        List<String> result = new ArrayList<>();
        if (normalized.isBlank()) {
            return result;
        }

        int start = 0;
        int length = normalized.length();
        while (start < length) {
            int end = Math.min(start + chunkSize, length);
            String chunk = normalized.substring(start, end).trim();
            if (!chunk.isBlank()) {
                result.add(chunk);
            }
            if (end == length) {
                break;
            }
            start = Math.max(0, end - chunkOverlap);
        }
        return result;
    }
}
