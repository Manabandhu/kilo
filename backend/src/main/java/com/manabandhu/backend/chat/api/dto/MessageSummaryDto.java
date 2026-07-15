package com.manabandhu.backend.chat.api.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageSummaryDto(
        UUID id,
        String body,
        UUID senderId,
        Instant createdAt) {
}
