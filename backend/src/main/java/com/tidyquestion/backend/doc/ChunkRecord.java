package com.tidyquestion.backend.doc;

/**
 * 分块检索记录。
 */
public record ChunkRecord(
        Long id,
        Long documentId,
        Integer chunkIndex,
        String content,
        Double score
) {
}
