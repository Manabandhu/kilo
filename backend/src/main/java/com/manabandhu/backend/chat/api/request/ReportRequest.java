package com.manabandhu.backend.chat.api.request;

import jakarta.validation.constraints.NotBlank;

public record ReportRequest(
        @NotBlank String reason,
        String detail) {
}
