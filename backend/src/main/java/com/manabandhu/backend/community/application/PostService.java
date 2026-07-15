package com.manabandhu.backend.community.application;

import com.manabandhu.backend.common.application.RateLimiter;
import com.manabandhu.backend.common.exception.BusinessException;
import com.manabandhu.backend.community.domain.Community;
import com.manabandhu.backend.community.domain.Post;
import com.manabandhu.backend.community.domain.Report;
import com.manabandhu.backend.community.domain.SavedItem;
import com.manabandhu.backend.community.infrastructure.CommunityRepository;
import com.manabandhu.backend.community.infrastructure.CommunityMembershipRepository;
import com.manabandhu.backend.community.infrastructure.PostRepository;
import com.manabandhu.backend.community.infrastructure.ReportRepository;
import com.manabandhu.backend.community.infrastructure.SavedItemRepository;
import com.manabandhu.backend.community.infrastructure.DtoMapper;
import com.manabandhu.backend.community.api.dto.PostDto;
import com.manabandhu.backend.community.api.request.CreatePostRequest;
import com.manabandhu.backend.community.api.request.UpdatePostRequest;
import com.manabandhu.backend.security.Authorization;
import com.manabandhu.backend.security.CurrentUser;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;
    private final CommunityMembershipRepository membershipRepository;
    private final SavedItemRepository savedItemRepository;
    private final ReportRepository reportRepository;
    private final CommentService commentService;
    private final RateLimiter rateLimiter;

    public PostService(PostRepository postRepository, CommunityRepository communityRepository,
                       CommunityMembershipRepository membershipRepository, SavedItemRepository savedItemRepository,
                       ReportRepository reportRepository, CommentService commentService, RateLimiter rateLimiter) {
        this.postRepository = postRepository;
        this.communityRepository = communityRepository;
        this.membershipRepository = membershipRepository;
        this.savedItemRepository = savedItemRepository;
        this.reportRepository = reportRepository;
        this.commentService = commentService;
        this.rateLimiter = rateLimiter;
    }

    public PostDto createPost(UUID communityId, CreatePostRequest req, CurrentUser user) {
        rateLimiter.check("post-create", user.userId().toString());
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> BusinessException.notFound("Community not found"));

        if ("topic".equals(community.getKind())) {
            boolean isMember = membershipRepository.existsById_CommunityIdAndId_UserId(communityId, user.userId());
            if (!isMember) {
                throw BusinessException.forbidden("You must be a member to post in topic communities");
            }
        }

        Post post = new Post();
        post.setCommunityId(communityId);
        post.setAuthorId(user.userId());
        post.setTitle(req.title());
        post.setBody(req.body());
        post.setTags(DtoMapper.formatTags(req.tags()));
        post.setPinned(false);
        post.setReactions(Map.of("like", 0, "helpful", 0, "celebrate", 0, "support", 0));
        post.setStatus("published");
        post.setCreatedBy(user.userId());
        post.setUpdatedBy(user.userId());
        post.setCreatedAt(Instant.now());
        postRepository.save(post);
        return DtoMapper.toDto(post, user.email(), false, null, 0);
    }

    public Page<PostDto> getFeed(UUID communityId, int page, int size, String sort, CurrentUser user) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> BusinessException.notFound("Community not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("pinned").descending().and(Sort.by("createdAt").descending()));
        Page<Post> posts = postRepository.findByCommunityIdAndStatus(communityId, "published", pageable);

        return posts.map(post -> {
            boolean saved = savedItemRepository.existsByUserIdAndItemTypeAndItemId(user.userId(), "post", post.getId());
            long commentCount = commentService.countPublishedComments(post.getId());
            return DtoMapper.toDto(post, user.email(), saved, null, commentCount);
        });
    }

    public PostDto updatePost(UUID postId, UpdatePostRequest req, CurrentUser user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> BusinessException.notFound("Post not found"));

        if (!post.getAuthorId().equals(user.userId()) && !user.isModerator()) {
            throw BusinessException.forbidden("You can only edit your own post");
        }

        post.setTitle(req.title());
        post.setBody(req.body());
        post.setTags(DtoMapper.formatTags(req.tags()));
        post.setUpdatedBy(user.userId());
        postRepository.save(post);
        boolean saved = savedItemRepository.existsByUserIdAndItemTypeAndItemId(user.userId(), "post", post.getId());
        long commentCount = commentService.countPublishedComments(post.getId());
        return DtoMapper.toDto(post, user.email(), saved, null, commentCount);
    }

    public void deletePost(UUID postId, CurrentUser user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> BusinessException.notFound("Post not found"));

        if (!post.getAuthorId().equals(user.userId()) && !user.isModerator()) {
            throw BusinessException.forbidden("You can only delete your own post");
        }

        post.setStatus(user.isModerator() ? "moderated" : "deleted");
        post.setUpdatedBy(user.userId());
        postRepository.save(post);
    }

    public PostDto reactPost(UUID postId, String reaction, CurrentUser user) {
        rateLimiter.check("post-react", user.userId().toString());
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> BusinessException.notFound("Post not found"));

        if (!List.of("like", "helpful", "celebrate", "support").contains(reaction)) {
            throw BusinessException.validation("Invalid reaction type", Map.of("reaction", "must be like, helpful, celebrate, or support"));
        }

        Map<String, Integer> reactions = post.getReactions();
        reactions.put(reaction, reactions.getOrDefault(reaction, 0) + 1);
        post.setReactions(reactions);
        postRepository.save(post);

        boolean saved = savedItemRepository.existsByUserIdAndItemTypeAndItemId(user.userId(), "post", post.getId());
        long commentCount = commentService.countPublishedComments(post.getId());
        return DtoMapper.toDto(post, user.email(), saved, reaction, commentCount);
    }

    public void savePost(UUID postId, CurrentUser user) {
        rateLimiter.check("post-save", user.userId().toString());
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> BusinessException.notFound("Post not found"));

        if (savedItemRepository.existsByUserIdAndItemTypeAndItemId(user.userId(), "post", post.getId())) {
            throw BusinessException.conflict("Post already saved");
        }

        SavedItem item = new SavedItem(user.userId(), "post", post.getId());
        savedItemRepository.save(item);
    }

    public void reportPost(UUID postId, com.manabandhu.backend.community.api.request.ReportRequest req, CurrentUser user) {
        rateLimiter.check("post-report", user.userId().toString());
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> BusinessException.notFound("Post not found"));

        Report report = new Report();
        report.setReporterId(user.userId());
        report.setTargetType("post");
        report.setTargetId(postId);
        report.setReason(req.reason());
        report.setDetail(req.detail());
        report.setStatus("open");
        report.setUpdatedAt(Instant.now());
        reportRepository.save(report);
    }

    public PostDto getPost(UUID postId, CurrentUser user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> BusinessException.notFound("Post not found"));
        boolean saved = savedItemRepository.existsByUserIdAndItemTypeAndItemId(user.userId(), "post", post.getId());
        long commentCount = commentService.countPublishedComments(post.getId());
        return DtoMapper.toDto(post, user.email(), saved, null, commentCount);
    }

    public Post getPostEntity(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> BusinessException.notFound("Post not found"));
    }
}
