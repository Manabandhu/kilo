package com.manabandhu.backend.rooms.api;

import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.moderation.api.ReportApi.CreateReportRequest;
import com.manabandhu.backend.moderation.api.ReportApi.ReportResponse;
import com.manabandhu.backend.moderation.application.ReportService;
import com.manabandhu.backend.rooms.api.RoomApi.CreateRoomRequest;
import com.manabandhu.backend.rooms.api.RoomApi.RoomResponse;
import com.manabandhu.backend.rooms.api.RoomApi.UpdateRoomRequest;
import com.manabandhu.backend.rooms.application.RoomService;
import com.manabandhu.backend.security.AuthenticatedUser;
import com.manabandhu.backend.security.CurrentUser;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    private final RoomService roomService;
    private final ReportService reportService;

    public RoomController(RoomService roomService, ReportService reportService) {
        this.roomService = roomService;
        this.reportService = reportService;
    }

    @GetMapping
    public PagedResult<RoomResponse> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) BigDecimal minRent,
            @RequestParam(required = false) BigDecimal maxRent,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return roomService.search(city, state, roomType, propertyType, minRent, maxRent, query, page, size);
    }

    @GetMapping("/{id}")
    public RoomResponse get(@AuthenticatedUser CurrentUser user, @PathVariable UUID id) {
        return roomService.get(id, user);
    }

    @PostMapping
    public RoomResponse create(@AuthenticatedUser CurrentUser user, @Valid @RequestBody CreateRoomRequest req) {
        return roomService.create(user, req);
    }

    @PutMapping("/{id}")
    public RoomResponse update(@AuthenticatedUser CurrentUser user, @PathVariable UUID id, @Valid @RequestBody UpdateRoomRequest req) {
        return roomService.update(user, id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticatedUser CurrentUser user, @PathVariable UUID id) {
        roomService.delete(user, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/similar")
    public PagedResult<RoomResponse> similar(@PathVariable UUID id,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "5") int size) {
        return roomService.similar(id, page, size);
    }

    @PostMapping("/{id}/report")
    public ReportResponse report(@AuthenticatedUser CurrentUser user, @PathVariable UUID id, @Valid @RequestBody CreateReportRequest req) {
        return reportService.create(user, new CreateReportRequest("room", id, req.reason(), req.detail()));
    }
}
