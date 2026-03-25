package com.tidyquestion.backend.rag.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 问答请求。
 */
public record AskRequest(
        @NotBlank(message = "问题不能为空") String question,
        Integer topK
) {
}
