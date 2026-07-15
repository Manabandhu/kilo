# Architecture

## High-Level Overview
```
┌─────────────────────────────────────────────────────────────────────┐
│                        Frontend (Expo + RN + Web)                   │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐   │
│  │   iOS App   │ │ Android App │ │   Web App   │ │  Watch OS   │   │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘   │
└────────────────────────────┬────────────────────────────────────────┘
                             │ HTTPS (REST + WebSocket)
                             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    Spring Boot Modular Monolith                     │
│  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐  │
│  │ Users  │ │ Rooms  │ │ Rides  │ │Community│ │ Chat   │ │ Jobs   │  │
│  │Profiles│ │        │ │        │ │        │ │        │ │        │  │
│  └────────┘ └────────┘ └────────┘ └────────┘ └────────┘ └────────┘  │
│  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐  │
│  │ Immig. │ │Expenses│ │ Events │ │ Notif. │ │ Moder. │ │ Admin  │  │
│  └────────┘ └────────┘ └────────┘ └────────┘ └────────┘ └────────┘  │
│  ┌────────────────────────────────────────────────────────────────┐  │
│  │ Common (API responses, errors, money, JPA base, config)        │  │
│  │ Security (JWT validation, CurrentUser, Authorization)          │  │
│  └────────────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────────┘
                             │ JDBC / Supabase JS
                             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        Supabase Platform                            │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐   │
│  │  Postgres   │ │    Auth     │ │  Storage    │ │  Realtime   │   │
│  │  (RLS, FTS) │ │  (JWT HS256)│ │  (Signed)   │ │  (Chat/Notif)│   │
│  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

## Frontend Architecture (Expo + React Native + TypeScript)
- **Router**: Expo Router (file-based, nested layouts, deep links)
- **State**: TanStack Query (server), Zustand (client auth/ui/onboarding)
- **Forms**: React Hook Form + Zod (schema from `@manabandhu/contracts`)
- **Styling**: NativeWind (Tailwind CSS for RN) + `@manabandhu/design-tokens`
- **Animations**: Reanimated 3 + Gesture Handler (bottom sheets, modals)
- **Auth**: Supabase JS client (anon key) for auth only; business ops via Spring Boot
- **Real-time**: Supabase Realtime for chat/notifications (supplementary)
- **Code Sharing**: 100% shared between iOS/Android/Web

### Key Frontend Packages
```
apps/client/
├── app/                    # Expo Router routes
│   ├── (auth)/            # Login, register, forgot-password
│   ├── (onboarding)/      # Multi-step onboarding
│   ├── (app)/             # Authenticated shell
│   │   ├── _layout.tsx    # Responsive shell (tabs/sidebar)
│   │   ├── home.tsx       # Dashboard
│   │   ├── explore.tsx    # Module hub
│   │   ├── chat.tsx
│   │   ├── community.tsx
│   │   ├── profile.tsx
│   │   └── (explore)/     # Module list/detail
│   │       ├── rooms.tsx, rooms/[id].tsx
│   │       ├── rides.tsx, jobs.tsx, immigration.tsx, expenses.tsx, events.tsx
├── src/
│   ├── components/ui/     # Design system primitives (Button, Input, Card, etc.)
│   ├── hooks/             # useAuth, useApiQuery, useApiMutation, useResponsive, useDebounce, usePushNotifications
│   ├── lib/               # apiClient, queryClient, cn, env, format, storage, validations
│   ├── providers/         # AppProviders (QueryClient, Theme, Session), ErrorBoundary
│   ├── services/          # supabase (auth only), auth.service
│   ├── store/             # authStore, uiStore, onboardingStore (Zustand + SecureStore persist)
│   ├── theme/             # ThemeProvider, tokens, useColorScheme
│   ├── types/             # Re-exports from @manabandhu/contracts
│   └── utils/
```

## Backend Architecture (Spring Boot 3.3, Java 21)
Modular monolith organized by **business domain**, not technical layer.

```
backend/
├── src/main/java/com/manabandhu/
│   ├── backend/BackendApplication.java
│   ├── common/
│   │   ├── api/           # ApiError, PagedResult
│   │   ├── config/        # JpaAuditingConfig
│   │   ├── domain/        # BaseEntity
│   │   ├── exception/     # BusinessException, ErrorCode
│   │   ├── money/         # Money (BigDecimal helpers)
│   │   └── web/           # GlobalExceptionHandler, CorrelationIdFilter
│   ├── security/
│   │   ├── CurrentUser, AuthenticatedUser, JwtService, AuthFilter,
│   │   │   Authorization, SecurityConfig, CurrentUserArgumentResolver
│   │   └── ConsolePushProvider, PushProvider
│   ├── users/             # (Supabase handles auth; this is placeholder for admin)
│   ├── profiles/          # Profile CRUD, completion score, block/save/export/delete
│   ├── rooms/             # Listings CRUD, search, status machine, similar
│   ├── rides/             # Offers/requests, seat requests, status machine, ratings
│   ├── community/         # Communities, posts, comments, reactions, reports
│   ├── chat/              # Conversations, messages, read receipts, edit/delete
│   ├── jobs/              # Jobs, referrals, search
│   ├── immigration/       # Categories, resources (disclaimer), Q&A
│   ├── expenses/          # Groups, expenses (split validation), settlements, balances
│   ├── events/            # Events, RSVP (capacity→waitlist)
│   ├── notifications/     # In-app + push (provider abstraction), preferences, tokens
│   ├── moderation/        # Reports queue, actions (warn/suspend/ban), notes, audit
│   └── admin/             # Metrics, user search, role mgmt, audit logs
```

### Domain Package Structure (per domain)
```
<domain>/
├── api/           # *Controller, *Api (DTO records), *Mapper
├── application/   # *Service (use cases), *StatusMachine (where applicable)
├── domain/        # Enums, domain services, business rules, events
└── infrastructure/# *Entity, *Repository, *Mapper
```

### Key Conventions
- **Never trust frontend user IDs** — always compare `CurrentUser.userId()` to resource owner via `Authorization.requireOwner()`.
- **Money**: `String` in API, `BigDecimal` (NUMERIC(12,2)) in DB, `Money` helper for arithmetic.
- **Soft delete**: `status='deleted'` or `deleted_at`; never hard delete user content.
- **Pagination**: `PagedResult<T>` with `page,size,totalItems,totalPages,hasNext`.
- **Errors**: `ApiError` envelope with `code,message,path,fieldErrors,traceId`.
- **Correlation ID**: `X-Correlation-Id` header + MDC logging.
- **Validation**: Bean Validation on DTOs; `BusinessException` for domain rules.
- **OpenAPI**: `springdoc-openapi` at `/swagger-ui.html`, `/v3/api-docs`.

## Supabase Platform
| Component | Purpose |
|-----------|---------|
| **PostgreSQL** | Primary DB with RLS, FTS, triggers, PostGIS (future) |
| **Auth** | Email/password, email verification, JWT (HS256) |
| **Storage** | Media (images, receipts) with signed URLs |
| **Realtime** | Chat messages, notifications (supplementary) |
| **RLS** | All tables enabled; policies enforce ownership/membership |

## Data Flow Examples

### Create Room Listing
1. Frontend: `POST /api/v1/rooms` with JWT → `apiClient.post()`
2. Backend: `RoomController.create()` → `RoomService.create()`
3. Service: validates, sets `posterId=currentUser`, saves `RoomListingEntity`
4. Backend: returns `RoomApi.RoomResponse` (exact address hidden if not shared)
5. Frontend: updates cache, navigates to detail

### Request Ride Seat
1. Frontend: `POST /api/v1/rides/{id}/requests`
2. Backend: `RideService.requestSeat()` → validates active ride, seats available
3. Creates `RideRequestEntity` (pending), returns `SeatRequestResponse`
3. Driver approves → `RideService.decideSeat()` decrements seats, sets full if 0

### Send Message
1. Frontend: `POST /api/v1/conversations/{id}/messages`
2. Backend: `ChatService.send()` → membership check → saves `MessageEntity`
3. Publishes `NewNotificationEvent` (Spring `ApplicationEvent`)
4. `NotificationService` listener persists notification + triggers push (if prefs allow)

## Cross-Cutting Concerns
- **Observability**: Structured JSON logs, `X-Correlation-Id`, Actuator `/actuator/health|readiness|liveness`, Micrometer metrics.
- **Security**: JWT validation (issuer, audience, expiry, signature), CORS allowlist, rate-limit (config), file type/size validation, signed storage URLs.
- **Rate Limiting**: Per-identity (JWT sub) via `app.rate-limit.per-minute` (default 120).
- **Observability Hooks**: Error reporting DSN, OTel endpoint configurable.
- **Deployment**: EAS for iOS/Android; containerized Spring Boot (Dockerfile); static web via Expo export; Supabase hosted.

## Future Extraction Points
Each domain is isolated:
- `rooms`, `rides`, `community`, `chat`, `jobs`, `immigration`, `expenses`, `events`, `notifications`, `moderation`, `admin`
Can be extracted to microservices with minimal rewiring (shared contracts, no cross-domain entity dependencies).