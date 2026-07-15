package com.manabandhu.backend.community.application;

import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.common.exception.BusinessException;
import com.manabandhu.backend.community.api.CommunityApi;
import com.manabandhu.backend.community.api.CommunityApi.CreateCommentRequest;
import com.manabandhu.backend.community.api.CommunityApi.CreatePostRequest;
import com.manabandhu.backend.community.infrastructure.CommentEntity;
import com.manabandhu.backend.community.infrastructure.CommentRepository;
import com.manabandhu.backend.community.infrastructure.CommunityEntity;
import com.manabandhu.backend.community.infrastructure.CommunityMembershipEntity;
import com.manabandhu.backend.community.infrastructure.CommunityMembershipRepository;
import com.manabandhu.backend.community.infrastructure.CommunityRepository;
import com.manabandhu.backend.community.infrastructure.PostEntity;
import com.manabandhu.backend.community.infrastructure.PostEntity.PostStatus;
import com.manabandhu.backend.community.infrastructure.PostRepository;
import com.manabandhu.backend.security.Authorization;
import com.manabandhu.backend.security.CurrentUser;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMembershipRepository membershipRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private static final int MAX_COMMENT_DEPTH = 4;

    public CommunityService(CommunityRepository communityRepository,
                            CommunityMembershipRepository membershipRepository,
                            PostRepository postRepository,
                            CommentRepository commentRepository) {
        this.communityRepository = communityRepository;
        this.membershipRepository = membershipRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public CommunityApi.CommunityResponse getCommunity(UUID id) {
        return communityRepository.findById(id)
                .map(CommunityApi::toResponse)
                .orElseThrow(() -> BusinessException.notFound("Community not found"));
    }

    public PagedResult<CommunityApi.CommunityResponse> search(String kind, String category, String city,
                                                              String state, String query, int page, int size) {
        Page<CommunityEntity> p = communityRepository.search(kind, category, city, state, query, PageRequest.of(page, size));
        return PagedResult.of(p.getContent().stream().map(CommunityApi::toResponse).toList(), page, size, p.getTotalElements());
    }

    @Transactional
    public void join(CurrentUser user, UUID communityId) {
        if (!membershipRepository.existsByCommunityIdAndUserId(communityId, user.userId())) {
            CommunityMembershipEntity m = new CommunityMembershipEntity();
            m.setCommunityId(communityId);
            m.setUserId(user.userId());
            membershipRepository.save(m);
            communityRepository.findById(communityId).ifPresent(c -> {
                c.setMemberCount(c.getMemberCount() + 1);
                communityRepository.save(c);
            });
        }
    }

    @Transactional
    public void leave(CurrentUser user, UUID communityId) {
        membershipRepository.deleteByCommunityIdAndUserId(communityId, user.userId());
        communityRepository.findById(communityId).ifPresent(c -> {
            c.setMemberCount(Math.max(0, c.getMemberCount() - 1));
            communityRepository.save(c);
        });
    }

    public PagedResult<CommunityApi.PostResponse> feed(UUID communityId, int page, int size) {
        Page<PostEntity> p = postRepository.findByCommunityIdAndStatus(PageRequest.of(page, size), communityId, PostStatus.published);
        return PagedResult.of(p.getContent().stream().map(CommunityApi::toResponse).toList(), page, size, p.getTotalElements());
    }

    @Transactional
    public CommunityApi.PostResponse createPost(CurrentUser user, UUID communityId, CreatePostRequest req) {
        CommunityEntity community = communityRepository.findById(communityId)
                .orElseThrow(() -> BusinessException.notFound("Community not found"));
        if ("topic".equals(community.getKind()) && !membershipRepository.existsByCommunityIdAndUserId(communityId, user.userId())) {
            throw BusinessException.forbidden("Join the community to post");
        }
        PostEntity e = new PostEntity();
        e.setCommunityId(communityId);
        e.setAuthorId(user.userId());
        e.setTitle(req.title());
        e.setBody(req.body());
        e.setTags(req.tags() == null ? new String[0] : req.tags());
        e.setReactions("{\"like\":0,\"helpful\":0,\"celebrate\":0,\"support\":0}");
        e.setStatus(PostStatus.published);
        return CommunityApi.toResponse(postRepository.save(e));
    }

    @Transactional
    public CommunityApi.PostResponse updatePost(CurrentUser user, UUID postId, CreatePostRequest req) {
        PostEntity e = requirePost(postId);
        Authorization.requireOwner(user, e.getAuthorId());
        if (req.title() != null) e.setTitle(req.title());
        if (req.body() != null) e.setBody(req.body());
        if (req.tags() != null) e.setTags(req.tags());
        return CommunityApi.toResponse(postRepository.save(e));
    }

    @Transactional
    public void deletePost(CurrentUser user, UUID postId) {
        PostEntity e = requirePost(postId);
        if (!e.getAuthorId().equals(user.userId()) && !user.isModerator()) {
            throw BusinessException.forbidden("Not allowed to delete this post");
        }
        e.setStatus(PostStatus.moderated);
        postRepository.save(e);
    }

    @Transactional
    public CommunityApi.CommentResponse addComment(CurrentUser user, UUID postId, CreateCommentRequest req) {
        PostEntity post = requirePost(postId);
        if (req.parentId() != null) {
            CommentEntity parent = commentRepository.findById(req.parentId())
                    .orElseThrow(() -> BusinessException.notFound("Parent comment not found"));
            if (depthOf(parent) >= MAX_COMMENT_DEPTH) {
                throw BusinessException.businessRule("Maximum reply depth reached");
            }
        }
        CommentEntity c = new CommentEntity();
        c.setPostId(postId);
        c.setParentId(req.parentId());
        c.setAuthorId(user.userId());
        c.setBody(req.body());
        c.setStatus(CommentEntity.CommentStatus.published);
        return CommunityApi.toResponse(commentRepository.save(c));
    }

    public List<CommunityApi.CommentResponse> comments(UUID postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(CommunityApi::toResponse).toList();
    }

    @Transactional
    public void helpfulVote(CurrentUser user, UUID commentId) {
        CommentEntity c = commentRepository.findById(commentId)
                .orElseThrow(() -> BusinessException.notFound("Comment not found"));
        c.setHelpfulVotes(c.getHelpfulVotes() + 1);
        commentRepository.save(c);
    }

    private int depthOf(CommentEntity c) {
        int depth = 0;
        CommentEntity current = c;
        while (current.getParentId() != null) {
            final UUID pid = current.getParentId();
            current = commentRepository.findById(pid).orElse(null);
            if (current == null) break;
            depth++;
        }
        return depth;
    }

    private PostEntity requirePost(UUID id) {
        return postRepository.findById(id).filter(p -> p.getStatus() != PostStatus.deleted)
                .orElseThrow(() -> BusinessException.notFound("Post not found"));
    }
}
