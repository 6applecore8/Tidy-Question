package com.tidyquestion.backend.common;

/**
 * 统一错误码定义，避免前后端对错误语义理解不一致。
 */
public enum ErrorCode {
    BAD_REQUEST(1001, "请求参数错误"),
    UNAUTHORIZED(1002, "未授权访问"),
    FORBIDDEN(1003, "禁止访问"),

    DOC_UPLOAD_FAILED(2001, "文档上传失败"),
    DOC_INDEX_FAILED(2002, "文档索引失败"),
    DOC_NOT_FOUND(2003, "文档不存在"),

    MODEL_CALL_FAILED(3001, "模型调用失败"),
    EMBEDDING_FAILED(3002, "向量生成失败"),

    QUIZ_SESSION_NOT_FOUND(4001, "训练会话不存在"),
    QUIZ_EVALUATION_FAILED(4002, "训练评分失败"),

    INTERNAL_ERROR(9000, "系统内部错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
