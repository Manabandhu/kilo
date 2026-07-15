package com.manabandhu.backend.chat.infrastructure;

import com.manabandhu.backend.chat.api.dto.ConversationDto;
import com.manabandhu.backend.chat.api.dto.ConversationMemberDto;
import com.manabandhu.backend.chat.api.dto.MessageDto;
import com.manabandhu.backend.chat.api.dto.MessageSummaryDto;
import com.manabandhu.backend.chat.domain.Conversation;
import com.manabandhu.backend.chat.domain.ConversationMember;
import com.manabandhu.backend.chat.domain.Message;
import java.util.List;
import java.util.UUID;

public class DtoMapper {

    public static ConversationDto toDto(Conversation c, List<UUID> memberIds, MessageSummaryDto lastMessage, long unreadCount, boolean muted) {
        return new ConversationDto(
                c.getId(), c.getKind(), c.getTitle(), memberIds,
                lastMessage, unreadCount, muted,
                c.getCreatedAt(), c.getUpdatedAt());
    }

    public static MessageSummaryDto toSummaryDto(Message m) {
        return new MessageSummaryDto(m.getId(), m.getBody(), m.getSenderId(), m.getCreatedAt());
    }

    public static MessageDto toDto(Message m, String status) {
        return new MessageDto(
                m.getId(), m.getConversationId(), m.getSenderId(), m.getBody(),
                m.getAttachments(), m.getReplyToId(), m.getReaction(),
                m.isEdited(), status, m.getCreatedAt(), m.getUpdatedAt());
    }

    public static ConversationMemberDto toDto(ConversationMember m) {
        return new ConversationMemberDto(
                m.getId().getConversationId(), m.getId().getUserId(),
                m.getRole(), m.getJoinedAt(), m.isMuted());
    }
}
