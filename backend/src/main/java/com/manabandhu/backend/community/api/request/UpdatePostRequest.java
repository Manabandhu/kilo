package com.manabandhu.backend.community.api.request;

import jakarta.validation.constraints.NotBlank;

public record UpdatePostRequest(
        @NotBlank String title,
        @NotBlank String body,
        java.util.List<String> tags) {
}
