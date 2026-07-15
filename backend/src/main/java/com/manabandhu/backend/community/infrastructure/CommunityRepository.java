package com.manabandhu.backend.community.infrastructure;

import com.manabandhu.backend.community.domain.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;
import java.util.UUID;

public interface CommunityRepository extends JpaRepository<Community, UUID>, JpaSpecificationExecutor<Community> {
    Optional<Community> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
