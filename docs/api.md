# API Reference

**Base URL**: `https://api.manabandhu.app/api/v1` (prod) / `http://localhost:8080/api/v1` (dev)

## Authentication
All endpoints except `/actuator/health` require `Authorization: Bearer <Supabase JWT>`.
JWT validated: signature (HS256), issuer, audience (`authenticated`), expiry.

## Common Patterns

### Pagination
Query params: `page` (default 0), `size` (default 20), `sort` (e.g., `createdAt,desc`).
Response: `PagedResult<T>`:
```json
{
  "items": [],
  "page": 0,
  "size": 20,
  "totalItems": 0,
  "totalPages": 0,
  "hasNext": false
}
```

### Error Response
```json
{
  "timestamp": "2026-01-15T12:00:00Z",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "path": "/api/v1/rooms",
  "fieldErrors": { "monthlyRent": "must be positive" },
  "traceId": "abc-123"
}
```
Codes: `VALIDATION_ERROR`, `UNAUTHORIZED`, `FORBIDDEN`, `NOT_FOUND`, `CONFLICT`, `RATE_LIMITED`, `IDEMPOTENT_REPLAY`, `BUSINESS_RULE_VIOLATION`, `INTERNAL_ERROR`.

### Money
Always **string** (e.g., `"1450.00"`). Never float.

## Endpoints

### Profiles
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/profiles/me` | ✅ | My full profile |
| GET | `/profiles/{id}` | ✅ | Public summary |
| PATCH | `/profiles/me` | ✅ | Update (recomputes completion) |
| GET | `/profiles/me/blocked` | ✅ | My blocked users |
| POST | `/profiles/{id}/block` | ✅ | Block user |
| DELETE | `/profiles/{id}/block` | ✅ | Unblock |
| GET | `/profiles/me/saved` | ✅ | My saved items |
| POST | `/profiles/me/saved` | ✅ | Save item |
| DELETE | `/profiles/me/saved` | ✅ | Unsave item |
| POST | `/profiles/me/export` | ✅ | Request data export |
| POST | `/profiles/me/deletion-request` | ✅ | Request deletion |

### Rooms
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/rooms` | ✅ | Search (city, state, minRent, maxRent, roomType, propertyType, furnished, utilities, parking, petPolicy, smoking, gender, query, page, size, sort) |
| GET | `/rooms/{id}` | ✅ | Detail (exact address hidden if not shared) |
| POST | `/rooms` | ✅ | Create (owner=current user) |
| PATCH | `/rooms/{id}` | ✅ (owner) | Update (status: active/paused/closed) |
| DELETE | `/rooms/{id}` | ✅ (owner) | Soft delete |
| POST | `/rooms/{id}/save` | ✅ | Save/unsave |
| POST | `/rooms/{id}/report` | ✅ | Report |
| GET | `/rooms/{id}/similar` | ✅ | Similar listings |

### Rides
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/rides` | ✅ | Search (type, state, query) |
| GET | `/rides/{id}` | ✅ | Detail |
| POST | `/rides` | ✅ | Create offer/request |
| PATCH | `/rides/{id}/status` | ✅ (driver) | Transition (draft→published→full/in_progress→completed, cancel) |
| POST | `/rides/{id}/requests` | ✅ | Request seat |
| PATCH | `/rides/{id}/requests/{reqId}` | ✅ (driver) | Approve/reject |
| POST | `/rides/{id}/complete` | ✅ (driver) | Mark completed |
| POST | `/rides/{id}/ratings` | ✅ | Rate after completion |
| POST | `/rides/{id}/report` | ✅ | Report |

### Community
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/communities` | ✅ | Search (kind, category, city, state, query) |
| GET | `/communities/{id}` | ✅ | Detail |
| POST | `/communities/{id}/join` | ✅ | Join |
| DELETE | `/communities/{id}/leave` | ✅ | Leave |
| GET | `/communities/{id}/feed` | ✅ | Posts feed |
| POST | `/communities/{id}/posts` | ✅ (member if topic) | Create post |
| PATCH | `/posts/{id}` | ✅ (author/staff) | Edit |
| DELETE | `/posts/{id}` | ✅ (author/staff) | Soft delete |
| POST | `/posts/{id}/react` | ✅ | React (like/helpful/celebrate/support) |
| POST | `/posts/{id}/save` | ✅ | Save |
| POST | `/posts/{id}/report` | ✅ | Report |
| POST | `/posts/{id}/comments` | ✅ | Comment (parentId optional) |
| GET | `/posts/{id}/comments` | ✅ | Threaded |
| PATCH/DELETE | `/comments/{id}` | ✅ (author/staff) | Edit/delete |
| POST | `/comments/{id}/helpful` | ✅ | Vote helpful |

### Chat
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/conversations` | ✅ | My conversations (last msg + unread) |
| GET | `/conversations/{id}/messages` | ✅ (member) | Messages |
| POST | `/conversations` | ✅ | Create (direct: 2 members; group: title+members) |
| POST | `/conversations/{id}/messages` | ✅ (member) | Send |
| PATCH | `/messages/{id}` | ✅ (author, ≤10min) | Edit |
| DELETE | `/messages/{id}` | ✅ (author/staff) | Delete |
| POST | `/messages/{id}/read` | ✅ | Mark read |
| POST | `/conversations/{id}/mute` | ✅ | Mute |
| POST | `/conversations/{id}/report` | ✅ | Report |

### Jobs
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/jobs` | ✅ | Search (workMode, employmentType, experienceLevel, state, sponsorship, minSalary, query) |
| GET | `/jobs/{id}` | ✅ | Detail |
| POST | `/jobs` | ✅ (owner/staff) | Create |
| PATCH | `/jobs/{id}` | ✅ (owner/staff) | Update |
| DELETE | `/jobs/{id}` | ✅ (owner/staff) | Soft delete |
| POST | `/jobs/{id}/save` | ✅ | Save |
| POST | `/jobs/{id}/report` | ✅ | Report |
| POST | `/referrals` | ✅ | Create referral (request/offer) |
| GET | `/referrals` | ✅ | My referrals |

