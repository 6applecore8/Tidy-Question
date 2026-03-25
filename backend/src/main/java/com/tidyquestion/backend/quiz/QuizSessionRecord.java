package com.tidyquestion.backend.quiz;

import java.time.OffsetDateTime;

/**
 * 训练会话数据模型。
 */
public record QuizSessionRecord(
        Long id,
        Long userId,
        String topic,
        String difficulty,
        String currentQuestion,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
