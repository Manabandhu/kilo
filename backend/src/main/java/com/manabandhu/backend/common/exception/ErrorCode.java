package com.manabandhu.backend.common.exception;

public enum ErrorCode {
    VALIDATION_ERROR,
    UNAUTHORIZED,
    FORBIDDEN,
    NOT_FOUND,
    CONFLICT,
    RATE_LIMITED,
    IDEMPOTENT_REPLAY,
    BUSINESS_RULE_VIOLATION,
    INTERNAL_ERROR
}
