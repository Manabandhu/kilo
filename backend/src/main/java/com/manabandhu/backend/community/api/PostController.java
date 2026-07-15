package com.manabandhu.backend.community.api;

import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.community.api.dto.CommentDto;
import com.manabandhu.backend.community.api.dto.PostDto;
import com.manabandhu.backend.community.application.PostService;
import com.manabandhu.backend.community.application.CommentService;
import com.manabandhu.backend.security.Authorization;
import com.manabandhu.backend.security.AuthenticatedUser;
import com.manabandhu.backend.security.CurrentUser;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> get(@PathVariable UUID id, @AuthenticatedUser CurrentUser user) {
        return ResponseEntity.ok(postService.getPost(id, user));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PostDto> update(@PathVariable UUID id, @Valid @RequestBody com.manabandhu.backend.community.api.request.UpdatePostRequest req, @AuthenticatedUser CurrentUser user) {
        return ResponseEntity.ok(postService.updatePost(id, req, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @AuthenticatedUser CurrentUser user) {
        postService.deletePost(id, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/react")
    public ResponseEntity<PostDto> react(@PathVariable UUID id, @Valid @RequestBody com.manabandhu.backend.community.api.request.ReactionRequest req, @AuthenticatedUser CurrentUser user) {
        return ResponseEntity.ok(postService.reactPost(id, req.reaction(), user));
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<Void> save(@PathVariable UUID id, @AuthenticatedUser CurrentUser user) {
        postService.savePost(id, user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{id}/report")
    public ResponseEntity<Void> report(@PathVariable UUID id, @Valid @RequestBody com.manabandhu.backend.community.api.request.ReportRequest req, @AuthenticatedUser CurrentUser user) {
        postService.reportPost(id, req, user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDto>> comments(@PathVariable UUID id, @AuthenticatedUser CurrentUser user) {
        return ResponseEntity.ok(commentService.getCommentTree(id, user));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable UUID id, @Valid @RequestBody com.manabandhu.backend.community.api.request.CreateCommentRequest req, @AuthenticatedUser CurrentUser user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(id, req, user));
    }
}
