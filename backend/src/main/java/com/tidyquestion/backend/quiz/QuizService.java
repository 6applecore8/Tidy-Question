package com.tidyquestion.backend.quiz;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tidyquestion.backend.common.BusinessException;
import com.tidyquestion.backend.common.ErrorCode;
import com.tidyquestion.backend.quiz.dto.AnswerResponse;
import com.tidyquestion.backend.quiz.dto.StartSessionResponse;
import com.tidyquestion.backend.quiz.dto.WeakPointItem;
import com.tidyquestion.backend.rag.ModelClient;
import com.tidyquestion.backend.security.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 八股训练服务。
 *
 * 流程：创建会话 -> 出题 -> 作答 -> LLM评分 + 规则评分 -> 生成下一题。
 */
@Service
public class QuizService {

    private static final Logger log = LoggerFactory.getLogger(QuizService.class);

    private final QuizRepository quizRepository;
    private final ModelClient modelClient;
    private final RuleScoringService ruleScoringService;
    private final ObjectMapper objectMapper;
    private final UserContext userContext;

    public QuizService(QuizRepository quizRepository,
                       ModelClient modelClient,
                       RuleScoringService ruleScoringService,
                       ObjectMapper objectMapper,
                       UserContext userContext) {
        this.quizRepository = quizRepository;
        this.modelClient = modelClient;
        this.ruleScoringService = ruleScoringService;
        this.objectMapper = objectMapper;
        this.userContext = userContext;
    }

    /**
     * 创建训练会话并生成首题。
     */
    public StartSessionResponse startSession(String topic, String difficulty) {
        Long userId = userContext.getCurrentUserId();
        String question = generateQuestion(topic, difficulty, null, null);
        Long sessionId = quizRepository.createSession(userId, topic, difficulty, question);
        log.info("module=quiz action=start result=success sessionId={} userId={} topic={} difficulty={}",
                sessionId, userId, topic, difficulty);
        return new StartSessionResponse(sessionId, topic, difficulty, question, "ACTIVE");
    }

    /**
     * 提交作答并返回评分结果。
     */
    @Transactional
    public AnswerResponse answer(Long sessionId, String userAnswer) {
        Long userId = userContext.getCurrentUserId();
        QuizSessionRecord session = quizRepository.findSession(sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUIZ_SESSION_NOT_FOUND, "训练会话不存在: " + sessionId));
        if (!session.userId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限访问该训练会话");
        }

        EvaluationResult evaluation = evaluateAnswer(session.currentQuestion(), userAnswer);
        int ruleScore = ruleScoringService.score(userAnswer, evaluation.keyPoints());
        int finalScore = (int) Math.round(evaluation.llmScore() * 0.7 + ruleScore * 0.3);

        quizRepository.insertAnswer(
                sessionId,
                userId,
                session.currentQuestion(),
                userAnswer,
                evaluation.llmScore(),
                ruleScore,
                finalScore,
                evaluation.strengths(),
                evaluation.improvements(),
                evaluation.idealAnswer(),
                String.join(" | ", evaluation.keyPoints())
        );

        String nextQuestion = generateQuestion(session.topic(), session.difficulty(), session.currentQuestion(), userAnswer);
        quizRepository.updateCurrentQuestion(sessionId, nextQuestion);

        log.info("module=quiz action=answer result=success sessionId={} userId={} llmScore={} ruleScore={} finalScore={}",
                sessionId, userId, evaluation.llmScore(), ruleScore, finalScore);

        return new AnswerResponse(
                sessionId,
                session.currentQuestion(),
                userAnswer,
                evaluation.llmScore(),
                ruleScore,
                finalScore,
                evaluation.keyPoints(),
                evaluation.strengths(),
                evaluation.improvements(),
                evaluation.idealAnswer(),
                nextQuestion
        );
    }

    public List<WeakPointItem> weakPoints() {
        return quizRepository.findWeakPoints(userContext.getCurrentUserId());
    }

    private String generateQuestion(String topic, String difficulty, String previousQuestion, String previousAnswer) {
        try {
            String systemPrompt = "你是面试官，请围绕给定主题生成一条适合口述回答的八股题，题目清晰、可追问。";
            String userPrompt = """
                    主题：%s
                    难度：%s
                    上一题：%s
                    上一轮回答：%s

                    请只输出一道新题，不要附加解释。
                    """.formatted(
                    topic,
                    difficulty,
                    previousQuestion == null ? "无" : previousQuestion,
                    previousAnswer == null ? "无" : previousAnswer
            );
            return modelClient.chat(systemPrompt, userPrompt).trim();
        } catch (Exception ex) {
            log.warn("module=quiz action=generateQuestion result=fallback topic={} difficulty={} message={}",
                    topic, difficulty, ex.getMessage());
            return "请解释“" + topic + "”的核心概念、实现原理、常见问题及优化思路。";
        }
    }

    /**
     * 让模型输出结构化评分，随后本地解析。
     */
    private EvaluationResult evaluateAnswer(String question, String answer) {
        String systemPrompt = """
                你是严格的技术面试评估助手。请仅返回 JSON，不要输出额外文本。
                JSON 字段：
                llmScore(0-100整数), strengths(字符串), improvements(字符串), idealAnswer(字符串), keyPoints(字符串数组)
                """;

        String userPrompt = """
                问题：%s
                回答：%s

                请给出评估结果。
                """.formatted(question, answer);

        try {
            String raw = modelClient.chat(systemPrompt, userPrompt);
            JsonNode root = extractJson(raw);

            int llmScore = clampScore(root.path("llmScore").asInt(0));
            String strengths = root.path("strengths").asText("回答有一定基础");
            String improvements = root.path("improvements").asText("建议补充关键点并加强结构化表达");
            String idealAnswer = root.path("idealAnswer").asText("");

            List<String> keyPoints = new ArrayList<>();
            JsonNode keyPointNode = root.path("keyPoints");
            if (keyPointNode.isArray()) {
                keyPointNode.forEach(item -> keyPoints.add(item.asText()));
            }

            // 这里不能重新给 keyPoints 赋值，否则它就不是 effectively final，
            // 在上面的 lambda (forEach) 中会触发编译错误。
            if (keyPoints.isEmpty()) {
                keyPoints.addAll(List.of("核心概念", "关键流程", "常见问题", "优化思路"));
            }
            return new EvaluationResult(llmScore, strengths, improvements, idealAnswer, List.copyOf(keyPoints));
        } catch (Exception ex) {
            log.error("module=quiz action=evaluate result=failed message={}", ex.getMessage(), ex);
            throw new BusinessException(ErrorCode.QUIZ_EVALUATION_FAILED, "评分失败：" + ex.getMessage());
        }
    }

    private JsonNode extractJson(String raw) throws Exception {
        String cleaned = raw.trim();
        int start = cleaned.indexOf('{');
        int end = cleaned.lastIndexOf('}');
        if (start >= 0 && end > start) {
            cleaned = cleaned.substring(start, end + 1);
        }
        return objectMapper.readTree(cleaned);
    }

    private int clampScore(int score) {
        return Math.max(0, Math.min(100, score));
    }

    private record EvaluationResult(int llmScore,
                                    String strengths,
                                    String improvements,
                                    String idealAnswer,
                                    List<String> keyPoints) {
    }
}
