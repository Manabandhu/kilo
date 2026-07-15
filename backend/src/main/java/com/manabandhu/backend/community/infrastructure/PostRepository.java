package com.manabandhu.backend.community.infrastructure;

import com.manabandhu.backend.community.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    Page<Post> findByCommunityIdAndStatus(UUID communityId, String status, Pageable pageable);
    long countByCommunityIdAndStatus(UUID communityId, String status);
    List<Post> findByCommunityIdAndStatus(UUID communityId, String status);
    List<Post> findByAuthorId(UUID authorId);
}
