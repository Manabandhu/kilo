package com.manabandhu.backend.immigration.api;

import com.manabandhu.backend.immigration.infrastructure.ImmigrationCategoryEntity;
import com.manabandhu.backend.immigration.infrastructure.ImmigrationQuestionEntity;
import com.manabandhu.backend.immigration.infrastructure.ImmigrationResourceEntity;
import java.time.LocalDate;
import java.util.UUID;

public final class ImmigrationApi {

    public static final String DISCLAIMER =
            "This content is general educational information and is not legal advice. Consult a licensed immigration attorney for your situation.";

    private ImmigrationApi() {
    }

    public record CategoryResponse(UUID id, String slug, String name, String description, int orderIndex) {
    }

    public record ResourceResponse(
            UUID id, UUID categoryId, String type, String title, String body, String[] tags,
            LocalDate versionDate, String lastReviewedAt, String[] sourceReferences, boolean trustedContributor,
            int helpfulCount, String status, String disclaimer) {
    }

    public record CreateResourceRequest(UUID categoryId, String type, String title, String body, String[] tags, String[] sourceReferences) {
    }

    public record QuestionResponse(UUID id, UUID resourceId, String body, String answer, String status, String createdAt) {
    }

    public record CreateQuestionRequest(UUID resourceId, String body) {
    }

    public static CategoryResponse toResponse(ImmigrationCategoryEntity e) {
        return new CategoryResponse(e.getId(), e.getSlug(), e.getName(), e.getDescription(), e.getOrderIndex());
    }

    public static ResourceResponse toResponse(ImmigrationResourceEntity e) {
        return new ResourceResponse(e.getId(), e.getCategoryId(), e.getType(), e.getTitle(), e.getBody(),
                e.getTags(), e.getVersionDate(), e.getLastReviewedAt().toString(), e.getSourceReferences(),
                e.isTrustedContributor(), e.getHelpfulCount(), e.getStatus().name(), DISCLAIMER);
    }

    public static QuestionResponse toResponse(ImmigrationQuestionEntity e) {
        return new QuestionResponse(e.getId(), e.getResourceId(), e.getBody(), e.getAnswer(),
                e.getStatus().name(), e.getCreatedAt().toString());
    }
}
