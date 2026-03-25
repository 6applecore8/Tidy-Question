package com.tidyquestion.backend.quiz.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 创建训练会话请求。
 */
public record StartSessionRequest(
        @NotBlank(message = "主题不能为空") String topic,
        @NotBlank(message = "难度不能为空") String difficulty
) {
}
