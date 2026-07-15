package com.manabandhu.backend.chat.domain;

public record MessageAttachment(
        String id,
        String type,
        String url,
        String name,
        Long sizeBytes) {
}
