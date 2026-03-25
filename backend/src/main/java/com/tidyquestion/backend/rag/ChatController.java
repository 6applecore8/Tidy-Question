package com.tidyquestion.backend.rag;

import com.tidyquestion.backend.common.ApiResponse;
import com.tidyquestion.backend.rag.dto.AskRequest;
import com.tidyquestion.backend.rag.dto.AskResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 问答接口。
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final RagService ragService;

    public ChatController(RagService ragService) {
        this.ragService = ragService;
    }

    /**
     * 非流式问答接口。
     */
    @PostMapping("/ask")
    public ApiResponse<AskResponse> ask(@Valid @RequestBody AskRequest request) {
        return ApiResponse.success(ragService.ask(request.question(), request.topK()));
    }
}
