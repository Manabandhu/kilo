package com.manabandhu.backend.chat.api.dto;

import java.time.Instant;
import java.util.UUID;

public record ConversationMemberDto(
        UUID conversationId,
        UUID userId,
        String role,
        Instant joinedAt,
        boolean muted) {
}
