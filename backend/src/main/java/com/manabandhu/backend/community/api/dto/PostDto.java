package com.manabandhu.backend.community.api.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record PostDto(
        UUID id,
        UUID communityId,
        UUID authorId,
        String authorName,
        String title,
        String body,
        List<String> tags,
        boolean pinned,
        boolean saved,
        Map<String, Integer> reactions,
        String userReaction,
        long commentCount,
        String status,
        Instant createdAt,
        Instant updatedAt) {
}
