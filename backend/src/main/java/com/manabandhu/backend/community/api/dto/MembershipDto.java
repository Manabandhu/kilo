package com.manabandhu.backend.community.api.dto;

import java.time.Instant;
import java.util.UUID;

public record MembershipDto(
        UUID communityId,
        UUID userId,
        Instant joinedAt,
        String role) {
}
