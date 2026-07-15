package com.manabandhu.backend.chat.application;

import com.manabandhu.backend.chat.api.ChatApi;
import com.manabandhu.backend.chat.api.ChatApi.CreateConversationRequest;
import com.manabandhu.backend.chat.api.ChatApi.MessageResponse;
import com.manabandhu.backend.chat.api.ChatApi.SendMessageRequest;
import com.manabandhu.backend.chat.infrastructure.ConversationEntity;
import com.manabandhu.backend.chat.infrastructure.ConversationMemberEntity;
import com.manabandhu.backend.chat.infrastructure.ConversationMemberRepository;
import com.manabandhu.backend.chat.infrastructure.ConversationRepository;
import com.manabandhu.backend.chat.infrastructure.MessageEntity;
import com.manabandhu.backend.chat.infrastructure.MessageReadEntity;
import com.manabandhu.backend.chat.infrastructure.MessageReadRepository;
import com.manabandhu.backend.chat.infrastructure.MessageRepository;
import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.common.exception.BusinessException;
import com.manabandhu.backend.security.Authorization;
import com.manabandhu.backend.security.CurrentUser;
import java.time.Duration;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository memberRepository;
    private final MessageRepository messageRepository;
    private final MessageReadRepository readRepository;

    public ChatService(ConversationRepository conversationRepository, ConversationMemberRepository memberRepository,
                       MessageRepository messageRepository, MessageReadRepository readRepository) {
        this.conversationRepository = conversationRepository;
        this.memberRepository = memberRepository;
        this.messageRepository = messageRepository;
        this.readRepository = readRepository;
    }

    public PagedResult<ChatApi.ConversationResponse> conversations(CurrentUser user, int page, int size) {
        Page<ConversationEntity> p = conversationRepository.findByMember(user.userId(), PageRequest.of(page, size));
        return PagedResult.of(p.getContent().stream()
                .map(c -> ChatApi.toResponse(c, membersOf(c.getId()), null, null)).toList(), page, size, p.getTotalElements());
    }

    public PagedResult<MessageResponse> messages(CurrentUser user, UUID conversationId, int page, int size) {
        requireMember(conversationId, user.userId());
        Page<MessageEntity> p = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId, PageRequest.of(page, size));
        return PagedResult.of(p.getContent().stream().map(ChatApi::toResponse).toList(), page, size, p.getTotalElements());
    }

    @Transactional
    public ChatApi.ConversationResponse create(CurrentUser user, CreateConversationRequest req) {
        ConversationEntity c = new ConversationEntity();
        c.setKind(req.memberIds() != null && req.memberIds().length > 2
                ? ConversationEntity.ConversationKind.group : ConversationEntity.ConversationKind.direct);
        c.setTitle(req.title());
        conversationRepository.save(c);
        UUID[] ids = req.memberIds() == null ? new UUID[]{user.userId()} : req.memberIds();
        for (UUID id : ids) {
            ConversationMemberEntity m = new ConversationMemberEntity();
            m.setConversationId(c.getId());
            m.setUserId(id);
            memberRepository.save(m);
        }
        return ChatApi.toResponse(c, ids, null, null);
    }

    @Transactional
    public MessageResponse send(CurrentUser user, UUID conversationId, SendMessageRequest req) {
        requireMember(conversationId, user.userId());
        MessageEntity m = new MessageEntity();
        m.setConversationId(conversationId);
        m.setSenderId(user.userId());
        m.setBody(req.body());
        m.setAttachments(req.attachments() == null ? "[]" : req.attachments());
        m.setReplyToId(req.replyToId());
        m.setEdited(false);
        return ChatApi.toResponse(messageRepository.save(m));
    }

    @Transactional
    public MessageResponse edit(CurrentUser user, UUID messageId, String body) {
        MessageEntity m = requireMessage(messageId);
        Authorization.requireOwner(user, m.getSenderId());
        if (Duration.between(m.getCreatedAt(), java.time.Instant.now()).toMinutes() > 10) {
            throw BusinessException.businessRule("Edit window expired");
        }
        m.setBody(body);
        m.setEdited(true);
        return ChatApi.toResponse(messageRepository.save(m));
    }

    @Transactional
    public void delete(CurrentUser user, UUID messageId) {
        MessageEntity m = requireMessage(messageId);
        if (!m.getSenderId().equals(user.userId()) && !user.isModerator()) {
            throw BusinessException.forbidden("Not allowed to delete this message");
        }
        messageRepository.delete(m);
    }

    @Transactional
    public void markRead(CurrentUser user, UUID messageId) {
        if (!readRepository.existsByMessageIdAndUserId(messageId, user.userId())) {
            MessageReadEntity r = new MessageReadEntity();
            r.setMessageId(messageId);
            r.setUserId(user.userId());
            readRepository.save(r);
        }
    }

    private UUID[] membersOf(UUID conversationId) {
        return memberRepository.findAll().stream()
                .filter(m -> m.getConversationId().equals(conversationId))
                .map(ConversationMemberEntity::getUserId).toArray(UUID[]::new);
    }

    private void requireMember(UUID conversationId, UUID userId) {
        if (!memberRepository.existsByConversationIdAndUserId(conversationId, userId)) {
            throw BusinessException.forbidden("You are not a member of this conversation");
        }
    }

    private MessageEntity requireMessage(UUID id) {
        return messageRepository.findById(id).orElseThrow(() -> BusinessException.notFound("Message not found"));
    }
}
