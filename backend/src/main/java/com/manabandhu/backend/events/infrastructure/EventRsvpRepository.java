package com.manabandhu.backend.events.infrastructure;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRsvpRepository extends JpaRepository<EventRsvpEntity, UUID> {
    EventRsvpEntity findByEventIdAndUserId(UUID eventId, UUID userId);
    long countByEventIdAndStatus(UUID eventId, EventRsvpEntity.RsvpStatus status);
}
