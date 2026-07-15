# Testing Strategy

## Test Pyramid
```
        E2E (Critical Flows)
      /                         \
   Integration (API + DB)       \
  /                                 \
Unit (Domain, Services, Utils, Hooks)
```

## Frontend Testing

### Stack
- **Jest** + **React Native Testing Library** (unit/component)
- **Detox** (E2E on simulators/emulators)
- **MSW** (Mock Service Worker) for API mocking in unit tests

### What to Test
| Layer | Targets |
|-------|---------|
| **Validation** | All Zod schemas (`src/lib/validations.ts`) |
| **Auth State** | `useAuthStore` persist/rehydrate, signIn/signUp/signOut |
| **Hooks** | `useApiQuery`, `useApiMutation`, `useDebounce`, `useResponsive`, `usePushNotifications` |
| **Components** | Design system primitives (Button, Input, Card, Avatar, Badge, Skeleton, EmptyState, ErrorState, SearchBar, Screen) — rendering, accessibility props, loading/error/disabled states |
| **Screens** | Home (data fetch + empty/error), Rooms list/detail, Profile, Onboarding steps |
| **Navigation** | Redirect logic (index → auth/app), protected routes, deep links |

### Commands
```bash
cd apps/client
npm run test          # Jest unit/component
npm run test:watch    # Watch mode
npm run test:coverage # Coverage report
npm run e2e           # Detox (requires simulator/emulator)
```

### Test File Convention
- `*.test.tsx` alongside component or in `__tests__/`
- Mock `@manabandhu/contracts` types, `apiClient`, `supabase`

### Example: Validation Schema Test
```ts
import { registerSchema } from '@/lib/validations';

test('registerSchema rejects short password', () => {
  const result = registerSchema.safeParse({ email: 'a@b.com', password: '123', displayName: 'A' });
  expect(result.success).toBe(false);
  expect(result.error.flatten().fieldErrors.password).toContain('At least 8 characters');
});
```

### Example: Hook Test
```ts
import { renderHook, act } from '@testing-library/react-native';
import { useDebounce } from '@/hooks/useDebounce';

test('debounces value after delay', () => {
  const { result } = renderHook(() => useDebounce('initial', 100));
  act(() => { result.current[1]('updated'); }); // setter
  expect(result.current[0]).toBe('initial');
  await waitFor(() => expect(result.current[0]).toBe('updated'));
});
```

## Backend Testing

### Stack
- **JUnit 5** + **Mockito** (unit)
- **Testcontainers** (PostgreSQL for integration)
- **Spring Boot Test** (`@SpringBootTest`, `@AutoConfigureMockMvc`)

### What to Test
| Layer | Targets |
|-------|---------|
| **Domain** | `Money` arithmetic, `RideStatusMachine` transitions, `ProfileCompletionCalculator`, split validation (`ExpenseService.validateSplits`) |
| **Service** | All `*Service` classes — use cases, authorization checks, business rules, exception mapping |
| **Controller** | `@WebMvcTest` — request mapping, validation, auth injection, response shape (`PagedResult`, `ApiError`) |
| **Repository** | `@DataJpaTest` + Testcontainers — custom queries, pagination |
| **Security** | JWT parsing, role extraction, `CurrentUserArgumentResolver` |
| **Migrations** | Flyway `validate` on test schema |

### Commands
```bash
cd backend
./gradlew test                    # All tests (unit + integration)
./gradlew test --tests "*Service*"   # Service tests only
./gradlew test --tests "*Controller*" # Controller tests only
./gradlew flywayValidate           # Migration validation
```

### Testcontainers Config
```java
@Container
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
    .withDatabaseName("manabandhu_test")
    .withUsername("postgres")
    .withPassword("postgres");

@DynamicPropertySource
static void props(DynamicPropertyRegistry r) {
    r.add("spring.datasource.url", postgres::getJdbcUrl);
    r.add("spring.datasource.username", postgres::getUsername);
    r.add("spring.datasource.password", postgres::getPassword);
}
```

### Example: Service Test
```java
@ExtendWith(MockitoExtension.class)
class RoomServiceTest {
    @Mock RoomRepository repo; @InjectMocks RoomService svc;

    @Test void create_setsPosterAndStatus() {
        CurrentUser user = new CurrentUser(UUID.randomUUID(), "a@b.com", "user", List.of());
        RoomApi.CreateRoomRequest req = new RoomApi.CreateRoomRequest(/* valid */);
        RoomApi.RoomResponse resp = svc.create(user, req);
        assertEquals(user.userId(), resp.posterId());
        assertEquals("active", resp.status());
    }
}
```

### Example: Controller Test
```java
@WebMvcTest(RoomController.class)
class RoomControllerTest {
    @Autowired MockMvc mvc;
    @MockBean RoomService svc;

    @Test void search_returnsPagedResult() throws Exception {
        when(svc.search(any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
            .thenReturn(PagedResult.of(List.of(), 0, 20, 0));
        mvc.perform(get("/api/v1/rooms").with(jwt().subject("uid")))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.items").isArray());
    }
}
```

## E2E Test Scenarios (Detox)
| Scenario | Steps |
|--------|-------|
| Sign up & onboarding | Register → email verify → complete 9 steps → lands on home |
| Browse & save room | Home → Explore → Rooms → search → tap listing → Save → Profile → Saved |
| Create room listing | Explore → Rooms → Create → fill form → submit → appears in list |
| Create ride | Explore → Rides → Create offer → fill → publish → appears in list |
| Request ride seat | Explore → Rides → tap ride → Request seat → driver approves |
| Community post | Community → join → create post → comment → react |
| Send message | Chat → new conversation → send message → appears in list |
| Save job | Explore → Jobs → tap → Save → Profile → Saved |
| Split expense | Explore → Expenses → Create group → Add expense (equal split) → Settlement |
| RSVP event | Explore → Events → tap → RSVP Going → appears in My RSVPs |
| Submit report | Any listing → Report → select reason → submit → appears in moderation queue (admin) |
| Admin reviews report | Admin login → Moderation queue → Resolve → action logged |

## CI Integration
- **GitHub Actions**: `npm run test`, `npm run typecheck`, `npm run lint` (frontend); `./gradlew test` (backend)
- **Coverage**: Upload to Codecov (optional)
- **Detox**: Run on CI macOS runner for iOS, Ubuntu for Android (optional, slow)

## Test Data Management
- **Frontend**: MSW handlers in `src/tests/msw/handlers.ts` — mirrors backend API contracts.
- **Backend**: Testcontainers spins fresh Postgres per test class; `@Sql` for seed if needed.

## Coverage Goals
| Area | Target |
|------|--------|
| Domain logic (Money, StatusMachines, Validators) | 90%+ |
| Services | 80%+ |
| Controllers | 70%+ |
| Frontend hooks/components | 70%+ |
| Overall | 75%+ |

## Accessibility Testing
- **axe-core** / `react-native-accessibility` in component tests.
- Manual: VoiceOver (iOS), TalkBack (Android), keyboard (web) on every screen.
- Automated: `jest-axe` on rendered components.

## Performance Testing
- **k6** script for load testing `/api/v1/rooms` search, `/api/v1/rides` search.
- Thresholds: p95 < 200ms (simple), < 500ms (complex search).
- Run nightly or on release candidate.