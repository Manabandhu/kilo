package com.manabandhu.backend.rides.api;

import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.moderation.api.ReportApi.CreateReportRequest;
import com.manabandhu.backend.moderation.api.ReportApi.ReportResponse;
import com.manabandhu.backend.moderation.application.ReportService;
import com.manabandhu.backend.rides.api.RideApi.CreateRideRequest;
import com.manabandhu.backend.rides.api.RideApi.CreateSeatRequest;
import com.manabandhu.backend.rides.api.RideApi.RateRequest;
import com.manabandhu.backend.rides.api.RideApi.RideResponse;
import com.manabandhu.backend.rides.api.RideApi.SeatRequestResponse;
import com.manabandhu.backend.rides.application.RideService;
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
@RequestMapping("/api/v1/rides")
public class RideController {

    private final RideService rideService;
    private final ReportService reportService;

    public RideController(RideService rideService, ReportService reportService) {
        this.rideService = rideService;
        this.reportService = reportService;
    }

    @GetMapping
    public PagedResult<RideResponse> search(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String query,
            @AuthenticatedUser CurrentUser user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return rideService.search(type, state, query, user, page, size);
    }

    @GetMapping("/{id}")
    public RideResponse get(@PathVariable UUID id) {
        return rideService.get(id);
    }

    @PostMapping
    public RideResponse create(@AuthenticatedUser CurrentUser user, @Valid @RequestBody CreateRideRequest req) {
        return rideService.create(user, req);
    }

    @PatchMapping("/{id}/status")
    public RideResponse status(@AuthenticatedUser CurrentUser user, @PathVariable UUID id, @RequestParam String status) {
        return rideService.updateStatus(user, id, status);
    }

    @PostMapping("/{id}/requests")
    public SeatRequestResponse requestSeat(@AuthenticatedUser CurrentUser user, @PathVariable UUID id, @Valid @RequestBody CreateSeatRequest req) {
        return rideService.requestSeat(user, id, req);
    }

    @PatchMapping("/{id}/requests/{reqId}")
    public SeatRequestResponse decide(@AuthenticatedUser CurrentUser user, @PathVariable UUID id, @PathVariable UUID reqId, @RequestParam String decision) {
        return rideService.decideSeat(user, id, reqId, decision);
    }

    @PostMapping("/{id}/ratings")
    public ResponseEntity<Void> rate(@AuthenticatedUser CurrentUser user, @PathVariable UUID id, @Valid @RequestBody RateRequest req) {
        rideService.rate(user, id, req);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/report")
    public ReportResponse report(@AuthenticatedUser CurrentUser user, @PathVariable UUID id, @Valid @RequestBody CreateReportRequest req) {
        return reportService.create(user, new CreateReportRequest("ride", id, req.reason(), req.detail()));
    }
}
