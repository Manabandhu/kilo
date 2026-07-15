package com.manabandhu.backend.chat.domain;

import com.manabandhu.backend.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "conversations")
public class Conversation extends BaseEntity {

    @Column(nullable = false)
    private String kind;

    @Column
    private String title;

    public Conversation() {}

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
