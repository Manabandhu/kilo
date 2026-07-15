package com.manabandhu.backend.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        Map<String, String> fieldErrors,
        String traceId) {

    public static ApiError of(int status, String code, String message, String path, String traceId) {
        return new ApiError(Instant.now(), status, code, message, path, null, traceId);
    }

    public ApiError withFieldErrors(Map<String, String> fieldErrors) {
        return new ApiError(timestamp, status, code, message, path, fieldErrors, traceId);
    }
}
