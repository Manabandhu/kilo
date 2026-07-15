package com.manabandhu.backend.community.api;

import com.manabandhu.backend.community.infrastructure.CommentEntity;
import com.manabandhu.backend.community.infrastructure.CommunityEntity;
import com.manabandhu.backend.community.infrastructure.PostEntity;
import java.util.UUID;

public final class CommunityApi {

    private CommunityApi() {
    }

    public record CommunityResponse(
            UUID id, String name, String slug, String description, String kind, String category,
            String city, String state, int memberCount, String coverImageUrl, String[] pinnedPostIds) {
    }

    public record CreatePostRequest(String title, String body, String[] tags) {
    }

    public record CreateCommentRequest(UUID parentId, String body) {
    }

    public record PostResponse(
            UUID id, UUID communityId, UUID authorId, String title, String body, String[] tags,
            boolean pinned, String reactions, String status, String createdAt, String updatedAt) {
    }

    public record CommentResponse(
            UUID id, UUID postId, UUID parentId, UUID authorId, String body, int helpfulVotes, String status,
            String createdAt, String updatedAt) {
    }

    public static CommunityResponse toResponse(CommunityEntity e) {
        return new CommunityResponse(e.getId(), e.getName(), e.getSlug(), e.getDescription(), e.getKind(),
                e.getCategory(), e.getCity(), e.getState(), e.getMemberCount(), e.getCoverImageUrl(), e.getPinnedPostIds());
    }

    public static PostResponse toResponse(PostEntity e) {
        return new PostResponse(e.getId(), e.getCommunityId(), e.getAuthorId(), e.getTitle(), e.getBody(),
                e.getTags(), e.isPinned(), e.getReactions(), e.getStatus().name(),
                e.getCreatedAt().toString(), e.getUpdatedAt().toString());
    }

    public static CommentResponse toResponse(CommentEntity e) {
        return new CommentResponse(e.getId(), e.getPostId(), e.getParentId(), e.getAuthorId(), e.getBody(),
                e.getHelpfulVotes(), e.getStatus().name(), e.getCreatedAt().toString(), e.getUpdatedAt().toString());
    }
}
