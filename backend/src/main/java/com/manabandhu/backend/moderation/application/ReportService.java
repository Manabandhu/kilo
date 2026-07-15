package com.manabandhu.backend.moderation.application;

import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.common.exception.BusinessException;
import com.manabandhu.backend.moderation.api.ReportApi;
import com.manabandhu.backend.moderation.api.ReportApi.CreateReportRequest;
import com.manabandhu.backend.moderation.infrastructure.ReportEntity;
import com.manabandhu.backend.moderation.infrastructure.ReportEntity.ReportStatus;
import com.manabandhu.backend.moderation.infrastructure.ReportRepository;
import com.manabandhu.backend.security.Authorization;
import com.manabandhu.backend.security.CurrentUser;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportRepository repository;

    public ReportService(ReportRepository repository) {
        this.repository = repository;
    }

    /** Used by any domain controller to record a user report. */
    @Transactional
    public ReportApi.ReportResponse create(CurrentUser user, CreateReportRequest req) {
        ReportEntity e = new ReportEntity();
        e.setReporterId(user.userId());
        e.setTargetType(req.targetType());
        e.setTargetId(req.targetId());
        e.setReason(req.reason());
        e.setDetail(req.detail());
        e.setStatus(ReportStatus.open);
        return ReportApi.toResponse(repository.save(e));
    }

    public PagedResult<ReportApi.ReportResponse> queue(String status, int page, int size) {
        PageRequest pr = PageRequest.of(page, size);
        Page<ReportEntity> p = repository.findAll(pr); // staff-only; filtered upstream by security
        return PagedResult.of(p.getContent().stream().map(ReportApi::toResponse).toList(), page, size, p.getTotalElements());
    }

    public ReportApi.ReportResponse get(UUID id) {
        return ReportApi.toResponse(repository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Report not found")));
    }

    @Transactional
    public ReportApi.ReportResponse resolve(CurrentUser staff, UUID id, String status, String resolution) {
        Authorization.requireStaff(staff);
        ReportEntity e = repository.findById(id).orElseThrow(() -> BusinessException.notFound("Report not found"));
        e.setStatus(ReportStatus.valueOf(status));
        e.setResolution(resolution);
        e.setAssignedToId(staff.userId());
        return ReportApi.toResponse(repository.save(e));
    }
}
