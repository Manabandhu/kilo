package com.manabandhu.backend.community.api;

import com.manabandhu.backend.community.application.CommentService;
import com.manabandhu.backend.security.Authorization;
import com.manabandhu.backend.security.AuthenticatedUser;
import com.manabandhu.backend.security.CurrentUser;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<com.manabandhu.backend.community.api.dto.CommentDto> update(@PathVariable UUID id, @Valid @RequestBody com.manabandhu.backend.community.api.request.UpdateCommentRequest req, @AuthenticatedUser CurrentUser user) {
        return ResponseEntity.ok(commentService.updateComment(id, req, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @AuthenticatedUser CurrentUser user) {
        commentService.deleteComment(id, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/helpful")
    public ResponseEntity<Void> helpful(@PathVariable UUID id, @AuthenticatedUser CurrentUser user) {
        commentService.markHelpful(id, user);
        return ResponseEntity.ok().build();
    }
}
