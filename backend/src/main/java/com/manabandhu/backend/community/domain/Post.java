package com.manabandhu.backend.community.domain;

import com.manabandhu.backend.common.domain.BaseEntity;
import com.manabandhu.backend.community.infrastructure.ReactionsConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "posts")
public class Post extends BaseEntity {

    @Column(name = "community_id", nullable = false)
    private UUID communityId;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Column
    private String title;

    @Column(nullable = false)
    private String body;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(nullable = false, columnDefinition = "text[] default '{}'")
    private String[] tags = new String[0];

    @Column(nullable = false)
    private boolean pinned;

    @Column(nullable = false, columnDefinition = "jsonb")
    @Convert(converter = ReactionsConverter.class)
    private Map<String, Integer> reactions = Map.of("like", 0, "helpful", 0, "celebrate", 0, "support", 0);

    @Column(nullable = false)
    private String status = "published";

    @Column(name = "search_vector", columnDefinition = "tsvector")
    private String searchVector;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public Post() {}

    public UUID getCommunityId() { return communityId; }
    public void setCommunityId(UUID communityId) { this.communityId = communityId; }
    public UUID getAuthorId() { return authorId; }
    public void setAuthorId(UUID authorId) { this.authorId = authorId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getTags() { return tags; }
    public void setTags(String[] tags) { this.tags = tags; }
    public boolean isPinned() { return pinned; }
    public void setPinned(boolean pinned) { this.pinned = pinned; }
    public Map<String, Integer> getReactions() { return reactions; }
    public void setReactions(Map<String, Integer> reactions) { this.reactions = reactions; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSearchVector() { return searchVector; }
    public void setSearchVector(String searchVector) { this.searchVector = searchVector; }
    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }
    public UUID getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(UUID updatedBy) { this.updatedBy = updatedBy; }
    public Instant getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }
}
