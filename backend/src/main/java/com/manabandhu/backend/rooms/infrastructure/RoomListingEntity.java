package com.manabandhu.backend.rooms.infrastructure;

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
@Table(name = "room_listings")
@Getter
@Setter
@NoArgsConstructor
public class RoomListingEntity extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 4000)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyRent;

    @Column(precision = 12, scale = 2)
    private BigDecimal securityDeposit;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false, length = 2)
    private String state;

    @Column(length = 10)
    private String zip;

    @Column(columnDefinition = "jsonb")
    private String approximateLocation;

    @Column(nullable = false)
    private boolean exactAddressShared;

    @Column(nullable = false)
    private String roomType;

    @Column(nullable = false)
    private String propertyType;

    @Column(nullable = false)
    private String furnished;

    private LocalDate availableDate;

    private Integer leaseDurationMonths;

    @Column(nullable = false)
    private boolean utilitiesIncluded;

    @Column(columnDefinition = "text[]")
    private String[] amenities = new String[0];

    @Column(nullable = false)
    private boolean parking;

    @Column(nullable = false)
    private String petPolicy;

    @Column(nullable = false)
    private String smokingPolicy;

    @Column(nullable = false)
    private int occupancy;

    @Column(nullable = false)
    private String genderPreference;

    @Column(nullable = false)
    private String contactPreference;

    @Column(columnDefinition = "text[]")
    private String[] images = new String[0];

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status = RoomStatus.active;

    @Column(nullable = false)
    private UUID posterId;

    @Column
    private UUID createdBy;

    @Column
    private UUID updatedBy;

    @Column
    private LocalDate deletedAt;

    public enum RoomStatus { active, paused, closed, deleted }
}
