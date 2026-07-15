package com.manabandhu.backend.moderation.api;

import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.moderation.api.ModerationApi.ActionRequest;
import com.manabandhu.backend.moderation.api.ModerationApi.ActionResponse;
import com.manabandhu.backend.moderation.api.ModerationApi.NoteRequest;
import com.manabandhu.backend.moderation.api.ModerationApi.NoteResponse;
import com.manabandhu.backend.moderation.application.ModerationService;
import com.manabandhu.backend.moderation.application.ReportService;
import com.manabandhu.backend.moderation.api.ReportApi.ReportResponse;
import com.manabandhu.backend.security.AuthenticatedUser;
import com.manabandhu.backend.security.CurrentUser;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/moderation")
public class ModerationController {

    private final ModerationService moderationService;
    private final ReportService reportService;

    public ModerationController(ModerationService moderationService, ReportService reportService) {
        this.moderationService = moderationService;
        this.reportService = reportService;
    }

    @GetMapping("/reports")
    public PagedResult<ReportResponse> queue(@RequestParam(required = false) String status,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size) {
        return reportService.queue(status, page, size);
    }

    @GetMapping("/reports/{id}")
    public ReportResponse report(@PathVariable UUID id) {
        return reportService.get(id);
    }

    @PostMapping("/actions")
    public ActionResponse action(@AuthenticatedUser CurrentUser staff, @Valid @RequestBody ActionRequest req) {
        return moderationService.action(staff, req);
    }

    @PostMapping("/notes")
    public NoteResponse note(@AuthenticatedUser CurrentUser staff, @Valid @RequestBody NoteRequest req) {
        return moderationService.note(staff, req);
    }

    @GetMapping("/notes/{targetUserId}")
    public List<NoteResponse> notes(@PathVariable UUID targetUserId) {
        return moderationService.notes(targetUserId);
    }
}
