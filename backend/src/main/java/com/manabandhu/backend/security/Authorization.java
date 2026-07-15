package com.manabandhu.backend.security;

import com.manabandhu.backend.common.exception.BusinessException;
import java.util.UUID;

/** Server-side authorization helpers. Never trust frontend-supplied user ids. */
public final class Authorization {

    private Authorization() {
    }

    public static void requireOwner(CurrentUser user, UUID ownerId) {
        if (!user.userId().equals(ownerId)) {
            throw BusinessException.forbidden("You can only modify your own resource");
        }
    }

    public static void requireOwnership(CurrentUser user, UUID ownerId) {
        requireOwner(user, ownerId);
    }

    public static void requireStaff(CurrentUser user) {
        if (!user.isModerator()) {
            throw BusinessException.forbidden("Staff access required");
        }
    }

    public static void requireAdmin(CurrentUser user) {
        if (!user.isAdmin()) {
            throw BusinessException.forbidden("Admin access required");
        }
    }

    public static void requireActiveAccount(CurrentUser user) {
        // role/status checks are enforced via JWT claims; placeholder for status gating.
        if (user == null) {
            throw BusinessException.unauthorized("Authentication required");
        }
    }
}
