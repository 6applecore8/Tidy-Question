package com.tidyquestion.backend.common;

import org.slf4j.MDC;

/**
 * 统一响应体，便于前端做统一拦截处理。
 *
 * @param <T> 响应数据类型
 */
public record ApiResponse<T>(int code, String message, T data, String traceId) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "OK", data, getTraceId());
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(0, "OK", null, getTraceId());
    }

    public static ApiResponse<Void> error(ErrorCode errorCode, String message) {
        String msg = (message == null || message.isBlank()) ? errorCode.getMessage() : message;
        return new ApiResponse<>(errorCode.getCode(), msg, null, getTraceId());
    }

    private static String getTraceId() {
        return MDC.get("traceId");
    }
}
