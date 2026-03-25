package com.tidyquestion.backend.security;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * 当前用户上下文。
 *
 * 单用户版本默认返回 1，后续扩展多用户时无需修改业务层方法签名。
 */
@Component
public class UserContext {

    public Long getCurrentUserId() {
        String userId = MDC.get("userId");
        if (userId == null || userId.isBlank()) {
            return 1L;
        }
        try {
            return Long.parseLong(userId);
        } catch (NumberFormatException ex) {
            return 1L;
        }
    }
}
