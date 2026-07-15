package com.manabandhu.backend.community.domain;

import com.manabandhu.backend.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reports")
public class Report extends BaseEntity {

    @Column(name = "reporter_id", nullable = false)
    private UUID reporterId;

    @Column(name = "target_type", nullable = false)
    private String targetType;

    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @Column(nullable = false)
    private String reason;

    @Column
    private String detail;

    @Column(nullable = false)
    private String status = "open";

    @Column(name = "assigned_to_id")
    private UUID assignedToId;

    @Column
    private String resolution;

    public Report() {}

    public UUID getReporterId() { return reporterId; }
    public void setReporterId(UUID reporterId) { this.reporterId = reporterId; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public UUID getTargetId() { return targetId; }
    public void setTargetId(UUID targetId) { this.targetId = targetId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public UUID getAssignedToId() { return assignedToId; }
    public void setAssignedToId(UUID assignedToId) { this.assignedToId = assignedToId; }
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
}
