package com.manabandhu.backend.chat.domain;

import com.manabandhu.backend.common.domain.BaseEntity;
import com.manabandhu.backend.chat.infrastructure.AttachmentsConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "messages")
public class Message extends BaseEntity {

    @Column(name = "conversation_id", nullable = false)
    private UUID conversationId;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(nullable = false)
    private String body;

    @Column(nullable = false, columnDefinition = "jsonb")
    @Convert(converter = AttachmentsConverter.class)
    private List<MessageAttachment> attachments = List.of();

    @Column(name = "reply_to_id")
    private UUID replyToId;

    @Column
    private String reaction;

    @Column(nullable = false)
    private boolean edited;

    public Message() {}

    public UUID getConversationId() { return conversationId; }
    public void setConversationId(UUID conversationId) { this.conversationId = conversationId; }
    public UUID getSenderId() { return senderId; }
    public void setSenderId(UUID senderId) { this.senderId = senderId; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public List<MessageAttachment> getAttachments() { return attachments; }
    public void setAttachments(List<MessageAttachment> attachments) { this.attachments = attachments; }
    public UUID getReplyToId() { return replyToId; }
    public void setReplyToId(UUID replyToId) { this.replyToId = replyToId; }
    public String getReaction() { return reaction; }
    public void setReaction(String reaction) { this.reaction = reaction; }
    public boolean isEdited() { return edited; }
    public void setEdited(boolean edited) { this.edited = edited; }
}
