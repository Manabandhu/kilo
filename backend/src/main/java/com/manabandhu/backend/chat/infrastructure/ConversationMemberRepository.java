package com.manabandhu.backend.chat.infrastructure;

import com.manabandhu.backend.chat.domain.ConversationMember;
import com.manabandhu.backend.chat.domain.ConversationMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationMemberRepository extends JpaRepository<ConversationMember, ConversationMemberId> {
    List<ConversationMember> findById_ConversationId(UUID conversationId);
    Optional<ConversationMember> findById_ConversationIdAndId_UserId(UUID conversationId, UUID userId);
    boolean existsById_ConversationIdAndId_UserId(UUID conversationId, UUID userId);
}
