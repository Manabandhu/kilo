package com.manabandhu.backend.chat.application;

import com.manabandhu.backend.common.application.RateLimiter;
import com.manabandhu.backend.common.exception.BusinessException;
import com.manabandhu.backend.chat.domain.Message;
import com.manabandhu.backend.chat.domain.MessageRead;
import com.manabandhu.backend.chat.domain.MessageReadId;
import com.manabandhu.backend.chat.domain.NewMessageEvent;
import com.manabandhu.backend.chat.infrastructure.MessageReadRepository;
import com.manabandhu.backend.chat.infrastructure.MessageRepository;
import com.manabandhu.backend.chat.infrastructure.DtoMapper;
import com.manabandhu.backend.chat.api.dto.MessageDto;
import com.manabandhu.backend.chat.api.request.SendMessageRequest;
import com.manabandhu.backend.chat.api.request.UpdateMessageRequest;
import com.manabandhu.backend.security.Authorization;
import com.manabandhu.backend.security.CurrentUser;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageReadRepository messageReadRepository;
    private final ConversationService conversationService;
    private final ApplicationEventPublisher eventPublisher;
    private final RateLimiter rateLimiter;

    public MessageService(MessageRepository messageRepository, MessageReadRepository messageReadRepository,
                          ConversationService conversationService, ApplicationEventPublisher eventPublisher, RateLimiter rateLimiter) {
        this.messageRepository = messageRepository;
        this.messageReadRepository = messageReadRepository;
        this.conversationService = conversationService;
        this.eventPublisher = eventPublisher;
        this.rateLimiter = rateLimiter;
    }

    public Page<MessageDto> listMessages(UUID conversationId, int page, int size, CurrentUser user) {
        conversationService.getConversation(conversationId, user);

        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageRepository.findByConversationId(conversationId, pageable);

        return messages.map(msg -> {
            String status = computeStatus(msg, user.userId());
            return DtoMapper.toDto(msg, status);
        });
    }

    public MessageDto sendMessage(UUID conversationId, SendMessageRequest req, CurrentUser user) {
        rateLimiter.check("message-send", user.userId().toString());
        conversationService.getConversation(conversationId, user);

        Message message = new Message();
        message.setConversationId(conversationId);
        message.setSenderId(user.userId());
        message.setBody(req.body());
        message.setAttachments(req.attachments() != null ? req.attachments() : List.of());
        message.setReplyToId(req.replyToId());
        message.setEdited(false);
        message.setCreatedAt(Instant.now());
        message.setUpdatedAt(Instant.now());
        messageRepository.save(message);

        eventPublisher.publishEvent(new NewMessageEvent(this, message.getId(), conversationId, user.userId()));

        return DtoMapper.toDto(message, "sent");
    }

    public MessageDto updateMessage(UUID messageId, UpdateMessageRequest req, CurrentUser user) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> BusinessException.notFound("Message not found"));

        if (!message.getSenderId().equals(user.userId())) {
            throw BusinessException.forbidden("You can only edit your own message");
        }

        long minutesSinceCreated = java.time.Duration.between(message.getCreatedAt(), Instant.now()).toMinutes();
        if (minutesSinceCreated > 10) {
            throw BusinessException.businessRule("Message can only be edited within 10 minutes");
        }

        message.setBody(req.body());
        message.setEdited(true);
        message.setUpdatedAt(Instant.now());
        messageRepository.save(message);
        return DtoMapper.toDto(message, computeStatus(message, user.userId()));
    }

    public void deleteMessage(UUID messageId, CurrentUser user) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> BusinessException.notFound("Message not found"));

        if (!message.getSenderId().equals(user.userId()) && !user.isModerator()) {
            throw BusinessException.forbidden("You can only delete your own message");
        }

        messageRepository.delete(message);
    }

    public void markRead(UUID messageId, CurrentUser user) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> BusinessException.notFound("Message not found"));

        conversationService.getConversation(message.getConversationId(), user);

        if (messageReadRepository.existsById_MessageIdAndId_UserId(messageId, user.userId())) {
            return;
        }

        MessageRead read = new MessageRead(new MessageReadId(messageId, user.userId()), Instant.now());
        messageReadRepository.save(read);
    }

    private String computeStatus(Message message, UUID userId) {
        if (message.getSenderId().equals(userId)) {
            return messageReadRepository.countById_MessageId(message.getId()) > 0 ? "read" : "sent";
        }
        return messageReadRepository.existsById_MessageIdAndId_UserId(message.getId(), userId) ? "read" : "delivered";
    }
}
