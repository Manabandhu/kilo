package com.manabandhu.backend.immigration.api;

import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.immigration.api.ImmigrationApi.CategoryResponse;
import com.manabandhu.backend.immigration.api.ImmigrationApi.CreateQuestionRequest;
import com.manabandhu.backend.immigration.api.ImmigrationApi.CreateResourceRequest;
import com.manabandhu.backend.immigration.api.ImmigrationApi.QuestionResponse;
import com.manabandhu.backend.immigration.api.ImmigrationApi.ResourceResponse;
import com.manabandhu.backend.immigration.application.ImmigrationService;
import com.manabandhu.backend.security.AuthenticatedUser;
import com.manabandhu.backend.security.CurrentUser;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/immigration")
public class ImmigrationController {

    private final ImmigrationService service;

    public ImmigrationController(ImmigrationService service) {
        this.service = service;
    }

    @GetMapping("/categories")
    public List<CategoryResponse> categories() {
        return service.categories();
    }

    @GetMapping("/resources")
    public PagedResult<ResourceResponse> resources(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.resources(categoryId, type, query, page, size);
    }

    @GetMapping("/resources/{id}")
    public ResourceResponse resource(@PathVariable UUID id) {
        return service.resource(id);
    }

    @PostMapping("/resources")
    public ResourceResponse create(@AuthenticatedUser CurrentUser user, @Valid @RequestBody CreateResourceRequest req) {
        return service.create(user, req);
    }

    @PostMapping("/resources/{id}/helpful")
    public ResponseEntity<Void> helpful(@PathVariable UUID id) {
        service.helpful(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/questions")
    public QuestionResponse ask(@AuthenticatedUser CurrentUser user, @Valid @RequestBody CreateQuestionRequest req) {
        return service.ask(user, req);
    }

    @PostMapping("/questions/{id}/answer")
    public QuestionResponse answer(@AuthenticatedUser CurrentUser user, @PathVariable UUID id, @RequestParam String answer) {
        return service.answer(user, id, answer);
    }
}
