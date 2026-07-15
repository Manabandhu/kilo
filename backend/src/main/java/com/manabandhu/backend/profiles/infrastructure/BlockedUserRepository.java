package com.manabandhu.backend.profiles.infrastructure;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedUserRepository extends JpaRepository<BlockedUserEntity, UUID> {
    boolean existsByUserIdAndBlockedUserId(UUID userId, UUID blockedUserId);
    void deleteByUserIdAndBlockedUserId(UUID userId, UUID blockedUserId);
}
