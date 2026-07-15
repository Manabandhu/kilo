package com.manabandhu.backend.jobs.api;

import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.jobs.api.JobApi.CreateJobRequest;
import com.manabandhu.backend.jobs.api.JobApi.CreateReferralRequest;
import com.manabandhu.backend.jobs.api.JobApi.JobResponse;
import com.manabandhu.backend.jobs.api.JobApi.ReferralResponse;
import com.manabandhu.backend.jobs.application.JobService;
import com.manabandhu.backend.moderation.api.ReportApi.CreateReportRequest;
import com.manabandhu.backend.moderation.api.ReportApi.ReportResponse;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public PagedResult<JobResponse> search(
            @RequestParam(required = false) String workMode,
            @RequestParam(required = false) String employmentType,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Boolean sponsorship,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return jobService.search(workMode, employmentType, experienceLevel, state, sponsorship, minSalary, query, page, size);
    }

    @GetMapping("/{id}")
    public JobResponse get(@PathVariable UUID id) {
        return jobService.get(id);
    }

    @PostMapping
    public JobResponse create(@AuthenticatedUser CurrentUser user, @Valid @RequestBody CreateJobRequest req) {
        return jobService.create(user, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticatedUser CurrentUser user, @PathVariable UUID id) {
        jobService.delete(user, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/referrals")
    public ReferralResponse refer(@AuthenticatedUser CurrentUser user, @PathVariable UUID id, @Valid @RequestBody CreateReferralRequest req) {
        return jobService.refer(user, new CreateReferralRequest(id, req.kind(), req.toUserId(), req.company(), req.note()));
    }

    @PostMapping("/{id}/report")
    public ReportResponse report(@AuthenticatedUser CurrentUser user, @PathVariable UUID id, @Valid @RequestBody CreateReportRequest req) {
        return jobService.report(user, id, req);
    }
}
