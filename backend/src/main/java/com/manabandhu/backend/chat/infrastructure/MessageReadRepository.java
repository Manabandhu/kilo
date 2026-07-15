package com.manabandhu.backend.chat.infrastructure;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageReadRepository extends JpaRepository<MessageReadEntity, UUID> {
    boolean existsByMessageIdAndUserId(UUID messageId, UUID userId);
    void deleteByMessageIdAndUserId(UUID messageId, UUID userId);
}
