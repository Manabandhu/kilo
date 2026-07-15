package com.manabandhu.backend.security;

import java.util.List;
import java.util.UUID;

public record CurrentUser(
        UUID userId,
        String email,
        String role, // user | moderator | admin
        List<String> scopes) {

    public boolean isAdmin() {
        return "admin".equals(role);
    }

    public boolean isModerator() {
        return "moderator".equals(role) || "admin".equals(role);
    }
}
