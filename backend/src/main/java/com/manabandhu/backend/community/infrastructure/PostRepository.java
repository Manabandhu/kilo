package com.manabandhu.backend.community.infrastructure;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, UUID> {
    Page<PostEntity> findByCommunityIdAndStatus(Pageable pageable, UUID communityId, PostEntity.PostStatus status);
    Page<PostEntity> findByCommunityId(Pageable pageable, UUID communityId);
}
