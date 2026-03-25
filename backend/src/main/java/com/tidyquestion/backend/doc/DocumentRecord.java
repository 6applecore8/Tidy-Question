package com.tidyquestion.backend.doc;

import java.time.OffsetDateTime;

/**
 * 文档数据模型（数据库映射）。
 */
public record DocumentRecord(
        Long id,
        Long userId,
        String fileName,
        String contentType,
        String storagePath,
        String status,
        Integer chunkCount,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
