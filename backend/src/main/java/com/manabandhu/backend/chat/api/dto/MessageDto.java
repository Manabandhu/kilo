package com.manabandhu.backend.chat.api.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto(
        UUID id,
        UUID conversationId,
        UUID senderId,
        String body,
        List<MessageAttachment> attachments,
        UUID replyToId,
        String reaction,
        boolean edited,
        String status,
        Instant createdAt,
        Instant updatedAt) {
}
