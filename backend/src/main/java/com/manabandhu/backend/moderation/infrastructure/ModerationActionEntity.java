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
@Table(name = "moderation_actions")
@Getter
@Setter
@NoArgsConstructor
public class ModerationActionEntity extends BaseEntity {

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private UUID moderatorId;

    private UUID targetUserId;

    private UUID reportId;

    private String targetType;

    private UUID targetId;

    @Column(length = 2000)
    private String note;
}
