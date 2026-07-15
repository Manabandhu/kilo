# Deployment Guide

## Prerequisites
- **Supabase Project** (hosted or self-managed)
- **Docker** for backend container
- **Expo EAS** account for mobile builds
- **GitHub** repo with Actions enabled
- **Domain** for web (Vercel/Netlify/Cloudflare Pages)

## Environment Variables
All secrets via `.env` (never committed). Example in `.env.example`.

| Variable | Scope | Description |
|----------|-------|-------------|
| `EXPO_PUBLIC_SUPABASE_URL` | Frontend | Supabase project URL (anon) |
| `EXPO_PUBLIC_SUPABASE_ANON_KEY` | Frontend | Supabase anon key |
| `EXPO_PUBLIC_API_URL` | Frontend | Backend REST base (e.g., `https://api.manabandhu.app/api/v1`) |
| `EXPO_PUBLIC_MAPBOX_TOKEN` | Frontend | Mapbox public token |
| `SUPABASE_JWT_SECRET` | Backend | **Secret** — HS256 key for JWT validation |
| `SUPABASE_SERVICE_ROLE_KEY` | Backend | **Secret** — privileged admin ops only |
| `SPRING_DATASOURCE_URL` | Backend | JDBC URL (Supabase pooler or direct) |
| `SPRING_DATASOURCE_USERNAME` | Backend | DB user |
| `SPRING_DATASOURCE_PASSWORD` | Backend | DB password |
| `CORS_ALLOWED_ORIGINS` | Backend | Comma-separated origins (frontend, Expo dev) |
| `PUSH_PROVIDER` | Backend | `console` (dev) / `firebase` / `apns` |
| `APP_WEB_URL` | Backend | Frontend web URL for deep links |

## Local Development
```bash
# 1. Start Supabase
supabase start
supabase db reset          # applies migrations + seed

# 2. Backend
cd backend
./gradlew bootRun          # runs on :8080, Flyway validates schema

# 3. Frontend
cd apps/client
npm install
npm run start              # Expo dev server (web: :8081, iOS/Android via app)
```

## Database Migrations
**Production**: Supabase CLI
```bash
supabase db push           # applies local migrations to linked project
supabase db pull           # pull remote changes
```
**Backend Integration Tests**: Flyway on Testcontainers (separate schema).

## Backend Deployment (Container)
```dockerfile
# Multi-stage build (see backend/Dockerfile)
# Build: gradle build -x test
# Runtime: eclipse-temurin:21-jre
```
Deploy to Cloud Run / Fly.io / Kubernetes:
```bash
docker build -t manabandhu-backend ./backend
docker run -p 8080:8080 --env-file .env manabandhu-backend
```
Health: `GET /actuator/health` (liveness/readiness probes).

## Frontend Deployment

### Web (Expo Static Export)
```bash
cd apps/client
npm run build:web          # npx expo export --platform web
# Output in dist/ — deploy to Vercel/Netlify/Cloudflare Pages
```
Configure SPA fallback (`/*` → `/index.html`).

### iOS (EAS Build)
```bash
eas build --platform ios --profile production
# Submit via EAS Submit or App Store Connect
```
Requirements: Apple Developer account, provisioning profiles, `app.config.ts` with `ios.bundleIdentifier`.

### Android (EAS Build)
```bash
eas build --platform android --profile production
# Generates .aab — upload to Play Console
```
Requirements: Google Play Developer account, keystore (EAS manages or bring your own).

### EAS Configuration (`eas.json`)
```json
{
  "build": {
    "development": { "developmentClient": true, "distribution": "internal" },
    "preview": { "distribution": "internal" },
    "production": { "autoIncrement": true }
  },
  "submit": { "production": {} }
}
```

## CI/CD Pipeline (`.github/workflows/ci.yml`)
1. **Frontend Job**: `npm ci` → `lint` → `typecheck` → `test:ci` → `build:web` (artifact)
2. **Backend Job**: `./gradlew check` (compile, checkstyle, spotbugs, test) → Docker build → push to registry
3. **Deploy Job** (on `main` tag):
   - Web: deploy `dist/` to hosting
   - Backend: rollout new container image
   - Mobile: `eas build --platform all` + `eas submit`

## Rollback Strategy
- **Backend**: Keep previous container image tagged; `kubectl rollout undo` / Cloud Run revision rollback.
- **Database**: Supabase point-in-time recovery (PITR) for PITR; Flyway `undo` not used — prefer forward migrations.
- **Frontend Web**: Hosting provider instant rollback (Vercel/Netlify).
- **Mobile**: App Store/Play Console previous version rollback (if critical).

## Monitoring & Observability
- **Health**: `/actuator/health` (Spring Boot Actuator)
- **Logs**: Structured JSON (Logback) → Loki / Datadog / CloudWatch
- **Metrics**: Micrometer → Prometheus → Grafana
- **Errors**: Sentry (frontend + backend) — DSN via env
- **Uptime**: Pingdom / UptimeRobot on `/actuator/health`

## Backup & Disaster Recovery
- **Supabase**: Automated daily backups + PITR (7 days default, configurable).
- **Backend Config**: GitOps (env in secret manager, not repo).
- **RTO/RPO**: RPO < 1hr (PITR), RTO < 30min (container redeploy).

## Security Checklist (Pre-Deploy)
- [ ] No `.env` in repo
- [ ] `SUPABASE_JWT_SECRET` rotated, not in frontend
- [ ] `SUPABASE_SERVICE_ROLE_KEY` backend-only
- [ ] CORS allowlist exact origins
- [ ] RLS enabled on all tables
- [ ] Rate limiting configured
- [ ] Security headers (Helmet equivalent in Spring)
- [ ] Dependency scan (`npm audit`, `./gradlew dependencyCheckAnalyze`)
- [ ] Secrets in secret manager (not CI vars for prod)

## Scaling Considerations
- **Backend**: Stateless → horizontal scale (K8s HPA / Cloud Run concurrency).
- **Database**: Supabase read replicas for heavy reads; connection pooling (PgBouncer).
- **Realtime**: Supabase handles WebSocket scaling; shard by tenant if needed.
- **Storage**: Supabase Storage (S3-compatible) + CDN.
- **Push**: FCM/APNs batching; `PushProvider` abstraction for swap.

## DNS & SSL
- **API**: `api.manabandhu.app` → Cloud Run / ALB → backend
- **Web**: `app.manabandhu.app` → Vercel/Netlify → static export
- **Supabase**: `db.manabandhu.app` (custom domain) or use Supabase domain
- **SSL**: Managed by hosting provider (Let's Encrypt / managed certs)

## Post-Deploy Verification
1. `GET https://api.manabandhu.app/actuator/health` → `{"status":"UP"}`
2. `GET https://app.manabandhu.app` → loads without console errors
3. Sign up → email verify → login → onboarding → home
4. Create room → appears in search
5. Create ride → request seat → approve
6. Admin login → metrics visible
7. Push notification test (console provider logs)