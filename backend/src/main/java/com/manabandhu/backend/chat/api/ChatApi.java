package com.manabandhu.backend.chat.api;

import com.manabandhu.backend.chat.infrastructure.ConversationEntity;
import com.manabandhu.backend.chat.infrastructure.MessageEntity;
import java.util.UUID;

public final class ChatApi {

    private ChatApi() {
    }

    public record ConversationResponse(
            UUID id, String kind, String title, UUID[] memberIds, String lastBody, UUID lastSenderId,
            String createdAt, String updatedAt) {
    }

    public record MessageResponse(
            UUID id, UUID conversationId, UUID senderId, String body, String attachments,
            UUID replyToId, String reaction, boolean edited, String createdAt, String updatedAt) {
    }

    public record CreateConversationRequest(String title, UUID[] memberIds) {
    }

    public record SendMessageRequest(String body, UUID replyToId, String attachments) {
    }

    public static ConversationResponse toResponse(ConversationEntity c, UUID[] memberIds, String lastBody, UUID lastSenderId) {
        return new ConversationResponse(c.getId(), c.getKind().name(), c.getTitle(), memberIds, lastBody, lastSenderId,
                c.getCreatedAt().toString(), c.getUpdatedAt().toString());
    }

    public static MessageResponse toResponse(MessageEntity m) {
        return new MessageResponse(m.getId(), m.getConversationId(), m.getSenderId(), m.getBody(),
                m.getAttachments(), m.getReplyToId(), m.getReaction(), m.isEdited(),
                m.getCreatedAt().toString(), m.getUpdatedAt().toString());
    }
}
