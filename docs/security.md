# Security

## Threat Model & Principles
- **Zero trust frontend**: Never trust user IDs, roles, or any data from the client. All authorization derived from validated JWT claims.
- **Defense in depth**: Supabase RLS + Spring Boot service-layer authorization + API gateway rate limiting.
- **Least privilege**: Frontend uses only Supabase **anon key** (public). Service role key backend-only.
- **Secrets management**: All secrets via environment variables. `.env` never committed. `.env.example` documents required vars.

## Authentication (Supabase Auth)
- **Provider**: Supabase Auth (email/password, email verification, password reset, optional phone OTP architecture).
- **JWT**: HS256 signed, 1-hour access token, refresh token rotation.
- **Claims**: `sub` (UUID), `email`, `role` (`user`|`moderator`|`admin`), `scopes`.
- **Validation in Spring Boot**:
  - Signature verification with `SUPABASE_JWT_SECRET`
  - Issuer: `https://<project>.supabase.co/auth/v1`
  - Audience: `authenticated`
  - Expiry check
- **Session restoration**: SecureStore (mobile) / httpOnly cookie (web) for refresh token.

## Authorization
| Layer | Mechanism |
|-------|-----------|
| **API Gateway / Spring Security** | Stateless JWT filter → `CurrentUser` in `SecurityContext` |
| **Controller** | `@AuthenticatedUser CurrentUser` injection; method security `@PreAuthorize` |
| **Service** | `Authorization.requireOwner(user, resource.ownerId)`, `requireStaff()`, `requireAdmin()` |
| **Database (RLS)** | Row Level Security on every table; policies enforce ownership/membership |

**Never** use user IDs from request body/path for authorization decisions.

## Data Protection
| Data | Protection |
|------|------------|
| Passwords | bcrypt (Supabase) |
| JWT secret | Env var `SUPABASE_JWT_SECRET` (32+ bytes) |
| Service role key | Backend-only env var |
| Media uploads | Signed URLs (Supabase Storage), type/size validation |
| PII in logs | **Never** log tokens, passwords, private messages, SSN, visa details |
| Audit logs | Actor, action, entity, timestamp (no PII payload) |

## Rate Limiting & Abuse Prevention
- **Per-identity**: `app.rate-limit.per-minute` (default 120) via Spring Boot filter.
- **Auth endpoints**: Stricter limits on login/register/reset.
- **Report/Moderation**: Duplicate detection, cooldown.
- **File upload**: Max 10MB, allowed MIME types only.

## CORS & Headers
- `CORS_ALLOWED_ORIGINS` explicit allowlist (no `*`).
- Security headers: `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `Referrer-Policy: strict-origin-when-cross-origin`, CSP (report-only initially).
- HSTS in production.

## Input Validation
- **Frontend**: Zod schemas (shared with `@manabandhu/contracts`).
- **Backend**: Bean Validation (`@NotBlank`, `@Size`, `@Positive`, custom) on all DTOs.
- **Money**: String in API, `BigDecimal` in DB, `Money` utility for arithmetic.
- **File uploads**: Magic byte check, extension allowlist, size limit.

## Secrets & Configuration
| Variable | Scope | Description |
|----------|-------|-------------|
| `SUPABASE_JWT_SECRET` | Backend | HS256 key for JWT validation |
| `SUPABASE_SERVICE_ROLE_KEY` | Backend | Admin operations only |
| `SPRING_DATASOURCE_*` | Backend | Flyway/Integration test DB |
| `EXPO_PUBLIC_SUPABASE_URL` | Frontend | Supabase project URL |
| `EXPO_PUBLIC_SUPABASE_ANON_KEY` | Frontend | Public anon key |
| `EXPO_PUBLIC_API_URL` | Frontend | Spring Boot base URL |
| `CORS_ALLOWED_ORIGINS` | Backend | Comma-separated origins |
| `PUSH_PROVIDER` | Backend | `console` \| `firebase` \| `apns` |

## Incident Response
1. Rotate `SUPABASE_JWT_SECRET` and `SUPABASE_SERVICE_ROLE_KEY` immediately on suspected leak.
2. Revoke all refresh tokens via Supabase dashboard.
3. Audit `audit_logs` for suspicious actor/actions.
4. Deploy hotfix for rate-limit tightening if abuse detected.

## Compliance Notes
- **No legal advice**: Immigration resources carry disclaimer.
- **Data export**: User can request full data export (GDPR-style).
- **Account deletion**: Soft delete + anonymization; hard delete on request after retention period.
- **Minors**: Not targeted; age gate not implemented (future).

## Security Checklist for Releases
- [ ] All endpoints have `@AuthenticatedUser` or explicit permit
- [ ] No `findById` without ownership check in services
- [ ] No `console.log` of tokens/PII
- [ ] New tables have RLS + policies
- [ ] Money fields use `String` in DTOs, `BigDecimal` in entities
- [ ] File upload validation present
- [ ] Rate limit config reviewed
- [ ] Secrets not in code/config