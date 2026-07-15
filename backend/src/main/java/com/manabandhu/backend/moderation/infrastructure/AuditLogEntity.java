package com.manabandhu.backend.moderation.infrastructure;

import com.manabandhu.backend.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
public class AuditLogEntity extends BaseEntity {

    private UUID actorId;

    @Column(nullable = false)
    private String action;

    private String entityType;

    private UUID entityId;

    @Column(columnDefinition = "jsonb")
    private String metadata;
}
