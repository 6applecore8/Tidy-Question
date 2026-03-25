package com.tidyquestion.backend.quiz;

import com.tidyquestion.backend.quiz.dto.WeakPointItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 八股训练数据访问层。
 */
@Repository
public class QuizRepository {

    private static final RowMapper<QuizSessionRecord> SESSION_MAPPER = (rs, rowNum) -> new QuizSessionRecord(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getString("topic"),
            rs.getString("difficulty"),
            rs.getString("current_question"),
            rs.getString("status"),
            rs.getObject("created_at", OffsetDateTime.class),
            rs.getObject("updated_at", OffsetDateTime.class)
    );

    private final JdbcTemplate jdbcTemplate;

    public QuizRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long createSession(Long userId, String topic, String difficulty, String question) {
        Long sessionId = jdbcTemplate.queryForObject(
                """
                        INSERT INTO quiz_session(user_id, topic, difficulty, current_question, status)
                        VALUES (?, ?, ?, ?, ?)
                        RETURNING id
                        """,
                Long.class,
                userId,
                topic,
                difficulty,
                question,
                "ACTIVE"
        );
        if (sessionId == null) {
            throw new IllegalStateException("Insert quiz_session succeeded but no id returned");
        }
        return sessionId;
    }

    public Optional<QuizSessionRecord> findSession(Long sessionId) {
        List<QuizSessionRecord> sessions = jdbcTemplate.query(
                "SELECT * FROM quiz_session WHERE id = ?",
                SESSION_MAPPER,
                sessionId
        );
        return sessions.stream().findFirst();
    }

    public void updateCurrentQuestion(Long sessionId, String question) {
        jdbcTemplate.update(
                "UPDATE quiz_session SET current_question = ?, updated_at = NOW() WHERE id = ?",
                question,
                sessionId
        );
    }

    /**
     * 保存作答评分记录。
     */
    public void insertAnswer(Long sessionId,
                             Long userId,
                             String question,
                             String answer,
                             int llmScore,
                             int ruleScore,
                             int finalScore,
                             String strengths,
                             String improvements,
                             String idealAnswer,
                             String keyPoints) {
        jdbcTemplate.update(
                """
                        INSERT INTO quiz_answer(
                            session_id, user_id, question, user_answer,
                            llm_score, rule_score, final_score,
                            strengths, improvements, ideal_answer, key_points
                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                sessionId,
                userId,
                question,
                answer,
                llmScore,
                ruleScore,
                finalScore,
                strengths,
                improvements,
                idealAnswer,
                keyPoints
        );
    }

    /**
     * 聚合低分主题作为薄弱点。
     */
    public List<WeakPointItem> findWeakPoints(Long userId) {
        return jdbcTemplate.query(
                """
                        SELECT s.topic,
                               ROUND(AVG(a.final_score)::numeric, 2) AS avg_score,
                               COUNT(*) AS attempt_count,
                               CASE
                                   WHEN AVG(a.final_score) < 50 THEN '建议先回到基础概念，整理核心定义与流程图'
                                   WHEN AVG(a.final_score) < 70 THEN '建议补齐关键点并进行口述训练'
                                   ELSE '建议增加深挖追问训练，提升表达完整度'
                               END AS suggestion
                        FROM quiz_answer a
                        JOIN quiz_session s ON a.session_id = s.id
                        WHERE a.user_id = ?
                        GROUP BY s.topic
                        HAVING AVG(a.final_score) < 80
                        ORDER BY avg_score ASC, attempt_count DESC
                        LIMIT 20
                        """,
                (rs, rowNum) -> new WeakPointItem(
                        rs.getString("topic"),
                        rs.getDouble("avg_score"),
                        rs.getLong("attempt_count"),
                        rs.getString("suggestion")
                ),
                userId
        );
    }
}
