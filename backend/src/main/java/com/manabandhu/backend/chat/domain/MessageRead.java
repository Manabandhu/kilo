package com.manabandhu.backend.chat.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "message_reads")
public class MessageRead {

    @EmbeddedId
    private MessageReadId id;

    @Column(name = "read_at", nullable = false)
    private Instant readAt;

    public MessageRead() {}

    public MessageRead(MessageReadId id, Instant readAt) {
        this.id = id;
        this.readAt = readAt;
    }

    public MessageReadId getId() { return id; }
    public void setId(MessageReadId id) { this.id = id; }
    public Instant getReadAt() { return readAt; }
    public void setReadAt(Instant readAt) { this.readAt = readAt; }
}