### Immigration
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/immigration/categories` | ✅ | Categories |
| GET | `/immigration/resources` | ✅ | Search (categoryId, type, tag, query) |
| GET | `/immigration/resources/{id}` | ✅ | Detail (includes disclaimer) |
| POST | `/immigration/resources` | ✅ (staff) | Create |
| PATCH/DELETE | `/immigration/resources/{id}` | ✅ (staff) | Update/delete |
| POST | `/immigration/resources/{id}/save` | ✅ | Save |
| POST | `/immigration/resources/{id}/helpful` | ✅ | Vote helpful |
| POST | `/immigration/resources/{id}/report` | ✅ | Report outdated |
| POST | `/immigration/questions` | ✅ | Ask question |
| GET | `/immigration/questions` | ✅ | List (open/answered) |
| POST | `/immigration/questions/{id}/answer` | ✅ (staff) | Answer |

### Expenses
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | `/expense-groups` | ✅ | Create group |
| GET | `/expense-groups` | ✅ | My groups |
| GET | `/expense-groups/{id}` | ✅ (member) | Detail |
| POST | `/expense-groups/{id}/members` | ✅ | Invite |
| DELETE | `/expense-groups/{id}/members/{userId}` | ✅ | Remove |
| POST | `/expense-groups/{id}/expenses` | ✅ (member) | Add expense (validates splits) |
| GET | `/expense-groups/{id}/expenses` | ✅ (member) | List |
| POST | `/expense-groups/{id}/settlements` | ✅ (member) | Record settlement |
| GET | `/expense-groups/{id}/settlements` | ✅ (member) | List |
| GET | `/expense-groups/{id}/balances` | ✅ (member) | Net balances |
| GET | `/expense-groups/{id}/export` | ✅ (member) | Export CSV/JSON |

### Events
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/events` | ✅ | Search (city, state, venueType, upcoming, query) |
| GET | `/events/{id}` | ✅ | Detail |
| POST | `/events` | ✅ | Create |
| PATCH | `/events/{id}/status` | ✅ (organizer/staff) | Update status |
| DELETE | `/events/{id}` | ✅ (organizer/staff) | Soft delete |
| POST | `/events/{id}/rsvp` | ✅ | RSVP (going/interested/waitlist/declined) |
| POST | `/events/{id}/save` | ✅ | Save |
| POST | `/events/{id}/report` | ✅ | Report |
| GET | `/events/{id}/attendees` | ✅ (organizer/staff) | Attendees |

### Notifications
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/notifications` | ✅ | My notifications |
| GET | `/notifications/unread-count` | ✅ | Unread count |
| POST | `/notifications/{id}/read` | ✅ | Mark read |
| POST | `/notifications/read-all` | ✅ | Mark all read |
| GET | `/notifications/preferences` | ✅ | Get prefs |
| PATCH | `/notifications/preferences` | ✅ | Update prefs |
| POST | `/notifications/tokens` | ✅ | Register push token |
| DELETE | `/notifications/tokens` | ✅ | Remove token |

### Moderation (staff)
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/moderation/reports` | ✅ (mod/admin) | Queue (filter status) |
| GET | `/moderation/reports/{id}` | ✅ (mod/admin) | Detail |
| PATCH | `/moderation/reports/{id}` | ✅ (mod/admin) | Resolve/dismiss |
| POST | `/moderation/users/{id}/warning` | ✅ (mod/admin) | Warn |
| POST | `/moderation/users/{id}/suspend` | ✅ (mod/admin) | Suspend |
| POST | `/moderation/users/{id}/ban` | ✅ (mod/admin) | Ban |
| POST | `/moderation/users/{id}/reinstate` | ✅ (mod/admin) | Reinstate |
| POST | `/moderation/notes` | ✅ (mod/admin) | Add note |
| GET | `/moderation/notes/{userId}` | ✅ (mod/admin) | View notes |

### Admin (admin only)
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/admin/metrics` | ✅ (admin) | Dashboard metrics |
| GET | `/admin/users` | ✅ (admin) | Search users |
| GET | `/admin/users/{id}` | ✅ (admin) | User detail |
| POST | `/admin/users/{id}/role` | ✅ (admin) | Set role |
| GET | `/admin/audit-logs` | ✅ (admin) | Audit logs |
| GET | `/admin/reports` | ✅ (admin) | Moderation queue alias |

## Health & Observability
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/actuator/health` | ❌ | Liveness |
| GET | `/actuator/health/readiness` | ❌ | Readiness |
| GET | `/actuator/health/liveness` | ❌ | Liveness |
| GET | `/actuator/info` | ❌ | Build info |
| GET | `/actuator/metrics` | ❌ | Metrics |
| GET | `/swagger-ui.html` | ❌ | Swagger UI |
| GET | `/v3/api-docs` | ❌ | OpenAPI JSON |

## Rate Limiting
Default: 120 req/min per identity (configurable via `app.rate-limit.per-minute`). Stricter on auth endpoints.

## CORS
`CORS_ALLOWED_ORIGINS` (comma-separated). Credentials allowed.