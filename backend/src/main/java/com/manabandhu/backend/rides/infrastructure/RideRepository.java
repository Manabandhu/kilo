package com.manabandhu.backend.rides.infrastructure;

import com.manabandhu.backend.rides.infrastructure.RideEntity.RideStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends JpaRepository<RideEntity, UUID> {

    Page<RideEntity> findByDriverIdAndStatusNot(UUID driverId, RideStatus deleted, Pageable pageable);

    @Query("""
           select r from RideEntity r
           where r.deletedAt is null
             and (:type is null or r.type = :type)
             and (:state is null or r.destination = :state or r.origin = :state)
             and (:query is null or lower(r.origin || ' ' || r.destination) like lower('%' || :query || '%'))
             and (r.status = 'published' or r.driverId = :driverId)
           """)
    Page<RideEntity> search(@Param("type") String type, @Param("state") String state,
                            @Param("query") String query, @Param("driverId") UUID driverId, Pageable pageable);
}
