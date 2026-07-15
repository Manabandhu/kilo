package com.manabandhu.backend.jobs.application;

import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.common.exception.BusinessException;
import com.manabandhu.backend.jobs.api.JobApi;
import com.manabandhu.backend.jobs.api.JobApi.CreateJobRequest;
import com.manabandhu.backend.jobs.api.JobApi.CreateReferralRequest;
import com.manabandhu.backend.jobs.api.JobApi.JobResponse;
import com.manabandhu.backend.jobs.api.JobApi.ReferralResponse;
import com.manabandhu.backend.jobs.infrastructure.JobEntity;
import com.manabandhu.backend.jobs.infrastructure.JobEntity.JobStatus;
import com.manabandhu.backend.jobs.infrastructure.JobRepository;
import com.manabandhu.backend.jobs.infrastructure.ReferralEntity;
import com.manabandhu.backend.jobs.infrastructure.ReferralRepository;
import com.manabandhu.backend.moderation.api.ReportApi.CreateReportRequest;
import com.manabandhu.backend.moderation.api.ReportApi.ReportResponse;
import com.manabandhu.backend.moderation.application.ReportService;
import com.manabandhu.backend.security.Authorization;
import com.manabandhu.backend.security.CurrentUser;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final ReferralRepository referralRepository;
    private final ReportService reportService;

    public JobService(JobRepository jobRepository, ReferralRepository referralRepository, ReportService reportService) {
        this.jobRepository = jobRepository;
        this.referralRepository = referralRepository;
        this.reportService = reportService;
    }

    public PagedResult<JobResponse> search(String workMode, String employmentType, String experienceLevel,
                                           String state, Boolean sponsorship, BigDecimal minSalary, String query,
                                           int page, int size) {
        Page<JobEntity> p = jobRepository.search(workMode, employmentType, experienceLevel, state,
                sponsorship, minSalary, query, PageRequest.of(page, size));
        return PagedResult.of(p.getContent().stream().map(JobApi::toResponse).toList(), page, size, p.getTotalElements());
    }

    public JobResponse get(UUID id) {
        return JobApi.toResponse(require(id));
    }

    @Transactional
    public JobResponse create(CurrentUser user, CreateJobRequest req) {
        if (req.expiresAt() != null && req.expiresAt().isBefore(LocalDate.now())) {
            throw BusinessException.validation("Expiry must be in the future", null);
        }
        return JobApi.toResponse(jobRepository.save(JobApi.toEntity(req, user.userId())));
    }

    @Transactional
    public void delete(CurrentUser user, UUID id) {
        JobEntity e = require(id);
        boolean owner = e.getPostedById() != null && e.getPostedById().equals(user.userId());
        if (!owner && !user.isModerator()) {
            throw BusinessException.forbidden("Not allowed to remove this job");
        }
        e.setStatus(JobStatus.deleted);
        e.setDeletedAt(LocalDate.now());
        jobRepository.save(e);
    }

    @Transactional
    public ReferralResponse refer(CurrentUser user, CreateReferralRequest req) {
        ReferralEntity r = new ReferralEntity();
        r.setJobId(req.jobId());
        r.setKind(req.kind());
        r.setFromUserId(user.userId());
        r.setToUserId(req.toUserId());
        r.setCompany(req.company());
        r.setNote(req.note());
        r.setStatus(ReferralEntity.ReferralStatus.open);
        return JobApi.toResponse(referralRepository.save(r));
    }

    public ReportResponse report(CurrentUser user, UUID id, CreateReportRequest req) {
        return reportService.create(user, new CreateReportRequest("job", id, req.reason(), req.detail()));
    }

    private JobEntity require(UUID id) {
        return jobRepository.findById(id).filter(j -> j.getStatus() != JobStatus.deleted)
                .orElseThrow(() -> BusinessException.notFound("Job not found"));
    }
}
