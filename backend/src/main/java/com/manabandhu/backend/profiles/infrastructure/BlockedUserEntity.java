package com.manabandhu.backend.profiles.infrastructure;

import com.manabandhu.backend.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "blocked_users")
public class BlockedUserEntity extends BaseEntity {

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID blockedUserId;

    public UUID getUserId() { return userId; }
    public void setUserId(UUID v) { this.userId = v; }
    public UUID getBlockedUserId() { return blockedUserId; }
    public void setBlockedUserId(UUID v) { this.blockedUserId = v; }
}
