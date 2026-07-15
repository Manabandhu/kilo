package com.manabandhu.backend.jobs.api;

import com.manabandhu.backend.jobs.infrastructure.JobEntity;
import com.manabandhu.backend.jobs.infrastructure.ReferralEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class JobApi {

    private JobApi() {
    }

    public record JobResponse(
            UUID id, String title, String company, String location, String state, String workMode,
            String employmentType, String experienceLevel, String salaryMin, String salaryMax, String currency,
            String[] skills, Boolean sponsorshipOffered, String source, String applicationUrl,
            UUID postedById, LocalDate expiresAt, String status, String postedAt) {
    }

    public record CreateJobRequest(
            String title, String company, String location, String state, String workMode, String employmentType,
            String experienceLevel, String salaryMin, String salaryMax, String currency, String[] skills,
            Boolean sponsorshipOffered, String applicationUrl, LocalDate expiresAt) {
    }

    public record CreateReferralRequest(UUID jobId, String kind, UUID toUserId, String company, String note) {
    }

    public record ReferralResponse(
            UUID id, UUID jobId, String kind, UUID fromUserId, UUID toUserId, String company, String note, String status) {
    }

    public static JobResponse toResponse(JobEntity e) {
        return new JobResponse(e.getId(), e.getTitle(), e.getCompany(), e.getLocation(), e.getState(), e.getWorkMode(),
                e.getEmploymentType(), e.getExperienceLevel(),
                e.getSalaryMin() == null ? null : e.getSalaryMin().toPlainString(),
                e.getSalaryMax() == null ? null : e.getSalaryMax().toPlainString(), e.getCurrency(),
                e.getSkills(), e.getSponsorshipOffered(), e.getSource(), e.getApplicationUrl(),
                e.getPostedById(), e.getExpiresAt(), e.getStatus().name(), e.getCreatedAt().toString());
    }

    public static JobEntity toEntity(CreateJobRequest r, UUID posterId) {
        JobEntity e = new JobEntity();
        e.setTitle(r.title());
        e.setCompany(r.company());
        e.setLocation(r.location());
        e.setState(r.state());
        e.setWorkMode(r.workMode());
        e.setEmploymentType(r.employmentType());
        e.setExperienceLevel(r.experienceLevel());
        e.setSalaryMin(r.salaryMin() == null ? null : new BigDecimal(r.salaryMin()));
        e.setSalaryMax(r.salaryMax() == null ? null : new BigDecimal(r.salaryMax()));
        e.setCurrency(r.currency() == null ? "USD" : r.currency());
        e.setSkills(r.skills() == null ? new String[0] : r.skills());
        e.setSponsorshipOffered(r.sponsorshipOffered());
        e.setApplicationUrl(r.applicationUrl());
        e.setExpiresAt(r.expiresAt());
        e.setPostedById(posterId);
        e.setSource("community");
        e.setStatus(JobEntity.JobStatus.active);
        return e;
    }

    public static ReferralResponse toResponse(ReferralEntity e) {
        return new ReferralResponse(e.getId(), e.getJobId(), e.getKind(), e.getFromUserId(),
                e.getToUserId(), e.getCompany(), e.getNote(), e.getStatus().name());
    }
}
