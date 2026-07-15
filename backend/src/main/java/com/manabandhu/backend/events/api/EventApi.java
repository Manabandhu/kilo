package com.manabandhu.backend.events.api;

import com.manabandhu.backend.events.infrastructure.EventEntity;
import java.time.Instant;
import java.util.UUID;

public final class EventApi {

    private EventApi() {
    }

    public record EventResponse(
            UUID id, String title, String description, String venueType, String location, String onlineUrl,
            String city, String state, String startTime, String endTime, Integer capacity, int attendeeCount,
            int waitlistCount, UUID organizerId, String coverImageUrl, String status) {
    }

    public record CreateEventRequest(
            String title, String description, String venueType, String location, String onlineUrl,
            String city, String state, String startTime, String endTime, Integer capacity, String coverImageUrl) {
    }

    public record RsvpResponse(UUID eventId, UUID userId, String status) {
    }

    public static EventResponse toResponse(EventEntity e, long going, long waitlist) {
        return new EventResponse(e.getId(), e.getTitle(), e.getDescription(), e.getVenueType(), e.getLocation(),
                e.getOnlineUrl(), e.getCity(), e.getState(), e.getStartTime().toString(),
                e.getEndTime() == null ? null : e.getEndTime().toString(), e.getCapacity(),
                (int) going, (int) waitlist, e.getOrganizerId(), e.getCoverImageUrl(), e.getStatus().name());
    }

    public static EventEntity toEntity(CreateEventRequest r, UUID organizerId) {
        EventEntity e = new EventEntity();
        e.setTitle(r.title());
        e.setDescription(r.description());
        e.setVenueType(r.venueType());
        e.setLocation(r.location());
        e.setOnlineUrl(r.onlineUrl());
        e.setCity(r.city());
        e.setState(r.state());
        e.setStartTime(Instant.parse(r.startTime()));
        e.setEndTime(r.endTime() == null ? null : Instant.parse(r.endTime()));
        e.setCapacity(r.capacity());
        e.setCoverImageUrl(r.coverImageUrl());
        e.setOrganizerId(organizerId);
        e.setStatus(EventEntity.EventStatus.scheduled);
        return e;
    }
}
