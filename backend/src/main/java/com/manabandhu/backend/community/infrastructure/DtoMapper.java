package com.manabandhu.backend.community.infrastructure;

import com.manabandhu.backend.community.api.dto.CommentDto;
import com.manabandhu.backend.community.api.dto.MembershipDto;
import com.manabandhu.backend.community.api.dto.PostDto;
import com.manabandhu.backend.community.domain.Community;
import com.manabandhu.backend.community.domain.CommunityMembership;
import com.manabandhu.backend.community.domain.Post;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class DtoMapper {

    public static CommunityDto toDto(Community c) {
        List<UUID> pinned = c.getPinnedPostIds() != null ? List.of(c.getPinnedPostIds()) : List.of();
        return new CommunityDto(
                c.getId(), c.getName(), c.getSlug(), c.getDescription(),
                c.getKind(), c.getCategory(), c.getCity(), c.getState(),
                c.getMemberCount(), c.getCoverImageUrl(), pinned, c.getCreatedAt());
    }

    public static MembershipDto toDto(CommunityMembership m) {
        return new MembershipDto(m.getId().getCommunityId(), m.getId().getUserId(), m.getJoinedAt(), m.getRole());
    }

    public static PostDto toDto(Post p, String authorName, boolean saved, String userReaction, long commentCount) {
        List<String> tagList = p.getTags() != null ? List.of(p.getTags()) : List.of();
        return new PostDto(
                p.getId(), p.getCommunityId(), p.getAuthorId(), authorName,
                p.getTitle(), p.getBody(), tagList,
                p.isPinned(), saved, p.getReactions(), userReaction,
                commentCount, p.getStatus(), p.getCreatedAt(), p.getUpdatedAt());
    }

    public static CommentDto toDto(com.manabandhu.backend.community.domain.Comment c, String authorName, List<CommentDto> replies) {
        return new CommentDto(
                c.getId(), c.getPostId(), c.getParentId(), c.getAuthorId(), authorName,
                c.getBody(), c.getHelpfulVotes(), c.getStatus(),
                c.getCreatedAt(), c.getUpdatedAt(), replies);
    }

    public static String[] formatTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) return new String[0];
        return tags.toArray(new String[0]);
    }
}
