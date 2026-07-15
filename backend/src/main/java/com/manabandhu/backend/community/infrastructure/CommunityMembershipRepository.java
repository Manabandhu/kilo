package com.manabandhu.backend.community.infrastructure;

import com.manabandhu.backend.community.domain.CommunityMembership;
import com.manabandhu.backend.community.domain.CommunityMembershipId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CommunityMembershipRepository extends JpaRepository<CommunityMembership, CommunityMembershipId> {
    boolean existsById_CommunityIdAndId_UserId(UUID communityId, UUID userId);
    Optional<CommunityMembership> findById_CommunityIdAndId_UserId(UUID communityId, UUID userId);
    long countById_CommunityId(UUID communityId);
}
