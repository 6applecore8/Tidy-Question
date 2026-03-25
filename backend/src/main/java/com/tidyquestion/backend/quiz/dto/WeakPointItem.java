package com.tidyquestion.backend.quiz.dto;

/**
 * 薄弱点统计项。
 */
public record WeakPointItem(String topic, double avgScore, long attemptCount, String suggestion) {
}
