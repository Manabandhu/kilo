package com.manabandhu.backend.community.api.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CommunityDto(
        UUID id,
        String name,
        String slug,
        String description,
        String kind,
        String category,
        String city,
        String state,
        int memberCount,
        String coverImageUrl,
        List<UUID> pinnedPostIds,
        Instant createdAt) {
}
