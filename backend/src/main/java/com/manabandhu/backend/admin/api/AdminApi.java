package com.manabandhu.backend.admin.api;

import com.manabandhu.backend.profiles.infrastructure.ProfileEntity;
import java.util.UUID;

public final class AdminApi {

    private AdminApi() {
    }

    public record DashboardMetrics(
            long totalUsers, long activeUsers, long openReports, long newListingsToday,
            long newRidesToday, long newPostsToday, long messagesToday, long flaggedContent) {
    }

    public record UserSummary(
            UUID id, String displayName, String city, String state, String role, String accountStatus) {
    }

    public record RoleRequest(String role) {
    }

    public static UserSummary toSummary(ProfileEntity e) {
        return new UserSummary(e.getId(), e.getDisplayName(), e.getCity(), e.getState(),
                e.getRole().name(), e.getAccountStatus().name());
    }
}
