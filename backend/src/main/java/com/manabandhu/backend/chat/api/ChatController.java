package com.manabandhu.backend.chat.api;

import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.chat.application.ConversationService;
import com.manabandhu.backend.chat.application.MessageService;
import com.manabandhu.backend.chat.api.dto.ConversationDto;
import com.manabandhu.backend.chat.api.dto.MessageDto;
import com.manabandhu.backend.chat.api.request.CreateConversationRequest;
import com.manabandhu.backend.chat.api.request.SendMessageRequest;
import com.manabandhu.backend.chat.api.request.ReportRequest;
import com.manabandhu.backend.chat.api.request.UpdateMessageRequest;
import com.manabandhu.backend.security.Authorization;
import com.manabandhu.backend.security.AuthenticatedUser;
import com.manabandhu.backend.security.CurrentUser;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/conversations")
public class ChatController {

    private final ConversationService conversationService;
    private final MessageService messageService;

    public ChatController(ConversationService conversationService, MessageService messageService) {
        this.conversationService = conversationService;
        this.messageService = messageService;
    }

    @GetMapping
    public ResponseEntity<PagedResult<ConversationDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticatedUser CurrentUser user) {
        Authorization.requireActiveAccount(user);
        var result = conversationService.listConversations(user, page, size);
        return ResponseEntity.ok(PagedResult.of(result.getContent(), page, size, result.getTotalElements()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversationDto> get(@PathVariable UUID id, @AuthenticatedUser CurrentUser user) {
        Authorization.requireActiveAccount(user);
        return ResponseEntity.ok(conversationService.getConversation(id, user));
    }

    @PostMapping
    public ResponseEntity<ConversationDto> create(@Valid @RequestBody CreateConversationRequest req, @AuthenticatedUser CurrentUser user) {
        Authorization.requireActiveAccount(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(conversationService.createConversation(req, user));
    }

    @PostMapping("/{id}/mute")
    public ResponseEntity<Void> mute(@PathVariable UUID id, @AuthenticatedUser CurrentUser user) {
        Authorization.requireActiveAccount(user);
        conversationService.muteConversation(id, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/report")
    public ResponseEntity<Void> report(@PathVariable UUID id, @Valid @RequestBody ReportRequest req, @AuthenticatedUser CurrentUser user) {
        Authorization.requireActiveAccount(user);
        conversationService.reportConversation(id, req, user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<PagedResult<MessageDto>> messages(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @AuthenticatedUser CurrentUser user) {
        Authorization.requireActiveAccount(user);
        var result = messageService.listMessages(id, page, size, user);
        return ResponseEntity.ok(PagedResult.of(result.getContent(), page, size, result.getTotalElements()));
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<MessageDto> sendMessage(@PathVariable UUID id, @Valid @RequestBody SendMessageRequest req, @AuthenticatedUser CurrentUser user) {
        Authorization.requireActiveAccount(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.sendMessage(id, req, user));
    }

    @PatchMapping("/messages/{id}")
    public ResponseEntity<MessageDto> updateMessage(@PathVariable UUID id, @Valid @RequestBody UpdateMessageRequest req, @AuthenticatedUser CurrentUser user) {
        Authorization.requireActiveAccount(user);
        return ResponseEntity.ok(messageService.updateMessage(id, req, user));
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID id, @AuthenticatedUser CurrentUser user) {
        Authorization.requireActiveAccount(user);
        messageService.deleteMessage(id, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/messages/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable UUID id, @AuthenticatedUser CurrentUser user) {
        Authorization.requireActiveAccount(user);
        messageService.markRead(id, user);
        return ResponseEntity.ok().build();
    }
}
