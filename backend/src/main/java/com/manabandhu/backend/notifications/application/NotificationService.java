package com.manabandhu.backend.notifications.application;

import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.notifications.api.NotificationApi;
import com.manabandhu.backend.notifications.api.NotificationApi.NotificationResponse;
import com.manabandhu.backend.notifications.api.NotificationApi.PreferencesRequest;
import com.manabandhu.backend.notifications.api.NotificationApi.PreferencesResponse;
import com.manabandhu.backend.notifications.api.NotificationApi.TokenRequest;
import com.manabandhu.backend.notifications.infrastructure.NotificationEntity;
import com.manabandhu.backend.notifications.infrastructure.NotificationPreferenceEntity;
import com.manabandhu.backend.notifications.infrastructure.NotificationPreferenceRepository;
import com.manabandhu.backend.notifications.infrastructure.NotificationRepository;
import com.manabandhu.backend.notifications.infrastructure.PushTokenEntity;
import com.manabandhu.backend.notifications.infrastructure.PushTokenRepository;
import com.manabandhu.backend.security.CurrentUser;
import java.util.UUID;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final PushTokenRepository pushTokenRepository;
    private final PushProvider pushProvider;

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationPreferenceRepository preferenceRepository,
                               PushTokenRepository pushTokenRepository,
                               PushProvider pushProvider) {
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
        this.pushTokenRepository = pushTokenRepository;
        this.pushProvider = pushProvider;
    }

    @EventListener
    @Transactional
    public void onNewNotification(NewNotificationEvent event) {
        NotificationEntity n = new NotificationEntity();
        n.setUserId(event.userId());
        n.setType(event.type());
        n.setTitle(event.title());
        n.setBody(event.body());
        n.setDeepLink(event.deepLink());
        n.setRead(false);
        notificationRepository.save(n);

        NotificationPreferenceEntity prefs = preferenceRepository.findByUserId(event.userId());
        if (prefs != null && prefs.isPushEnabled() && wantsType(prefs, event.type())) {
            pushTokenRepository.findByUserIdAndToken(event.userId(), "")
                    .ifPresentOrElse(
                            t -> pushProvider.send(t.getToken(), t.getPlatform(), event.title(), event.body(), event.deepLink()),
                            () -> pushTokenRepository.findAll().stream()
                                    .filter(t -> t.getUserId().equals(event.userId()))
                                    .findFirst()
                                    .ifPresent(t -> pushProvider.send(t.getToken(), t.getPlatform(), event.title(), event.body(), event.deepLink())));
        }
    }

    private boolean wantsType(NotificationPreferenceEntity p, String type) {
        return switch (type) {
            case "message" -> p.isMessages();
            case "ride_request", "ride_decision", "ride_reminder" -> p.isRides();
            case "listing_inquiry" -> p.isListings();
            case "post_reply", "comment_reply" -> p.isPosts();
            case "event_reminder" -> p.isEvents();
            case "expense_added", "settlement_recorded" -> p.isExpenses();
            case "moderation_action", "security_alert" -> p.isModeration();
            default -> true;
        };
    }

    public PagedResult<NotificationResponse> list(CurrentUser user, int page, int size) {
        Page<NotificationEntity> p = notificationRepository.findByUser(user.userId(), PageRequest.of(page, size));
        return PagedResult.of(p.getContent().stream().map(NotificationApi::toResponse).toList(), page, size, p.getTotalElements());
    }

    public long unreadCount(CurrentUser user) {
        return notificationRepository.countByUserIdAndReadFalse(user.userId());
    }

    @Transactional
    public void markRead(CurrentUser user, UUID id) {
        notificationRepository.findById(id).ifPresent(n -> {
            if (n.getUserId().equals(user.userId())) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        });
    }

    @Transactional
    public void markAllRead(CurrentUser user) {
        notificationRepository.findByUser(user.userId(), PageRequest.of(0, Integer.MAX_VALUE)).getContent()
                .forEach(n -> { n.setRead(true); notificationRepository.save(n); });
    }

    public PreferencesResponse preferences(CurrentUser user) {
        return NotificationApi.toResponse(preferenceRepository.findByUserId(user.userId()));
    }

    @Transactional
    public PreferencesResponse updatePreferences(CurrentUser user, PreferencesRequest req) {
        NotificationPreferenceEntity e = preferenceRepository.findByUserId(user.userId());
        if (e == null) {
            e = new NotificationPreferenceEntity();
            e.setUserId(user.userId());
        }
        return NotificationApi.toResponse(preferenceRepository.save(NotificationApi.apply(e, req)));
    }

    @Transactional
    public void registerToken(CurrentUser user, TokenRequest req) {
        if (pushTokenRepository.findByUserIdAndToken(user.userId(), req.token()) == null) {
            PushTokenEntity t = new PushTokenEntity();
            t.setUserId(user.userId());
            t.setToken(req.token());
            t.setPlatform(req.platform());
            t.setDeviceId(req.deviceId());
            pushTokenRepository.save(t);
        }
    }

    @Transactional
    public void removeToken(CurrentUser user, String token) {
        PushTokenEntity t = pushTokenRepository.findByUserIdAndToken(user.userId(), token);
        if (t != null) pushTokenRepository.delete(t);
    }
}
