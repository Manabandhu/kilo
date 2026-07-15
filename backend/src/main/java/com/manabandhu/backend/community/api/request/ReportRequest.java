package com.manabandhu.backend.community.api.request;

import jakarta.validation.constraints.NotBlank;

public record ReportRequest(
        @NotBlank String reason,
        String detail) {
}
