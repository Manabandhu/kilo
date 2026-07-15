package com.manabandhu.backend.community.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "community_memberships")
public class CommunityMembership {

    @EmbeddedId
    private CommunityMembershipId id;

    @Column(nullable = false)
    private String role;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    public CommunityMembership() {}

    public CommunityMembership(CommunityMembershipId id, String role, Instant joinedAt) {
        this.id = id;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public CommunityMembershipId getId() { return id; }
    public void setId(CommunityMembershipId id) { this.id = id; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Instant getJoinedAt() { return joinedAt; }
    public void setJoinedAt(Instant joinedAt) { this.joinedAt = joinedAt; }
}
