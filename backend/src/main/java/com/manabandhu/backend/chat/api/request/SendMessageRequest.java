package com.manabandhu.backend.chat.api.request;

import jakarta.validation.constraints.NotBlank;

public record SendMessageRequest(
        @NotBlank String body,
        UUID replyToId,
        List<MessageAttachment> attachments) {
}
