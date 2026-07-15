package com.manabandhu.backend.rides.infrastructure;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRequestRepository extends JpaRepository<RideRequestEntity, UUID> {
    java.util.List<RideRequestEntity> findByRideId(UUID rideId);
}
