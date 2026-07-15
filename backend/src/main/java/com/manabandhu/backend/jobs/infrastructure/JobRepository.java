package com.manabandhu.backend.jobs.infrastructure;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, UUID> {

    @Query("""
           select j from JobEntity j
           where j.status <> 'deleted' and j.deletedAt is null
             and (j.expiresAt is null or j.expiresAt >= current_date)
             and (:workMode is null or j.workMode = :workMode)
             and (:employmentType is null or j.employmentType = :employmentType)
             and (:experienceLevel is null or j.experienceLevel = :experienceLevel)
             and (:state is null or j.state = :state)
             and (:sponsorship is null or j.sponsorshipOffered = :sponsorship)
             and (:minSalary is null or (j.salaryMax is not null and j.salaryMax >= :minSalary))
             and (:query is null or lower(j.title || ' ' || j.company || ' ' || j.location) like lower('%' || :query || '%'))
           """)
    Page<JobEntity> search(@Param("workMode") String workMode, @Param("employmentType") String employmentType,
                           @Param("experienceLevel") String experienceLevel, @Param("state") String state,
                           @Param("sponsorship") Boolean sponsorship, @Param("minSalary") java.math.BigDecimal minSalary,
                           @Param("query") String query, Pageable pageable);
}
