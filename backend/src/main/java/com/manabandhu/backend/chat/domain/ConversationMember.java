package com.manabandhu.backend.chat.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "conversation_members")
public class ConversationMember {

    @EmbeddedId
    private ConversationMemberId id;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private boolean muted;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    public ConversationMember() {}

    public ConversationMember(ConversationMemberId id, String role, boolean muted, Instant joinedAt) {
        this.id = id;
        this.role = role;
        this.muted = muted;
        this.joinedAt = joinedAt;
    }

    public ConversationMemberId getId() { return id; }
    public void setId(ConversationMemberId id) { this.id = id; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isMuted() { return muted; }
    public void setMuted(boolean muted) { this.muted = muted; }
    public Instant getJoinedAt() { return joinedAt; }
    public void setJoinedAt(Instant joinedAt) { this.joinedAt = joinedAt; }
}
