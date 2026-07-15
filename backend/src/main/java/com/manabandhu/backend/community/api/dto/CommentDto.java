package com.manabandhu.backend.community.api.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CommentDto(
        UUID id,
        UUID postId,
        UUID parentId,
        UUID authorId,
        String authorName,
        String body,
        int helpfulVotes,
        String status,
        Instant createdAt,
        Instant updatedAt,
        List<CommentDto> replies) {
}
