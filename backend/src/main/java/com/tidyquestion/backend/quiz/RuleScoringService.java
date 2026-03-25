package com.tidyquestion.backend.quiz;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 规则评分器。
 *
 * 评分目标不是取代 LLM，而是给出稳定基线，减少纯模型打分波动。
 */
@Component
public class RuleScoringService {

    /**
     * 基于关键点命中率和回答长度进行规则评分。
     */
    public int score(String answer, List<String> keyPoints) {
        if (answer == null || answer.isBlank()) {
            return 0;
        }

        String normalizedAnswer = normalize(answer);
        int base = Math.min(30, normalizedAnswer.length() / 10);

        if (keyPoints == null || keyPoints.isEmpty()) {
            return clamp(base + 30);
        }

        int hit = 0;
        for (String keyPoint : keyPoints) {
            if (keyPoint == null || keyPoint.isBlank()) {
                continue;
            }
            if (normalizedAnswer.contains(normalize(keyPoint))) {
                hit++;
            }
        }

        double ratio = (double) hit / keyPoints.size();
        int keywordScore = (int) Math.round(ratio * 70);
        return clamp(base + keywordScore);
    }

    private int clamp(int score) {
        return Math.max(0, Math.min(100, score));
    }

    private String normalize(String value) {
        return value.replaceAll("\\s+", "").toLowerCase();
    }
}
