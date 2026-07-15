package com.manabandhu.backend.community.api;

import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.community.api.CommunityApi.CommentResponse;
import com.manabandhu.backend.community.api.CommunityApi.CommunityResponse;
import com.manabandhu.backend.community.api.CommunityApi.CreateCommentRequest;
import com.manabandhu.backend.community.api.CommunityApi.CreatePostRequest;
import com.manabandhu.backend.community.api.CommunityApi.PostResponse;
import com.manabandhu.backend.community.application.CommunityService;
import com.manabandhu.backend.moderation.api.ReportApi.CreateReportRequest;
import com.manabandhu.backend.moderation.api.ReportApi.ReportResponse;
import com.manabandhu.backend.moderation.application.ReportService;
import com.manabandhu.backend.security.AuthenticatedUser;
import com.manabandhu.backend.security.CurrentUser;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CommunityController {

    private final CommunityService communityService;
    private final ReportService reportService;

    public CommunityController(CommunityService communityService, ReportService reportService) {
        this.communityService = communityService;
        this.reportService = reportService;
    }

    @GetMapping("/communities")
    public PagedResult<CommunityResponse> search(
            @RequestParam(required = false) String kind,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return communityService.search(kind, category, city, state, query, page, size);
    }

    @GetMapping("/communities/{id}")
    public CommunityResponse get(@PathVariable UUID id) {
        return communityService.getCommunity(id);
    }

    @PostMapping("/communities/{id}/join")
    public ResponseEntity<Void> join(@AuthenticatedUser CurrentUser user, @PathVariable UUID id) {
        communityService.join(user, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/communities/{id}/leave")
    public ResponseEntity<Void> leave(@AuthenticatedUser CurrentUser user, @PathVariable UUID id) {
        communityService.leave(user, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/communities/{id}/feed")
    public PagedResult<PostResponse> feed(@PathVariable UUID id,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        return communityService.feed(id, page, size);
    }

    @PostMapping("/communities/{id}/posts")
    public PostResponse createPost(@AuthenticatedUser CurrentUser user, @PathVariable UUID id, @Valid @RequestBody CreatePostRequest req) {
        return communityService.createPost(user, id, req);
    }

    @PutMapping("/posts/{id}")
    public PostResponse updatePost(@AuthenticatedUser CurrentUser user, @PathVariable UUID id, @Valid @RequestBody CreatePostRequest req) {
        return communityService.updatePost(user, id, req);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@AuthenticatedUser CurrentUser user, @PathVariable UUID id) {
        communityService.deletePost(user, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/posts/{id}/comments")
    public CommentResponse addComment(@AuthenticatedUser CurrentUser user, @PathVariable UUID id, @Valid @RequestBody CreateCommentRequest req) {
        return communityService.addComment(user, id, req);
    }

    @GetMapping("/posts/{id}/comments")
    public List<CommentResponse> comments(@PathVariable UUID id) {
        return communityService.comments(id);
    }

    @PostMapping("/comments/{id}/helpful")
    public ResponseEntity<Void> helpful(@AuthenticatedUser CurrentUser user, @PathVariable UUID id) {
        communityService.helpfulVote(user, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/posts/{id}/report")
    public ReportResponse reportPost(@AuthenticatedUser CurrentUser user, @PathVariable UUID id, @Valid @RequestBody CreateReportRequest req) {
        return reportService.create(user, new CreateReportRequest("post", id, req.reason(), req.detail()));
    }
}
