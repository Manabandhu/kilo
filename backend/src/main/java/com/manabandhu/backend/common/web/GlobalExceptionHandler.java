package com.manabandhu.backend.common.web;

import com.manabandhu.backend.common.api.ApiError;
import com.manabandhu.backend.common.exception.BusinessException;
import com.manabandhu.backend.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ApiError build(ErrorCode code, int status, String message, HttpServletRequest req, Map<String, String> fieldErrors) {
        String traceId = MDC.get("traceId");
        return ApiError.of(status, code.name(), message, req.getRequestURI(), traceId).withFieldErrors(fieldErrors);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex, HttpServletRequest req) {
        if (ex.getHttpStatus() >= 500) {
            log.error("Business error: {}", ex.getMessage(), ex);
        }
        return ResponseEntity.status(ex.getHttpStatus())
                .body(build(ex.getCode(), ex.getHttpStatus(), ex.getMessage(), req, ex.getFieldErrors()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(build(ErrorCode.VALIDATION_ERROR, 400, "Request validation failed", req, fieldErrors));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(build(ErrorCode.FORBIDDEN, 403, "You are not allowed to perform this action", req, null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnknown(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(build(ErrorCode.INTERNAL_ERROR, 500, "An unexpected error occurred", req, null));
    }
}
