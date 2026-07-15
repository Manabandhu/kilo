package com.manabandhu.backend.rides.infrastructure;

import com.manabandhu.backend.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rides")
@Getter
@Setter
@NoArgsConstructor
public class RideEntity extends BaseEntity {

    @Column(nullable = false)
    private String type; // offer | request

    @Column(nullable = false)
    private String frequency;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false, columnDefinition = "jsonb")
    private String originLocation;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false, columnDefinition = "jsonb")
    private String destinationLocation;

    @Column(columnDefinition = "jsonb")
    private String stops = "[]";

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String time;

    @Column(nullable = false)
    private String timeFlexibility;

    @Column(nullable = false)
    private int availableSeats;

    @Column(precision = 12, scale = 2)
    private BigDecimal suggestedContribution;

    @Column(length = 1000)
    private String luggageInfo;

    @Column(length = 1000)
    private String driverNotes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status = RideStatus.draft;

    @Column(nullable = false)
    private UUID driverId;

    @Column
    private UUID createdBy;

    @Column
    private UUID updatedBy;

    @Column
    private LocalDate deletedAt;

    public enum RideStatus { draft, published, full, in_progress, completed, cancelled }
}
