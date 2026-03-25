package com.tidyquestion.backend.quiz.dto;

/**
 * 创建训练会话响应。
 */
public record StartSessionResponse(Long sessionId, String topic, String difficulty, String question, String status) {
}
