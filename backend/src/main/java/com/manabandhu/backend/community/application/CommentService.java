package com.manabandhu.backend.community.application;

import com.manabandhu.backend.common.application.RateLimiter;
import com.manabandhu.backend.common.exception.BusinessException;
import com.manabandhu.backend.community.domain.Comment;
import com.manabandhu.backend.community.domain.Post;
import com.manabandhu.backend.community.infrastructure.CommentRepository;
import com.manabandhu.backend.community.infrastructure.PostRepository;
import com.manabandhu.backend.community.infrastructure.DtoMapper;
import com.manabandhu.backend.community.api.dto.CommentDto;
import com.manabandhu.backend.community.api.request.CreateCommentRequest;
import com.manabandhu.backend.community.api.request.UpdateCommentRequest;
import com.manabandhu.backend.security.Authorization;
import com.manabandhu.backend.security.CurrentUser;
import java.time.Instant;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final RateLimiter rateLimiter;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, RateLimiter rateLimiter) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.rateLimiter = rateLimiter;
    }

    public long countPublishedComments(UUID postId) {
        return commentRepository.countByPostIdAndStatus(postId, "published");
    }

    public List<CommentDto> getCommentTree(UUID postId, CurrentUser user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> BusinessException.notFound("Post not found"));

        List<Comment> all = commentRepository.findByPostIdAndStatus(postId, "published");
        Map<UUID, CommentDto> byId = new HashMap<>();
        for (Comment c : all) {
            byId.put(c.getId(), DtoMapper.toDto(c, user.email(), new ArrayList<>()));
        }

        for (Comment c : all) {
            if (c.getParentId() != null) {
                CommentDto parent = byId.get(c.getParentId());
                if (parent != null) {
                    List<CommentDto> replies = new ArrayList<>(parent.replies());
                    replies.add(byId.get(c.getId()));
                    byId.put(c.getParentId(), new CommentDto(parent.id(), parent.postId(), parent.parentId(), parent.authorId(), parent.authorName(), parent.body(), parent.helpfulVotes(), parent.status(), parent.createdAt(), parent.updatedAt(), replies));
                }
            }
        }

        List<CommentDto> roots = new ArrayList<>();
        for (Comment c : all) {
            if (c.getParentId() == null) {
                roots.add(byId.get(c.getId()));
            }
        }
        return roots;
    }

    public CommentDto createComment(UUID postId, CreateCommentRequest req, CurrentUser user) {
        rateLimiter.check("comment-create", user.userId().toString());
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> BusinessException.notFound("Post not found"));

        if (req.parentId() != null) {
            int depth = computeDepth(req.parentId());
            if (depth >= 4) {
                throw BusinessException.businessRule("Maximum comment depth exceeded");
            }
        }

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setParentId(req.parentId());
        comment.setAuthorId(user.userId());
        comment.setBody(req.body());
        comment.setHelpfulVotes(0);
        comment.setStatus("published");
        commentRepository.save(comment);
        return DtoMapper.toDto(comment, user.email(), List.of());
    }

    public CommentDto updateComment(UUID commentId, UpdateCommentRequest req, CurrentUser user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> BusinessException.notFound("Comment not found"));

        if (!comment.getAuthorId().equals(user.userId()) && !user.isModerator()) {
            throw BusinessException.forbidden("You can only edit your own comment");
        }

        comment.setBody(req.body());
        commentRepository.save(comment);
        return DtoMapper.toDto(comment, user.email(), List.of());
    }

    public void deleteComment(UUID commentId, CurrentUser user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> BusinessException.notFound("Comment not found"));

        if (!comment.getAuthorId().equals(user.userId()) && !user.isModerator()) {
            throw BusinessException.forbidden("You can only delete your own comment");
        }

        comment.setStatus(user.isModerator() ? "moderated" : "deleted");
        commentRepository.save(comment);
    }

    public void markHelpful(UUID commentId, CurrentUser user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> BusinessException.notFound("Comment not found"));

        comment.setHelpfulVotes(comment.getHelpfulVotes() + 1);
        commentRepository.save(comment);
    }

    private int computeDepth(UUID commentId) {
        int depth = 0;
        UUID current = commentId;
        while (depth < 5) {
            Optional<Comment> opt = commentRepository.findById(current);
            if (opt.isEmpty() || opt.get().getParentId() == null) break;
            current = opt.get().getParentId();
            depth++;
        }
        return depth;
    }
}
