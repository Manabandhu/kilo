package com.manabandhu.backend.chat.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class ConversationMemberId implements Serializable {

    @Column(name = "conversation_id")
    private UUID conversationId;

    @Column(name = "user_id")
    private UUID userId;

    public ConversationMemberId() {}

    public ConversationMemberId(UUID conversationId, UUID userId) {
        this.conversationId = conversationId;
        this.userId = userId;
    }

    public UUID getConversationId() { return conversationId; }
    public void setConversationId(UUID conversationId) { this.conversationId = conversationId; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
}
