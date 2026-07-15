package com.manabandhu.backend.chat.infrastructure;

import com.manabandhu.backend.chat.domain.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    @Query(value = "select distinct c.* from conversations c join conversation_members cm on cm.conversation_id = c.id where cm.user_id = :userId order by c.updated_at desc", nativeQuery = true)
    Page<Conversation> findByMember(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
        select cm from ConversationMember cm
        where cm.id.conversationId = :conversationId and cm.id.userId = :userId
    """)
    Optional<com.manabandhu.backend.chat.domain.ConversationMember> findMember(@Param("conversationId") UUID conversationId, @Param("userId") UUID userId);
}
