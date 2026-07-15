package com.manabandhu.backend.community.api;

import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.community.api.dto.CommunityDto;
import com.manabandhu.backend.community.api.dto.MembershipDto;
import com.manabandhu.backend.community.application.CommunityService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/communities")
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @GetMapping
    public ResponseEntity<PagedResult<CommunityDto>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String kind,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        var result = communityService.listCommunities(search, kind, category, city, state, page, size, sort);
        return ResponseEntity.ok(PagedResult.of(result.getContent(), page, size, result.getTotalElements()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityDto> get(@PathVariable UUID id) {
        return ResponseEntity.ok(communityService.getCommunity(id));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<MembershipDto> join(@PathVariable UUID id, @AuthenticatedUser CurrentUser user) {
        Authorization.requireActiveAccount(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(communityService.joinCommunity(id, user));
    }

    @DeleteMapping("/{id}/leave")
    public ResponseEntity<Void> leave(@PathVariable UUID id, @AuthenticatedUser CurrentUser user) {
        Authorization.requireActiveAccount(user);
        communityService.leaveCommunity(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/feed")
    public ResponseEntity<PagedResult<com.manabandhu.backend.community.api.dto.PostDto>> feed(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @AuthenticatedUser CurrentUser user) {
        Authorization.requireActiveAccount(user);
        var result = communityService.getFeed(id, page, size, sort, user);
        return ResponseEntity.ok(PagedResult.of(result.getContent(), page, size, result.getTotalElements()));
    }

    @PostMapping("/{id}/posts")
    public ResponseEntity<com.manabandhu.backend.community.api.dto.PostDto> createPost(
            @PathVariable UUID id,
            @Valid com.manabandhu.backend.community.api.request.CreatePostRequest req,
            @AuthenticatedUser CurrentUser user) {
        Authorization.requireActiveAccount(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(communityService.createPost(id, req, user));
    }
}
