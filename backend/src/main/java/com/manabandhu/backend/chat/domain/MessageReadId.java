package com.manabandhu.backend.chat.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class MessageReadId implements Serializable {

    @Column(name = "message_id")
    private UUID messageId;

    @Column(name = "user_id")
    private UUID userId;

    public MessageReadId() {}

    public MessageReadId(UUID messageId, UUID userId) {
        this.messageId = messageId;
        this.userId = userId;
    }

    public UUID getMessageId() { return messageId; }
    public void setMessageId(UUID messageId) { this.messageId = messageId; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
}
