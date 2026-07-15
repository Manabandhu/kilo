package com.manabandhu.backend.chat.api;

import com.manabandhu.backend.chat.api.ChatApi.ConversationResponse;
import com.manabandhu.backend.chat.api.ChatApi.CreateConversationRequest;
import com.manabandhu.backend.chat.api.ChatApi.MessageResponse;
import com.manabandhu.backend.chat.api.ChatApi.SendMessageRequest;
import com.manabandhu.backend.chat.application.ChatService;
import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.moderation.api.ReportApi.CreateReportRequest;
import com.manabandhu.backend.moderation.api.ReportApi.ReportResponse;
import com.manabandhu.backend.moderation.application.ReportService;
import com.manabandhu.backend.security.AuthenticatedUser;
import com.manabandhu.backend.security.CurrentUser;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ChatController {

    private final ChatService chatService;
    private final ReportService reportService;

    public ChatController(ChatService chatService, ReportService reportService) {
        this.chatService = chatService;
        this.reportService = reportService;
    }

    @GetMapping("/conversations")
    public PagedResult<ConversationResponse> list(@AuthenticatedUser CurrentUser user,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        return chatService.conversations(user, page, size);
    }

    @PostMapping("/conversations")
    public ConversationResponse create(@AuthenticatedUser CurrentUser user, @Valid @RequestBody CreateConversationRequest req) {
        return chatService.create(user, req);
    }

    @GetMapping("/conversations/{id}/messages")
    public PagedResult<MessageResponse> messages(@AuthenticatedUser CurrentUser user, @PathVariable UUID id,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "50") int size) {
        return chatService.messages(user, id, page, size);
    }

    @PostMapping("/conversations/{id}/messages")
    public MessageResponse send(@AuthenticatedUser CurrentUser user, @PathVariable UUID id, @Valid @RequestBody SendMessageRequest req) {
        return chatService.send(user, id, req);
    }

    @PutMapping("/messages/{id}")
    public MessageResponse edit(@AuthenticatedUser CurrentUser user, @PathVariable UUID id, @RequestParam String body) {
        return chatService.edit(user, id, body);
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity<Void> delete(@AuthenticatedUser CurrentUser user, @PathVariable UUID id) {
        chatService.delete(user, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/messages/{id}/read")
    public ResponseEntity<Void> read(@AuthenticatedUser CurrentUser user, @PathVariable UUID id) {
        chatService.markRead(user, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/conversations/{id}/report")
    public ReportResponse report(@AuthenticatedUser CurrentUser user, @PathVariable UUID id, @Valid @RequestBody CreateReportRequest req) {
        return reportService.create(user, new CreateReportRequest("conversation", id, req.reason(), req.detail()));
    }
}
