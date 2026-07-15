package com.manabandhu.backend.common.exception;

import java.util.Map;

public class BusinessException extends RuntimeException {
    private final ErrorCode code;
    private final int httpStatus;
    private final transient Map<String, String> fieldErrors;

    public BusinessException(ErrorCode code, int httpStatus, String message) {
        this(code, httpStatus, message, null);
    }

    public BusinessException(ErrorCode code, int httpStatus, String message, Map<String, String> fieldErrors) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
        this.fieldErrors = fieldErrors;
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(ErrorCode.NOT_FOUND, 404, message);
    }

    public static BusinessException forbidden(String message) {
        return new BusinessException(ErrorCode.FORBIDDEN, 403, message);
    }

    public static BusinessException conflict(String message) {
        return new BusinessException(ErrorCode.CONFLICT, 409, message);
    }

    public static BusinessException validation(String message, Map<String, String> fieldErrors) {
        return new BusinessException(ErrorCode.VALIDATION_ERROR, 400, message, fieldErrors);
    }

    public static BusinessException businessRule(String message) {
        return new BusinessException(ErrorCode.BUSINESS_RULE_VIOLATION, 422, message);
    }

    public static BusinessException rateLimited() {
        return new BusinessException(ErrorCode.RATE_LIMITED, 429, "Too many requests");
    }

    public static BusinessException unauthorized(String message) {
        return new BusinessException(ErrorCode.UNAUTHORIZED, 401, message);
    }

    public ErrorCode getCode() {
        return code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
