package com.manabandhu.backend.community.domain;

import com.manabandhu.backend.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {

    @Column(name = "post_id", nullable = false)
    private UUID postId;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Column(nullable = false)
    private String body;

    @Column(name = "helpful_votes", nullable = false)
    private int helpfulVotes;

    @Column(nullable = false)
    private String status = "published";

    public Comment() {}

    public UUID getPostId() { return postId; }
    public void setPostId(UUID postId) { this.postId = postId; }
    public UUID getParentId() { return parentId; }
    public void setParentId(UUID parentId) { this.parentId = parentId; }
    public UUID getAuthorId() { return authorId; }
    public void setAuthorId(UUID authorId) { this.authorId = authorId; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public int getHelpfulVotes() { return helpfulVotes; }
    public void setHelpfulVotes(int helpfulVotes) { this.helpfulVotes = helpfulVotes; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
