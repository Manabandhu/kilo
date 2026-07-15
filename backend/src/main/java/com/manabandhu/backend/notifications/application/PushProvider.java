package com.manabandhu.backend.notifications.application;

/** Provider abstraction so push delivery can evolve (console, firebase, apns). */
public interface PushProvider {
    void send(String token, String platform, String title, String body, String deepLink);
}
