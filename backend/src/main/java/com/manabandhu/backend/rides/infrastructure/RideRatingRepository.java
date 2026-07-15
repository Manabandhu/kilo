package com.manabandhu.backend.rides.infrastructure;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRatingRepository extends JpaRepository<RideRatingEntity, UUID> {
}
