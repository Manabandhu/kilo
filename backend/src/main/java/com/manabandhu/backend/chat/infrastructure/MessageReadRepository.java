package com.manabandhu.backend.chat.infrastructure;

import com.manabandhu.backend.chat.domain.MessageRead;
import com.manabandhu.backend.chat.domain.MessageReadId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface MessageReadRepository extends JpaRepository<MessageRead, MessageReadId> {
    boolean existsById_MessageIdAndId_UserId(UUID messageId, UUID userId);
    long countById_MessageId(UUID messageId);
    Optional<MessageRead> findById_MessageIdAndId_UserId(UUID messageId, UUID userId);
}
