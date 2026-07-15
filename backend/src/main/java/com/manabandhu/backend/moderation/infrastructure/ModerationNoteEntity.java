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
@Table(name = "moderation_notes")
@Getter
@Setter
@NoArgsConstructor
public class ModerationNoteEntity extends BaseEntity {

    @Column(nullable = false)
    private UUID moderatorId;

    @Column(nullable = false)
    private UUID targetUserId;

    @Column(nullable = false, length = 2000)
    private String body;
}
