package com.tidyquestion.backend.doc.dto;

/**
 * Document content preview response.
 */
public record DocumentContentResponse(
        Long docId,
        String fileName,
        String status,
        String content,
        Integer totalChars,
        Integer returnedChars,
        Boolean truncated
) {
}
