package com.tidyquestion.backend.doc.dto;

/**
 * 索引响应。
 */
public record DocumentIndexResponse(Long docId, String status, Integer chunkCount) {
}
