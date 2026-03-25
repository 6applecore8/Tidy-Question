package com.tidyquestion.backend.quiz;

import com.tidyquestion.backend.common.ApiResponse;
import com.tidyquestion.backend.quiz.dto.AnswerRequest;
import com.tidyquestion.backend.quiz.dto.AnswerResponse;
import com.tidyquestion.backend.quiz.dto.StartSessionRequest;
import com.tidyquestion.backend.quiz.dto.StartSessionResponse;
import com.tidyquestion.backend.quiz.dto.WeakPointItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 八股训练与复盘接口。
 */
@Validated
@RestController
@RequestMapping("/api")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * 创建训练会话。
     */
    @PostMapping("/quiz/session/start")
    public ApiResponse<StartSessionResponse> start(@Valid @RequestBody StartSessionRequest request) {
        return ApiResponse.success(quizService.startSession(request.topic(), request.difficulty()));
    }

    /**
     * 提交作答并评分。
     */
    @PostMapping("/quiz/session/{id}/answer")
    public ApiResponse<AnswerResponse> answer(@PathVariable("id") @Min(value = 1, message = "sessionId 不合法") Long sessionId,
                                              @Valid @RequestBody AnswerRequest request) {
        return ApiResponse.success(quizService.answer(sessionId, request.answer()));
    }

    /**
     * 获取薄弱点统计。
     */
    @GetMapping("/review/weak-points")
    public ApiResponse<List<WeakPointItem>> weakPoints() {
        return ApiResponse.success(quizService.weakPoints());
    }
}
