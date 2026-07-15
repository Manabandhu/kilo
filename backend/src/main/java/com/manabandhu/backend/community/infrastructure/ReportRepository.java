package com.manabandhu.backend.community.infrastructure;

import com.manabandhu.backend.community.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
}
