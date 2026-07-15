package com.manabandhu.backend.chat.api.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ConversationDto(
        UUID id,
        String kind,
        String title,
        List<UUID> memberIds,
        MessageSummaryDto lastMessage,
        long unreadCount,
        boolean muted,
        Instant createdAt,
        Instant updatedAt) {
}
