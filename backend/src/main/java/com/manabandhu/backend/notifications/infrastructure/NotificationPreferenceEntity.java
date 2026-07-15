package com.manabandhu.backend.notifications.infrastructure;

import com.manabandhu.backend.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notification_preferences")
@Getter
@Setter
@NoArgsConstructor
public class NotificationPreferenceEntity extends BaseEntity {

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private boolean pushEnabled = true;

    @Column(nullable = false)
    private boolean emailEnabled = true;

    @Column(nullable = false)
    private boolean messages = true;

    @Column(nullable = false)
    private boolean rides = true;

    @Column(nullable = false)
    private boolean listings = true;

    @Column(nullable = false)
    private boolean posts = true;

    @Column(nullable = false)
    private boolean events = true;

    @Column(nullable = false)
    private boolean expenses = true;

    @Column(nullable = false)
    private boolean moderation = true;

    @Column(nullable = false)
    private boolean marketing = false;
}
