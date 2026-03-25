package com.tidyquestion.backend.doc.dto;

import java.time.OffsetDateTime;

/**
 * 文档列表项。
 */
public record DocumentView(
        Long id,
        String fileName,
        String contentType,
        String status,
        Integer chunkCount,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
