package com.tidyquestion.backend.rag.dto;

/**
 * 引用片段。
 */
public record Citation(
        Long chunkId,
        Long documentId,
        Integer chunkIndex,
        String content,
        Double score
) {
}
