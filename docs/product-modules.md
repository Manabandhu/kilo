# Product Modules

ManaBandhu consists of the following functional modules, each implemented as a vertical slice (database → backend domain → frontend screens).

## 1. Authentication & Onboarding
**Backend**: `profiles` domain (profile completion, block/save/export/delete)
**Frontend**: `(auth)` routes (login, register, forgot-password), `(onboarding)` 9-step flow
- Email/password registration + verification
- Secure session restore (SecureStore)
- 9-step onboarding: profile, location, languages, occupation, status, interests, modules, notifications, privacy
- Resumable progress persisted locally

## 2. Home Dashboard
**Frontend**: `(app)/(tabs)/home.tsx`
- Personalized greeting + city
- Quick actions grid (Rooms, Rides, Jobs, Community, Events, Expenses)
- Sections: Recommended rooms, Upcoming rides, Recent messages, Popular posts, Saved items, Pending requests, Notifications preview, Safety tips
- Pull-to-refresh, skeleton loading, empty/error states

## 3. Rooms & Housing
**Backend**: `rooms` domain
**Frontend**: `(explore)/rooms.tsx`, `rooms/[id].tsx`
- Browse with filters (city, rent range, room type, property type, furnished, utilities, parking, pets, smoking, gender, availability)
- Map results (abstraction for provider)
- Create/edit/pause/delete own listings
- Image upload (Supabase Storage)
- Save/unsave, report, similar listings
- Exact address hidden unless `exact_address_shared=true`

## 4. Rides & Travel
**Backend**: `rides` domain (explicit status machine)
**Frontend**: `(explore)/rides.tsx`
- Ride offers & requests (one-time, daily, weekly)
- Origin/destination + stops + time flexibility
- Seat requests (approve/reject → decrements seats, auto-full)
- Status transitions: draft → published → full/in_progress → completed / cancelled
- Driver/rider ratings (1-5) after completion
- Ride chat (Supabase Realtime)
- Safety reporting

## 5. Community
**Backend**: `community` domain
**Frontend**: `(app)/community.tsx`, `(app)/chat.tsx`
- City & topic communities (housing, jobs, immigration, students, families, local, transport, marketplace, general)
- Join/leave, member count
- Feed: posts (title, body, tags, pin, reactions: like/helpful/celebrate/support)
- Nested comments (depth ≤4) with helpful votes
- Create/edit/delete own posts/comments
- Report content
- Real-time chat: 1:1 + groups, read receipts, typing indicators, edit (10-min), delete, mute, block, report

## 6. Jobs & Referrals
**Backend**: `jobs` domain
**Frontend**: `(explore)/jobs.tsx`
- Search: query, work mode (remote/hybrid/onsite), employment type, experience, state, sponsorship, min salary
- Job detail: title, company, location, type, experience, salary range, skills, sponsorship, source, apply link
- Save/unsave, report
- Community referrals (request/offer) with status tracking

## 7. Immigration Resources
**Backend**: `immigration` domain
**Frontend**: `(explore)/immigration.tsx`
- Categories (Getting Started, Visas, SSN/ITIN, Banking, Driver's License)
- Resources: guides, checklists, FAQs (version date, last reviewed, source refs, trusted contributor badge)
- Community Q&A (ask, answer, flag)
- **Legal disclaimer** on every resource: "General educational information, not legal advice"
- Trusted contributor label, helpful votes

## 8. Expense Sharing
**Backend**: `expenses` domain (deterministic decimal arithmetic)
**Frontend**: `(explore)/expenses.tsx`
- Groups with members, currency
- Expenses: equal / exact / percentage / shares splits
- **Validation**: splits must equal total (Money.remainder = 0)
- Multi-payer support
- Settlements (record, pending/completed)
- Balances (net per member), export CSV/JSON

## 9. Events
**Backend**: `events` domain
**Frontend**: `(explore)/events.tsx`
- Browse: city, state, venue type (online/in-person), upcoming
- Create/edit/cancel (organizer/staff)
- RSVP: going / interested / waitlist / declined
- Capacity enforcement → auto-waitlist
- Reminders (push + in-app)
- Organizer attendee list
- Watch-ready: reminder, RSVP status, start time, venue summary, check-in

## 10. Notifications
**Backend**: `notifications` domain (provider abstraction)
**Frontend**: In-app center + push (Expo Notifications)
- Types: message, ride_request, ride_decision, ride_reminder, listing_inquiry, post_reply, comment_reply, event_reminder, expense_added, settlement_recorded, moderation_action, security_alert
- In-app center: list, unread count, mark read/read-all
- Preferences per category (push/email)
- Push token registration (iOS/Android/Web)
- Deep links per type

## 11. User Profiles, Trust & Safety
**Backend**: `profiles` domain
**Frontend**: `profile.tsx`, settings
- Public profile: avatar, bio, city, languages, occupation, university, employer, interests, joined date, rating, verification badges
- Private settings: privacy toggles, notification prefs, blocked/muted users, saved items
- Trust indicators: email/phone verified, profile complete, positive ride ratings, contribution level, moderator/trusted contributor
- **Not guarantees** of safety
- Block/mute, report user
- Data export request, account deletion request

## 12. Admin & Moderation
**Backend**: `moderation` + `admin` domains
**Frontend**: Admin area (web-first, role-guarded)
- Dashboard metrics (users, active, reports, listings, rides, posts, messages, flagged)
- User search, review, warn/suspend/ban/reinstate, role change
- Report queue (filter by status) → assign/resolve/dismiss
- Content moderation (listings, rides, posts, comments, messages, jobs, events, resources)
- Moderator notes, audit logs
- All actions recorded in `moderation_actions` + `audit_logs`
- Private messages only viewable if reported + policy permits

## 12. Watch Readiness (Future)
**Doc**: `docs/watch-readiness.md`
- Ride: upcoming summary, pickup reminder, status, accept/decline, SOS
- Events: reminder, RSVP status, check-in
- Expenses: pending balance, settle
- Push provider abstraction (`PushProvider` interface)
- Deep links for all actions