package com.manabandhu.backend.community.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class CommunityMembershipId implements Serializable {

    @Column(name = "community_id")
    private UUID communityId;

    @Column(name = "user_id")
    private UUID userId;

    public CommunityMembershipId() {}

    public CommunityMembershipId(UUID communityId, UUID userId) {
        this.communityId = communityId;
        this.userId = userId;
    }

    public UUID getCommunityId() { return communityId; }
    public void setCommunityId(UUID communityId) { this.communityId = communityId; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
}
