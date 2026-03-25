package com.tidyquestion.backend.auth.dto;

/**
 * 登录响应。
 */
public record LoginResponse(Long userId, String username, String token, long expireHours) {
}
