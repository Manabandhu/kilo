package com.manabandhu.backend.chat.infrastructure;

import com.manabandhu.backend.chat.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    Page<Message> findByConversationId(UUID conversationId, Pageable pageable);
    List<Message> findByConversationIdOrderByCreatedAtDesc(UUID conversationId);
    List<Message> findByConversationIdAndIdIn(UUID conversationId, List<UUID> ids);
    Optional<Message> findTopByConversationIdOrderByCreatedAtDesc(UUID conversationId);
    long countByConversationId(UUID conversationId);
    @Query(value = "select count(*) from messages m where m.conversation_id = :conversationId and not exists (select 1 from message_reads mr where mr.message_id = m.id and mr.user_id = :userId)", nativeQuery = true)
    long countUnread(@Param("conversationId") UUID conversationId, @Param("userId") UUID userId);
}
