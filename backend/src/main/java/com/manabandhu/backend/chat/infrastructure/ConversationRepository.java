package com.manabandhu.backend.chat.infrastructure;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends JpaRepository<ConversationEntity, UUID> {

    @Query(value = """
           select c from ConversationEntity c
           join ConversationMemberEntity m on m.conversationId = c.id
           where m.userId = :userId
           """, countQuery = """
           select count(c) from ConversationEntity c
           join ConversationMemberEntity m on m.conversationId = c.id
           where m.userId = :userId
           """)
    Page<ConversationEntity> findByMember(@Param("userId") UUID userId, Pageable pageable);
}
