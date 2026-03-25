package com.tidyquestion.backend.quiz.dto;

import java.util.List;

/**
 * 作答评分响应。
 */
public record AnswerResponse(
        Long sessionId,
        String question,
        String userAnswer,
        int llmScore,
        int ruleScore,
        int finalScore,
        List<String> keyPoints,
        String strengths,
        String improvements,
        String idealAnswer,
        String nextQuestion
) {
}
