package com.manabandhu.backend.jobs.infrastructure;

import com.manabandhu.backend.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
public class JobEntity extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String location;

    @Column(length = 2)
    private String state;

    @Column(nullable = false)
    private String workMode;

    @Column(nullable = false)
    private String employmentType;

    @Column(nullable = false)
    private String experienceLevel;

    @Column(precision = 12, scale = 2)
    private BigDecimal salaryMin;

    @Column(precision = 12, scale = 2)
    private BigDecimal salaryMax;

    @Column(nullable = false)
    private String currency = "USD";

    @Column(columnDefinition = "text[]")
    private String[] skills = new String[0];

    private Boolean sponsorshipOffered;

    @Column(nullable = false)
    private String source;

    private String applicationUrl;

    private UUID postedById;

    private LocalDate expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status = JobStatus.active;

    @Column
    private LocalDate deletedAt;

    public enum JobStatus { active, closed, deleted }
}
