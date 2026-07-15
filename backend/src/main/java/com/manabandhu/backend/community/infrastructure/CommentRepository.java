package com.manabandhu.backend.community.infrastructure;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {
    java.util.List<CommentEntity> findByPostIdOrderByCreatedAtAsc(UUID postId);
}
