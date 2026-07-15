package com.manabandhu.backend.events.api;

import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.events.api.EventApi.CreateEventRequest;
import com.manabandhu.backend.events.api.EventApi.EventResponse;
import com.manabandhu.backend.events.api.EventApi.RsvpResponse;
import com.manabandhu.backend.events.application.EventService;
import com.manabandhu.backend.moderation.api.ReportApi.CreateReportRequest;
import com.manabandhu.backend.moderation.api.ReportApi.ReportResponse;
import com.manabandhu.backend.security.AuthenticatedUser;
import com.manabandhu.backend.security.CurrentUser;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public PagedResult<EventResponse> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String venueType,
            @RequestParam(required = false) Boolean upcoming,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return eventService.search(city, state, venueType, upcoming, query, page, size);
    }

    @GetMapping("/{id}")
    public EventResponse get(@PathVariable UUID id) {
        return eventService.get(id);
    }

    @PostMapping
    public EventResponse create(@AuthenticatedUser CurrentUser user, @Valid @RequestBody CreateEventRequest req) {
        return eventService.create(user, req);
    }

    @PatchMapping("/{id}/status")
    public EventResponse status(@AuthenticatedUser CurrentUser user, @PathVariable UUID id, @RequestParam String status) {
        return eventService.updateStatus(user, id, status);
    }

    @PostMapping("/{id}/rsvp")
    public RsvpResponse rsvp(@AuthenticatedUser CurrentUser user, @PathVariable UUID id, @RequestParam String status) {
        return eventService.rsvp(user, id, status);
    }

    @PostMapping("/{id}/report")
    public ReportResponse report(@AuthenticatedUser CurrentUser user, @PathVariable UUID id, @Valid @RequestBody CreateReportRequest req) {
        return eventService.report(user, id, req);
    }
}
