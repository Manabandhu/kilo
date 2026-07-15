package com.manabandhu.backend.security;

import com.manabandhu.backend.common.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.security.Key;
import java.util.List;
import java.util.UUID;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Validates Supabase-issued JWTs (HS256). Enforces signature, issuer, audience and expiry.
 * The JWT secret is a backend-only secret and must never reach the frontend.
 */
@Service
public class JwtService {

    private final Key key;
    private final String issuer;
    private final String audience;

    public JwtService(
            @Value("${supabase.jwt.secret}") String secret,
            @Value("${supabase.jwt.issuer:https://supabase.co/auth/v1}") String issuer,
            @Value("${supabase.jwt.audience:authenticated}") String audience) {
        this.key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        this.issuer = issuer;
        this.audience = audience;
    }

    public CurrentUser parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(issuer)
                    .requireAudience(audience)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            UUID userId = UUID.fromString(claims.getSubject());
            String email = claims.get("email", String.class);
            String role = claims.get("role", String.class);
            if (role == null) {
                role = "user";
            }
            List<String> scopes = claims.get("scopes", List.class);
            return new CurrentUser(userId, email, role, scopes == null ? List.of() : scopes);
        } catch (JwtException | IllegalArgumentException ex) {
            throw BusinessException.unauthorized("Invalid or expired token");
        }
    }
}
