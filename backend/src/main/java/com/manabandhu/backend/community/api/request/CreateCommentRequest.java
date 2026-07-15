package com.manabandhu.backend.community.api.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(
        UUID parentId,
        @NotBlank String body) {
}
