package com.manabandhu.backend.notifications.application;

import java.util.UUID;

/** Published by any domain when something notification-worthy occurs. */
public record NewNotificationEvent(
        UUID userId,
        String type,
        String title,
        String body,
        String deepLink) {
}
