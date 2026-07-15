# ManaBandhu

**ManaBandhu** ("friend/companion for the mind/heart") is a cross-platform
community super app for South Asian immigrants, international students,
professionals, and families living in the United States.

It combines housing & roommate discovery, ridesharing, jobs & referrals,
immigration resources, local communities, messaging, shared expenses, events,
safety/reporting, notifications, verification, and moderation into one
trustworthy, accessible, and modern experience.

- **Web / iOS / Android** from a single Expo + React Native + TypeScript app
- **Backend**: Java 21 + Spring Boot (modular monolith by business domain)
- **Platform**: Supabase (PostgreSQL, Auth, Storage, Realtime) + Flyway for
  backend-managed schema

---

## Monorepo layout

```
manabandhu/
├── apps/client/        # Expo + RN + TS app (web, iOS, Android)
├── backend/            # Spring Boot modular monolith (Gradle)
├── supabase/           # migrations, seed, functions, config
├── packages/           # contracts, design-tokens, shared-config
├── docs/               # architecture, api, database, security, ...
├── .github/workflows/  # CI
├── docker-compose.yml
└── .env.example
```

---

## Tech stack

| Layer      | Choice |
|------------|--------|
| Frontend   | React Native, Expo, Expo Router, TypeScript, NativeWind, TanStack Query, React Hook Form, Zod, Zustand, Reanimated, Gesture Handler, Secure Store, Notifications |
| Backend    | Java 21, Spring Boot, Spring Security, Spring Data JPA, Bean Validation, Actuator, OpenAPI/Swagger, Flyway, Testcontainers, JUnit 5, Mockito |
| Data/Platform | Supabase, PostgreSQL, Supabase Auth/Storage/Realtime, Postgres FTS, PostGIS, RLS |

---

## Prerequisites

- Node.js 20+ and npm 10+
- Java 21 + Gradle (or use the provided wrapper)
- Supabase CLI (`npm i -g supabase`)
- Docker (for backend Postgres / integration tests / container hosting)

---

## Quick start

### 1. Install dependencies

```bash
# Frontend
cd apps/client && npm install

# Backend (uses Gradle wrapper)
cd backend && ./gradlew build --offline || ./gradlew build
```

### 2. Configure environment

```bash
cp .env.example .env
# Edit .env with real Supabase credentials (do not commit it)
```

### 3. Start Supabase locally

```bash
supabase start
# Apply migrations + seed (handled automatically by `supabase start`,
# or run explicitly:)
supabase db reset
```

### 4. Run migrations (Flyway, backend-managed schema)

The Spring Boot backend applies its Flyway migrations on startup.

```bash
cd backend && ./gradlew bootRun
```

### 5. Seed data

```bash
supabase db reset   # applies supabase/migrations + supabase/seed
```

### 6. Start the backend

```bash
cd backend && ./gradlew bootRun
# Health: http://localhost:8080/actuator/health
# API:     http://localhost:8080/api/v1/...
# Swagger: http://localhost:8080/swagger-ui.html
```

### 7. Start Expo

```bash
cd apps/client
npm run start        # Expo dev server
npm run web          # Web
npm run ios          # iOS (macOS + Xcode required)
npm run android      # Android (Android Studio / emulator required)
```

---

## Running tests

```bash
# Frontend
cd apps/client && npm run test        # unit / component tests
cd apps/client && npm run typecheck   # TypeScript
cd apps/client && npm run lint        # ESLint

# Backend
cd backend && ./gradlew test          # JUnit 5 + Mockito + Testcontainers
```

---

## Production builds

```bash
# Expo EAS (iOS + Android)
cd apps/client && eas build --platform ios
cd apps/client && eas build --platform android
cd apps/client && eas build --platform all

# Backend container
docker build -t manabandhu-backend ./backend
```

---

## Documentation

- [docs/architecture.md](docs/architecture.md)
- [docs/product-modules.md](docs/product-modules.md)
- [docs/api.md](docs/api.md)
- [docs/database.md](docs/database.md)
- [docs/security.md](docs/security.md)
- [docs/testing.md](docs/testing.md)
- [docs/deployment.md](docs/deployment.md)
- [docs/watch-readiness.md](docs/watch-readiness.md)

---

## License

Proprietary. See internal policy.
