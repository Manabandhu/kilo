package com.manabandhu.backend.notifications.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Default push provider: logs delivery. Swap with Firebase/APNs via config + @Qualifier. */
@Component
public class ConsolePushProvider implements PushProvider {

    private static final Logger log = LoggerFactory.getLogger(ConsolePushProvider.class);

    private final String provider;

    public ConsolePushProvider(@Value("${app.push.provider:console}") String provider) {
        this.provider = provider;
    }

    @Override
    public void send(String token, String platform, String title, String body, String deepLink) {
        log.info("[push:{}] to={} platform={} title=\"{}\" body=\"{}\" deepLink={}",
                provider, token, platform, title, body, deepLink);
    }
}
