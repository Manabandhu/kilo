package com.manabandhu.backend.profiles.api;

import com.manabandhu.backend.profiles.api.ProfileApi.BlockRequest;
import com.manabandhu.backend.profiles.api.ProfileApi.DeletionRequest;
import com.manabandhu.backend.profiles.api.ProfileApi.ProfileResponse;
import com.manabandhu.backend.profiles.api.ProfileApi.ProfileUpdateRequest;
import com.manabandhu.backend.profiles.api.ProfileApi.PublicProfileSummary;
import com.manabandhu.backend.profiles.api.ProfileApi.SavedItemRequest;
import com.manabandhu.backend.profiles.application.ProfileService;
import com.manabandhu.backend.security.AuthenticatedUser;
import com.manabandhu.backend.security.CurrentUser;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public ProfileResponse me(@AuthenticatedUser CurrentUser user) {
        return service.getMe(user);
    }

    @GetMapping("/{id}")
    public PublicProfileSummary get(@PathVariable UUID id) {
        return service.getPublic(id);
    }

    @PutMapping("/me")
    public ProfileResponse update(@AuthenticatedUser CurrentUser user, @Valid @RequestBody ProfileUpdateRequest req) {
        return service.update(user, req);
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<Void> block(@AuthenticatedUser CurrentUser user, @PathVariable UUID id) {
        service.block(user, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/block")
    public ResponseEntity<Void> unblock(@AuthenticatedUser CurrentUser user, @PathVariable UUID id) {
        service.unblock(user, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/blocked")
    public java.util.List<PublicProfileSummary> blocked(@AuthenticatedUser CurrentUser user) {
        return service.blocked(user);
    }

    @PostMapping("/me/saved")
    public ResponseEntity<Void> save(@AuthenticatedUser CurrentUser user, @Valid @RequestBody SavedItemRequest req) {
        service.saveItem(user, req.itemType(), req.itemId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/saved")
    public ResponseEntity<Void> unsave(@AuthenticatedUser CurrentUser user, @Valid @RequestBody SavedItemRequest req) {
        service.unsaveItem(user, req.itemType(), req.itemId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/export")
    public ProfileApi.DataExportResponse export(@AuthenticatedUser CurrentUser user) {
        return service.requestExport(user);
    }

    @PostMapping("/me/deletion-request")
    public ResponseEntity<Void> deleteRequest(@AuthenticatedUser CurrentUser user, @Valid @RequestBody DeletionRequest req) {
        service.requestDeletion(user, req);
        return ResponseEntity.accepted().build();
    }
}
