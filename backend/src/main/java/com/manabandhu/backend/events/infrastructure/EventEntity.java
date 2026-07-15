package com.manabandhu.backend.events.infrastructure;

import com.manabandhu.backend.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
public class EventEntity extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 4000)
    private String description;

    @Column(nullable = false)
    private String venueType;

    private String location;

    private String onlineUrl;

    private String city;

    @Column(length = 2)
    private String state;

    @Column(nullable = false)
    private Instant startTime;

    private Instant endTime;

    private Integer capacity;

    @Column(nullable = false)
    private UUID organizerId;

    private String coverImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status = EventStatus.scheduled;

    @Column
    private LocalDate deletedAt;

    public enum EventStatus { scheduled, cancelled, completed }
}
