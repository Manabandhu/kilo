# Watch Readiness (Apple Watch / Wear OS)

## API Contract for Watch Clients
Future watch apps will consume a subset of existing REST endpoints with lightweight payloads. No new endpoints required — reuse existing with `Accept: application/vnd.manabandhu.watch.v1+json` (future) or query param `?watch=true`.

## Supported Watch Flows (MVP)

### Rides
| Action | Endpoint | Watch Payload |
|--------|----------|---------------|
| Upcoming ride summary | `GET /rides/upcoming` | `{id, origin, destination, date, time, status, driverName, seatsLeft}` |
| Pickup reminder | Push notification (FCM/APNs) | `{title: "Ride in 15 min", body: "Driver arriving at {origin}", deepLink: "/rides/{id}"}` |
| Ride status | `GET /rides/{id}` | `{status, driverLocation?, etaMinutes}` |
| Accept/Decline request | `PATCH /rides/{id}/requests/{reqId}` | `{decision: "approved"\|"rejected"}` |
| Emergency SOS | `POST /rides/{id}/emergency` | `{lat, lng, message}` → alerts driver + moderation |

### Events
| Action | Endpoint | Watch Payload |
|--------|----------|---------------|
| Event reminder | Push (1hr/15min before) | `{title: "Event starting soon", body: "{eventTitle} at {venue}", deepLink: "/events/{id}"}` |
| RSVP status | `GET /events/{id}/my-rsvp` | `{status: "going"\|"waitlist"}` |
| Check-in | `POST /events/{id}/checkin` | `{checkedIn: true}` (future: QR/NFC) |

### Expenses
| Action | Endpoint | Watch Payload |
|--------|----------|---------------|
| Pending settlement | `GET /expense-groups/{id}/my-balance` | `{net: "12.50", currency: "USD"}` |
| Settle | `POST /expense-groups/{id}/settlements` | `{fromUserId, toUserId, amount}` |

### Notifications
- All push notifications already support `deepLink` for watch tap-through.
- Watch app registers its own push token via `POST /notifications/tokens` (platform: `watchos` / `wearos`).

## Push Provider Abstraction
Backend uses `PushProvider` interface:
```java
interface PushProvider {
  void send(String token, String platform, String title, String body, String deepLink);
}
```
- **Dev**: `ConsolePushProvider` (logs)
- **Prod**: Swap to `FirebasePushProvider` (FCM) + `ApnsPushProvider` (APNs) via `@Qualifier` / config.
- Watch tokens stored in `push_tokens` with `platform='watchos'` or `'wearos'`.

## Deep Link Handling
All watch actions use existing deep links:
- `manabandhu://rides/{id}` → ride detail
- `manabandhu://events/{id}` → event detail
- `manabandhu://expense-groups/{id}/balances` → balances
- `manabandhu://chat/{conversationId}` → conversation

## Data Sync Strategy
- **Polling**: Watch app polls `/rides/upcoming` and `/events/upcoming` every 5 min (configurable).
- **Realtime**: Supabase Realtime subscription for `rides.status`, `event_rsvps`, `notifications` (if watch supports WebSocket).
- **Background**: iOS `WKApplicationRefreshBackgroundTask` / Wear OS `WorkManager` for periodic sync.

## UI/UX Guidelines for Watch
- **Complications**: Next ride time, next event, unread messages count.
- **Glances**: Upcoming ride card (origin→dest, time, status), event card (title, time, RSVP).
- **Actions**: One-tap Accept/Decline ride request, RSVP Going/Decline, Settle expense.
- **Emergency**: Long-press complication → SOS.
- **Accessibility**: VoiceOver/TalkBack labels, dynamic type, high contrast.

## API Additions for Watch (Future)
| Endpoint | Purpose |
|----------|---------|
| `GET /rides/upcoming` | Lightweight list for complication |
| `GET /events/upcoming` | Lightweight list |
| `GET /expense-groups/{id}/my-balance` | Single balance value |
| `POST /rides/{id}/emergency` | SOS alert |
| `POST /events/{id}/checkin` | QR/NFC check-in |

## Testing Watch Flows
- Mock `PushProvider` in tests.
- Unit test watch payload serializers.
- E2E: Simulate push → tap deepLink → app navigates correctly.

## Not Yet Implemented
- Watch-specific endpoints (listed above)
- Complication timeline provider
- Background task scheduling
- Realtime subscriptions on watch
- Voice commands (Siri/Google Assistant intents)

**Status**: Backend API ready; watch clients can be built against existing endpoints + push. Watch-specific endpoints and native apps are post-MVP.