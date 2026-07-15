package com.manabandhu.backend.rooms.infrastructure;

import com.manabandhu.backend.rooms.infrastructure.RoomListingEntity.RoomStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<RoomListingEntity, UUID> {

    Page<RoomListingEntity> findByPosterIdAndStatusNot(UUID posterId, RoomStatus deleted, Pageable pageable);

    @Query("""
           select r from RoomListingEntity r
           where r.status <> 'deleted' and r.deletedAt is null
             and (:city is null or r.city = :city)
             and (:state is null or r.state = :state)
             and (:roomType is null or r.roomType = :roomType)
             and (:propertyType is null or r.propertyType = :propertyType)
             and (:minRent is null or r.monthlyRent >= :minRent)
             and (:maxRent is null or r.monthlyRent <= :maxRent)
             and (:query is null or lower(r.title || ' ' || r.description) like lower('%' || :query || '%'))
           """)
    Page<RoomListingEntity> search(
            @Param("city") String city,
            @Param("state") String state,
            @Param("roomType") String roomType,
            @Param("propertyType") String propertyType,
            @Param("minRent") java.math.BigDecimal minRent,
            @Param("maxRent") java.math.BigDecimal maxRent,
            @Param("query") String query,
            Pageable pageable);
}
