package com.manabandhu.backend.chat.api.dto;

public record MessageAttachment(
        String id,
        String type,
        String url,
        String name,
        Long sizeBytes) {
}
