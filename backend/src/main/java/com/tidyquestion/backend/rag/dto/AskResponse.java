package com.tidyquestion.backend.rag.dto;

import java.util.List;

/**
 * 问答响应。
 */
public record AskResponse(String answer, List<Citation> citations, long costMs, String retrievalMode) {
}
