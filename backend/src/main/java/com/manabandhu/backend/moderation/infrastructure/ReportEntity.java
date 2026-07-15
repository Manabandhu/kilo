package com.manabandhu.backend.moderation.infrastructure;

import com.manabandhu.backend.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
public class ReportEntity extends BaseEntity {

    @Column(nullable = false)
    private UUID reporterId;

    @Column(nullable = false)
    private String targetType;

    @Column(nullable = false)
    private UUID targetId;

    @Column(nullable = false)
    private String reason;

    @Column(length = 2000)
    private String detail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.open;

    @Column
    private UUID assignedToId;

    @Column(length = 2000)
    private String resolution;

    public enum ReportStatus { open, reviewing, resolved, dismissed }
}
