package com.manabandhu.backend.chat.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateConversationRequest(
        @NotNull String kind,
        String title,
        List<UUID> memberIds) {
}
