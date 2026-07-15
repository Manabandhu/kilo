package com.manabandhu.backend.community.infrastructure;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityMembershipRepository extends JpaRepository<CommunityMembershipEntity, UUID> {
    boolean existsByCommunityIdAndUserId(UUID communityId, UUID userId);
    void deleteByCommunityIdAndUserId(UUID communityId, UUID userId);
}
