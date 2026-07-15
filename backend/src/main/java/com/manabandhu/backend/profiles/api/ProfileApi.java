package com.manabandhu.backend.profiles.api;

import com.manabandhu.backend.profiles.infrastructure.ProfileEntity;
import java.util.UUID;

public final class ProfileApi {

    private ProfileApi() {
    }

    public record ProfileResponse(
            UUID id,
            String displayName,
            String avatarUrl,
            String bio,
            String city,
            String state,
            String[] languages,
            String occupation,
            String archetype,
            String university,
            String employer,
            String[] interests,
            int profileCompletionScore,
            double ratingAverage,
            int ratingCount,
            int contributionLevel,
            PrivacyResponse privacy,
            boolean onboardingCompleted,
            String role,
            String accountStatus) {
    }

    public record PrivacyResponse(
            boolean showCity,
            boolean showEmployer,
            boolean showUniversity,
            boolean allowDirectMessages,
            boolean showOnlineStatus) {
    }

    public record PublicProfileSummary(
            UUID id,
            String displayName,
            String avatarUrl,
            double ratingAverage,
            boolean emailVerified,
            boolean phoneVerified,
            boolean profileCompleted) {
    }

    public record ProfileUpdateRequest(
            String displayName,
            String bio,
            String city,
            String state,
            String[] languages,
            String occupation,
            String archetype,
            String university,
            String employer,
            String[] interests,
            PrivacyRequest privacy) {
    }

    public record PrivacyRequest(
            Boolean showCity,
            Boolean showEmployer,
            Boolean showUniversity,
            Boolean allowDirectMessages,
            Boolean showOnlineStatus) {
    }

    public record BlockRequest(UUID blockedUserId) {
    }

    public record SavedItemRequest(String itemType, UUID itemId) {
    }

    public record DataExportResponse(UUID requestId, String status, String message) {
    }

    public record DeletionRequest(String reason, boolean confirmation) {
    }

    public static ProfileResponse toResponse(ProfileEntity e) {
        return new ProfileResponse(
                e.getId(), e.getDisplayName(), e.getAvatarUrl(), e.getBio(), e.getCity(), e.getState(),
                e.getLanguages(), e.getOccupation(),
                e.getArchetype() == null ? null : e.getArchetype().name(),
                e.getUniversity(), e.getEmployer(), e.getInterests(),
                e.getProfileCompletionScore(), e.getRatingAverage(), e.getRatingCount(), e.getContributionLevel(),
                parsePrivacy(e.getPrivacy()), e.isOnboardingCompleted(),
                e.getRole().name(), e.getAccountStatus().name());
    }

    public static PublicProfileSummary toSummary(ProfileEntity e) {
        return new PublicProfileSummary(
                e.getId(), e.getDisplayName(), e.getAvatarUrl(), e.getRatingAverage(),
                false, false, e.getProfileCompletionScore() >= 80);
    }

    private static PrivacyResponse parsePrivacy(String json) {
        if (json == null || json.isBlank()) {
            return new PrivacyResponse(true, true, true, true, true);
        }
        boolean showCity = !json.contains("\"showCity\":false");
        boolean showEmployer = !json.contains("\"showEmployer\":false");
        boolean showUniversity = !json.contains("\"showUniversity\":false");
        boolean allowDirectMessages = !json.contains("\"allowDirectMessages\":false");
        boolean showOnlineStatus = !json.contains("\"showOnlineStatus\":false");
        return new PrivacyResponse(showCity, showEmployer, showUniversity, allowDirectMessages, showOnlineStatus);
    }
}
