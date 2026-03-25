package com.tidyquestion.backend.rag;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tidyquestion.backend.common.BusinessException;
import com.tidyquestion.backend.common.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 大模型统一客户端。
 *
 * 当前采用阿里百炼 OpenAI 兼容接口，后续可通过 baseUrl 切换兼容服务。
 */
@Component
public class ModelClient {

    private static final Logger log = LoggerFactory.getLogger(ModelClient.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String apiKey;
    private final String chatModel;
    private final String embeddingModel;

    public ModelClient(RestTemplate modelRestTemplate,
                       ObjectMapper objectMapper,
                       @Value("${app.model.base-url}") String baseUrl,
                       @Value("${app.model.api-key}") String apiKey,
                       @Value("${app.model.chat-model}") String chatModel,
                       @Value("${app.model.embedding-model}") String embeddingModel) {
        this.restTemplate = modelRestTemplate;
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
    }

    /**
     * 生成文本向量。
     */
    public List<Double> embedding(String text) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", embeddingModel);
        body.put("input", text);

        JsonNode json = post("/embeddings", body, "embedding");
        JsonNode embeddingNode = json.path("data").path(0).path("embedding");
        if (!embeddingNode.isArray() || embeddingNode.isEmpty()) {
            throw new BusinessException(ErrorCode.EMBEDDING_FAILED, "embedding 响应为空");
        }

        List<Double> vectors = new ArrayList<>();
        embeddingNode.forEach(node -> vectors.add(node.asDouble()));
        return vectors;
    }

    /**
     * 生成对话回答。
     */
    public String chat(String systemPrompt, String userPrompt) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.add(Map.of("role", "user", "content", userPrompt));

        Map<String, Object> body = new HashMap<>();
        body.put("model", chatModel);
        body.put("messages", messages);
        body.put("temperature", 0.3);

        JsonNode json = post("/chat/completions", body, "chat");
        String content = json.path("choices").path(0).path("message").path("content").asText();
        if (content == null || content.isBlank()) {
            throw new BusinessException(ErrorCode.MODEL_CALL_FAILED, "chat 响应为空");
        }
        return content;
    }

    private JsonNode post(String path, Map<String, Object> body, String action) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new BusinessException(ErrorCode.MODEL_CALL_FAILED, "未配置 APP_MODEL_API_KEY");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        String url = baseUrl + path;
        try {
            long start = System.currentTimeMillis();
            String responseBody = restTemplate.postForObject(url, entity, String.class);
            long cost = System.currentTimeMillis() - start;
            log.info("module=model action={} url={} costMs={}", action, path, cost);
            return objectMapper.readTree(responseBody);
        } catch (RestClientException ex) {
            log.error("module=model action={} result=failed message={}", action, ex.getMessage(), ex);
            throw new BusinessException(ErrorCode.MODEL_CALL_FAILED, "模型调用失败：" + ex.getMessage());
        } catch (Exception ex) {
            log.error("module=model action={} result=parseFailed message={}", action, ex.getMessage(), ex);
            throw new BusinessException(ErrorCode.MODEL_CALL_FAILED, "模型响应解析失败：" + ex.getMessage());
        }
    }
}
