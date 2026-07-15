package com.manabandhu.backend.moderation.api;

import com.manabandhu.backend.moderation.infrastructure.ReportEntity;
import java.util.UUID;

public final class ReportApi {

    private ReportApi() {
    }

    public record CreateReportRequest(
            String targetType, UUID targetId, String reason, String detail) {
    }

    public record ReportResponse(
            UUID id, UUID reporterId, String targetType, UUID targetId, String reason,
            String detail, String status, UUID assignedToId, String resolution,
            String createdAt, String updatedAt) {
    }

    public static ReportResponse toResponse(ReportEntity e) {
        return new ReportResponse(e.getId(), e.getReporterId(), e.getTargetType(), e.getTargetId(),
                e.getReason(), e.getDetail(), e.getStatus().name(), e.getAssignedToId(), e.getResolution(),
                e.getCreatedAt().toString(), e.getUpdatedAt().toString());
    }
}
