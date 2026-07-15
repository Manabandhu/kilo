package com.manabandhu.backend.rides.infrastructure;

import com.manabandhu.backend.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ride_ratings")
@Getter
@Setter
@NoArgsConstructor
public class RideRatingEntity extends BaseEntity {

    @Column(nullable = false)
    private UUID rideId;

    @Column(nullable = false)
    private UUID fromUserId;

    @Column(nullable = false)
    private UUID toUserId;

    @Column(nullable = false)
    private int score;

    @Column(length = 1000)
    private String comment;
}
