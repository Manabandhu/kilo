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
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
public class MessageEntity extends BaseEntity {

    @Column(nullable = false)
    private UUID conversationId;

    @Column(nullable = false)
    private UUID senderId;

    @Column(nullable = false, length = 8000)
    private String body;

    @Column(columnDefinition = "jsonb")
    private String attachments = "[]";

    private UUID replyToId;

    private String reaction;

    @Column(nullable = false)
    private boolean edited;
}
