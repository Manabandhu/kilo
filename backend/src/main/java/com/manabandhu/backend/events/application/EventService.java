package com.manabandhu.backend.events.application;

import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.common.exception.BusinessException;
import com.manabandhu.backend.events.api.EventApi;
import com.manabandhu.backend.events.api.EventApi.CreateEventRequest;
import com.manabandhu.backend.events.api.EventApi.EventResponse;
import com.manabandhu.backend.events.api.EventApi.RsvpResponse;
import com.manabandhu.backend.events.infrastructure.EventEntity;
import com.manabandhu.backend.events.infrastructure.EventEntity.EventStatus;
import com.manabandhu.backend.events.infrastructure.EventRepository;
import com.manabandhu.backend.events.infrastructure.EventRsvpEntity;
import com.manabandhu.backend.events.infrastructure.EventRsvpEntity.RsvpStatus;
import com.manabandhu.backend.events.infrastructure.EventRsvpRepository;
import com.manabandhu.backend.moderation.api.ReportApi.CreateReportRequest;
import com.manabandhu.backend.moderation.api.ReportApi.ReportResponse;
import com.manabandhu.backend.moderation.application.ReportService;
import com.manabandhu.backend.security.Authorization;
import com.manabandhu.backend.security.CurrentUser;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventRsvpRepository rsvpRepository;
    private final ReportService reportService;

    public EventService(EventRepository eventRepository, EventRsvpRepository rsvpRepository, ReportService reportService) {
        this.eventRepository = eventRepository;
        this.rsvpRepository = rsvpRepository;
        this.reportService = reportService;
    }

    public PagedResult<EventResponse> search(String city, String state, String venueType, Boolean upcoming,
                                             String query, int page, int size) {
        Page<EventEntity> p = eventRepository.search(city, state, venueType, upcoming, query, PageRequest.of(page, size));
        return PagedResult.of(p.getContent().stream().map(this::toResponse).toList(), page, size, p.getTotalElements());
    }

    public EventResponse get(UUID id) {
        return toResponse(require(id));
    }

    @Transactional
    public EventResponse create(CurrentUser user, CreateEventRequest req) {
        return toResponse(eventRepository.save(EventApi.toEntity(req, user.userId())));
    }

    @Transactional
    public EventResponse updateStatus(CurrentUser user, UUID id, String status) {
        EventEntity e = require(id);
        Authorization.requireOwner(user, e.getOrganizerId());
        e.setStatus(EventStatus.valueOf(status));
        return toResponse(eventRepository.save(e));
    }

    @Transactional
    public RsvpResponse rsvp(CurrentUser user, UUID id, String status) {
        EventEntity e = require(id);
        RsvpStatus desired = RsvpStatus.valueOf(status);
        long going = rsvpRepository.countByEventIdAndStatus(id, RsvpStatus.going);
        if (desired == RsvpStatus.going && e.getCapacity() != null && going >= e.getCapacity()) {
            desired = RsvpStatus.waitlist;
        }
        EventRsvpEntity r = rsvpRepository.findByEventIdAndUserId(id, user.userId());
        if (r == null) {
            r = new EventRsvpEntity();
            r.setEventId(id);
            r.setUserId(user.userId());
        }
        r.setStatus(desired);
        rsvpRepository.save(r);
        return new RsvpResponse(id, user.userId(), desired.name());
    }

    public ReportResponse report(CurrentUser user, UUID id, CreateReportRequest req) {
        return reportService.create(user, new CreateReportRequest("event", id, req.reason(), req.detail()));
    }

    private EventResponse toResponse(EventEntity e) {
        long going = rsvpRepository.countByEventIdAndStatus(e.getId(), RsvpStatus.going);
        long waitlist = rsvpRepository.countByEventIdAndStatus(e.getId(), RsvpStatus.waitlist);
        return EventApi.toResponse(e, going, waitlist);
    }

    private EventEntity require(UUID id) {
        return eventRepository.findById(id).filter(e -> e.getStatus() != EventStatus.cancelled && e.getDeletedAt() == null)
                .orElseThrow(() -> BusinessException.notFound("Event not found"));
    }
}
