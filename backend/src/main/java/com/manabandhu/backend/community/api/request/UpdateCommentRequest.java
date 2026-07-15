package com.manabandhu.backend.community.api.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateCommentRequest(
        @NotBlank String body) {
}
