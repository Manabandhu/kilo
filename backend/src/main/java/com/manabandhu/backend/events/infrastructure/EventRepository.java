package com.manabandhu.backend.events.infrastructure;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, UUID> {

    @Query("""
           select e from EventEntity e
           where e.status <> 'cancelled' and e.deletedAt is null
             and (:city is null or e.city = :city)
             and (:state is null or e.state = :state)
             and (:venueType is null or e.venueType = :venueType)
             and (:upcoming is null or e.startTime >= current_timestamp)
             and (:query is null or lower(e.title || ' ' || e.description) like lower('%' || :query || '%'))
           """)
    Page<EventEntity> search(@Param("city") String city, @Param("state") String state,
                             @Param("venueType") String venueType, @Param("upcoming") Boolean upcoming,
                             @Param("query") String query, Pageable pageable);
}
