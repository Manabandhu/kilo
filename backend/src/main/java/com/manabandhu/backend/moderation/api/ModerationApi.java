package com.manabandhu.backend.moderation.api;

import com.manabandhu.backend.moderation.infrastructure.ModerationActionEntity;
import com.manabandhu.backend.moderation.infrastructure.ModerationNoteEntity;
import java.util.UUID;

public final class ModerationApi {

    private ModerationApi() {
    }

    public record ActionRequest(String type, UUID targetUserId, UUID reportId, String targetType, UUID targetId, String note) {
    }

    public record NoteRequest(UUID targetUserId, String body) {
    }

    public record ActionResponse(
            UUID id, String type, UUID moderatorId, UUID targetUserId, UUID reportId,
            String targetType, UUID targetId, String note, String createdAt) {
    }

    public record NoteResponse(UUID id, UUID moderatorId, UUID targetUserId, String body, String createdAt) {
    }

    public static ActionResponse toResponse(ModerationActionEntity e) {
        return new ActionResponse(e.getId(), e.getType(), e.getModeratorId(), e.getTargetUserId(),
                e.getReportId(), e.getTargetType(), e.getTargetId(), e.getNote(), e.getCreatedAt().toString());
    }

    public static NoteResponse toResponse(ModerationNoteEntity e) {
        return new NoteResponse(e.getId(), e.getModeratorId(), e.getTargetUserId(), e.getBody(), e.getCreatedAt().toString());
    }
}
