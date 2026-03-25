package com.tidyquestion.backend.rag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * 模型调用 HTTP 客户端配置。
 */
@Configuration
public class ModelClientConfig {

    @Bean
    public RestTemplate modelRestTemplate(RestTemplateBuilder builder,
                                          @Value("${app.model.timeout-seconds}") int timeoutSeconds) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(timeoutSeconds))
                .setReadTimeout(Duration.ofSeconds(timeoutSeconds))
                .build();
    }
}
