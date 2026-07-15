package com.manabandhu.backend.notifications.api;

import com.manabandhu.backend.notifications.infrastructure.NotificationEntity;
import com.manabandhu.backend.notifications.infrastructure.NotificationPreferenceEntity;
import java.util.UUID;

public final class NotificationApi {

    private NotificationApi() {
    }

    public record NotificationResponse(
            UUID id, UUID userId, String type, String title, String body, String deepLink, boolean read, String createdAt) {
    }

    public record PreferencesResponse(
            boolean pushEnabled, boolean emailEnabled, boolean messages, boolean rides, boolean listings,
            boolean posts, boolean events, boolean expenses, boolean moderation, boolean marketing) {
    }

    public record PreferencesRequest(
            Boolean pushEnabled, Boolean emailEnabled, Boolean messages, Boolean rides, Boolean listings,
            Boolean posts, Boolean events, Boolean expenses, Boolean moderation, Boolean marketing) {
    }

    public record TokenRequest(String token, String platform, String deviceId) {
    }

    public static NotificationResponse toResponse(NotificationEntity e) {
        return new NotificationResponse(e.getId(), e.getUserId(), e.getType(), e.getTitle(), e.getBody(),
                e.getDeepLink(), e.isRead(), e.getCreatedAt().toString());
    }

    public static PreferencesResponse toResponse(NotificationPreferenceEntity e) {
        return new PreferencesResponse(e.isPushEnabled(), e.isEmailEnabled(), e.isMessages(), e.isRides(),
                e.isListings(), e.isPosts(), e.isEvents(), e.isExpenses(), e.isModeration(), e.isMarketing());
    }

    public static NotificationPreferenceEntity apply(NotificationPreferenceEntity e, PreferencesRequest r) {
        if (r.pushEnabled() != null) e.setPushEnabled(r.pushEnabled());
        if (r.emailEnabled() != null) e.setEmailEnabled(r.emailEnabled());
        if (r.messages() != null) e.setMessages(r.messages());
        if (r.rides() != null) e.setRides(r.rides());
        if (r.listings() != null) e.setListings(r.listings());
        if (r.posts() != null) e.setPosts(r.posts());
        if (r.events() != null) e.setEvents(r.events());
        if (r.expenses() != null) e.setExpenses(r.expenses());
        if (r.moderation() != null) e.setModeration(r.moderation());
        if (r.marketing() != null) e.setMarketing(r.marketing());
        return e;
    }
}
