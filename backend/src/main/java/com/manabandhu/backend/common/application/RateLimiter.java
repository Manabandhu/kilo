package com.manabandhu.backend.common.application;

import com.manabandhu.backend.common.exception.BusinessException;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RateLimiter {

    private final Map<String, ActionWindow> windows = new ConcurrentHashMap<>();
    private final int perMinute;

    public RateLimiter(@Value("${app.rate-limit.per-minute:120}") int perMinute) {
        this.perMinute = perMinute;
    }

    public void check(String action, String key) {
        String composite = action + ":" + key;
        long now = System.currentTimeMillis();
        ActionWindow window = windows.computeIfAbsent(composite, k -> new ActionWindow(perMinute));
        window.check(now);
    }

    private static class ActionWindow {
        private final int limit;
        private final ArrayDeque<Long> hits = new ArrayDeque<>();

        ActionWindow(int limit) {
            this.limit = limit;
        }

        synchronized void check(long now) {
            while (!hits.isEmpty() && now - hits.peekFirst() > 60_000) {
                hits.pollFirst();
            }
            if (hits.size() >= limit) {
                throw BusinessException.rateLimited();
            }
            hits.addLast(now);
        }
    }
}
