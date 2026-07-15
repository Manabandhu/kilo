package com.manabandhu.backend.rides.infrastructure;

import com.manabandhu.backend.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ride_requests")
@Getter
@Setter
@NoArgsConstructor
public class RideRequestEntity extends BaseEntity {

    @Column(nullable = false)
    private UUID rideId;

    @Column(nullable = false)
    private UUID riderId;

    @Column(nullable = false)
    private int seatsRequested;

    @Column(length = 1000)
    private String riderNotes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatRequestStatus status = SeatRequestStatus.pending;

    public enum SeatRequestStatus { pending, approved, rejected, cancelled }
}
