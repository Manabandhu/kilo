package com.manabandhu.backend.chat.infrastructure;

import com.manabandhu.backend.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "message_reads")
@Getter
@Setter
@NoArgsConstructor
public class MessageReadEntity extends BaseEntity {

    @Column(nullable = false)
    private UUID messageId;

    @Column(nullable = false)
    private UUID userId;
}
