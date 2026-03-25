package com.tidyquestion.backend.quiz;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class RuleScoringServiceTest {

    private final RuleScoringService scoringService = new RuleScoringService();

    @Test
    void shouldGiveHighScoreWhenKeyPointsCovered() {
        String answer = "IOC 是控制反转，AOP 通过代理增强方法，事务基于 AOP 和数据库事务。";
        List<String> keyPoints = List.of("IOC", "AOP", "事务");

        int score = scoringService.score(answer, keyPoints);
        Assertions.assertTrue(score >= 70, "命中关键点时应得到较高分");
    }

    @Test
    void shouldGiveZeroWhenAnswerBlank() {
        int score = scoringService.score("   ", List.of("关键点"));
        Assertions.assertEquals(0, score);
    }
}
