package com.manabandhu.backend.notifications.api;

import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.notifications.api.NotificationApi.NotificationResponse;
import com.manabandhu.backend.notifications.api.NotificationApi.PreferencesRequest;
import com.manabandhu.backend.notifications.api.NotificationApi.PreferencesResponse;
import com.manabandhu.backend.notifications.api.NotificationApi.TokenRequest;
import com.manabandhu.backend.notifications.application.NotificationService;
import com.manabandhu.backend.security.AuthenticatedUser;
import com.manabandhu.backend.security.CurrentUser;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public PagedResult<NotificationResponse> list(@AuthenticatedUser CurrentUser user,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        return service.list(user, page, size);
    }

    @GetMapping("/unread-count")
    public java.util.Map<String, Long> unread(@AuthenticatedUser CurrentUser user) {
        return java.util.Map.of("count", service.unreadCount(user));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> read(@AuthenticatedUser CurrentUser user, @PathVariable UUID id) {
        service.markRead(user, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> readAll(@AuthenticatedUser CurrentUser user) {
        service.markAllRead(user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/preferences")
    public PreferencesResponse preferences(@AuthenticatedUser CurrentUser user) {
        return service.preferences(user);
    }

    @PutMapping("/preferences")
    public PreferencesResponse updatePreferences(@AuthenticatedUser CurrentUser user, @Valid @RequestBody PreferencesRequest req) {
        return service.updatePreferences(user, req);
    }

    @PostMapping("/tokens")
    public ResponseEntity<Void> token(@AuthenticatedUser CurrentUser user, @Valid @RequestBody TokenRequest req) {
        service.registerToken(user, req);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/tokens")
    public ResponseEntity<Void> removeToken(@AuthenticatedUser CurrentUser user, @RequestParam String token) {
        service.removeToken(user, token);
        return ResponseEntity.noContent().build();
    }
}
