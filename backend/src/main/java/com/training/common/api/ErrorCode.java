package com.training.common.api;

/**
 * 统一错误码定义
 */
public enum ErrorCode {
    SUCCESS(0, "success"),
    VALIDATE_FAILED(1001, "参数校验失败"),
    BUSINESS_CONFLICT(3001, "业务冲突"),
    NOT_FOUND(3004, "资源不存在"),
    SERVER_ERROR(1000, "系统异常");

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

