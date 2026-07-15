package com.manabandhu.backend.community.infrastructure;

import com.manabandhu.backend.community.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByPostIdAndParentId(UUID postId, UUID parentId);
    List<Comment> findByPostIdAndParentIdIsNull(UUID postId);
    long countByPostIdAndStatus(UUID postId, String status);
    List<Comment> findByPostIdAndStatus(UUID postId, String status);
    long countByPostIdAndStatusAndAuthorId(UUID postId, String status, UUID authorId);
}
