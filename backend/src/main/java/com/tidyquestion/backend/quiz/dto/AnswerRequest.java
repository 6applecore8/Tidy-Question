package com.tidyquestion.backend.quiz.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 训练作答请求。
 */
public record AnswerRequest(@NotBlank(message = "作答内容不能为空") String answer) {
}
