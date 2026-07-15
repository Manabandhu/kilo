# Database Schema & Migrations

## Schema Ownership
**Supabase migrations** (`supabase/migrations/`) are the **single source of truth** for the production schema. The Spring Boot backend uses `ddl-auto: validate` against this schema. Flyway in the backend is used only for integration tests (separate test schema).

Do not run two independent migration systems against the same schema.

## Naming Conventions
- Tables: `snake_case` (e.g., `room_listings`)
- Columns: `snake_case`
- Primary keys: `id uuid primary key default gen_random_uuid()`
- Foreign keys: `*_id uuid references ... on delete cascade|set null`
- Timestamps: `created_at timestamptz not null default now()`, `updated_at timestamptz not null default now()`
- Soft delete: `deleted_at timestamptz` or `status` enum with `deleted`
- Audit: `created_by uuid`, `updated_by uuid` where applicable
- Optimistic locking: `version int` where concurrent updates matter

## Core Tables

### Profiles (1:1 with auth.users)
```sql
profiles (
  id uuid pk references auth.users(id) on delete cascade,
  display_name text not null,
  avatar_url text,
  bio text,
  city text, state text,
  languages text[] default '{}',
  occupation text,
  archetype text check in ('student','professional','family','newcomer'),
  university text, employer text,
  interests text[] default '{}',
  profile_completion_score int default 0 check (0..100),
  rating_average numeric(3,2) default 0,
  rating_count int default 0,
  contribution_level int default 0,
  privacy jsonb default '{"showCity":true,...}',
  onboarding_completed boolean default false,
  role text default 'user' check in ('user','moderator','admin'),
  account_status text default 'active' check in ('active','warned','suspended','banned','deleted'),
  created_at, updated_at timestamptz
)
```

### Room Listings
```sql
room_listings (
  id uuid pk default gen_random_uuid(),
  title text not null,
  description text not null,
  monthly_rent numeric(12,2) not null check (>=0),
  security_deposit numeric(12,2),
  city text not null, state text not null, zip text,
  approximate_location jsonb,
  exact_address_shared boolean default false,
  room_type text, property_type text, furnished text,
  available_date date, lease_duration_months int,
  utilities_included boolean default false,
  amenities text[] default '{}',
  parking boolean default false,
  pet_policy text default 'none',
  smoking_policy text default 'no_smoking',
  occupancy int default 1 check (>0),
  gender_preference text default 'any',
  contact_preference text default 'in_app',
  images text[] default '{}',
  status text default 'active' check in ('active','paused','closed','deleted'),
  poster_id uuid not null references profiles(id) on delete cascade,
  created_at, updated_at, created_by, updated_by, deleted_at
)
```

### Rides
```sql
rides (
  id uuid pk, type text check in ('offer','request'),
  frequency text check in ('one_time','daily','weekly'),
  origin text, origin_location jsonb not null,
  destination text, destination_location jsonb not null,
  stops jsonb default '[]',
  date date not null, time text not null,
  time_flexibility text, available_seats int check (>=0),
  suggested_contribution numeric(12,2),
  luggage_info text, driver_notes text,
  status text check in ('draft','published','full','in_progress','completed','cancelled'),
  driver_id uuid not null references profiles(id) on delete cascade,
  created_at, updated_at, created_by, updated_by, deleted_at
)

ride_requests (
  id uuid pk, ride_id uuid references rides(id) on delete cascade,
  rider_id uuid references profiles(id) on delete cascade,
  seats_requested int check (>0), rider_notes text,
  status text check in ('pending','approved','rejected','cancelled'),
  created_at, updated_at,
  unique (ride_id, rider_id)
)

ride_ratings (id, ride_id, from_user_id, to_user_id, score int 1..5, comment, created_at)
```

### Community
```sql
communities (id, name, slug unique, description, kind check in ('city','topic'),
  category text, city, state, member_count, cover_image_url, pinned_post_ids uuid[],
  created_at)

community_memberships (community_id, user_id, role check in ('member','moderator'), joined_at, pk(community_id,user_id))

posts (id, community_id, author_id, title, body, tags text[], pinned boolean,
  reactions jsonb default '{"like":0,"helpful":0,"celebrate":0,"support":0}',
  status check in ('published','deleted','moderated'), search_vector tsvector,
  created_at, updated_at, created_by, updated_by, deleted_at)

comments (id, post_id, parent_id self-ref, author_id, body, helpful_votes int,
  status check in ('published','deleted','moderated'), created_at, updated_at)
```

### Chat
```sql
conversations (id, kind check in ('direct','group'), title, created_at, updated_at)
conversation_members (conversation_id, user_id, role check in ('member','admin'), muted boolean, joined_at, pk)
messages (id, conversation_id, sender_id, body, attachments jsonb, reply_to_id, reaction text,
  edited boolean, created_at, updated_at)
message_reads (message_id, user_id, read_at, pk)
```

### Jobs
```sql
jobs (id, title, company, location, state, work_mode check in ('remote','hybrid','onsite'),
  employment_type, experience_level, salary_min/max numeric(12,2), currency default 'USD',
  skills text[], sponsorship_offered boolean, source check in ('community','company','feed','admin'),
  application_url, posted_by_id references profiles, expires_at date,
  posted_at, status check in ('active','closed','deleted'), search_vector tsvector,
  created_at, updated_at, deleted_at)

referrals (id, job_id, kind check in ('request','offer'), from_user_id, to_user_id,
  company, note, status check in ('open','connected','closed'), created_at)
```

### Immigration
```sql
immigration_categories (id, slug unique, name, description, order_index)
immigration_resources (id, category_id, type check in ('guide','checklist','faq'),
  title, body, tags text[], version_date date, last_reviewed_at timestamptz,
  source_references text[], trusted_contributor boolean, author_id,
  helpful_count int, status check in ('draft','published','archived'), search_vector tsvector,
  created_at, updated_at)

immigration_questions (id, resource_id, author_id, body, answer, answered_by_id,
  status check in ('open','answered','flagged'), created_at, updated_at)
```

### Expenses
```sql
expense_groups (id, name, currency default 'USD', created_by_id, created_at, updated_at)
expense_group_members (group_id, user_id, joined_at, pk)
expenses (id, group_id, title, description, total_amount numeric(12,2), currency,
  paid_by_ids uuid[], split_strategy check in ('equal','exact','percentage','shares'),
  splits jsonb, date, category, receipt_url, created_by_id, created_at)
settlements (id, group_id, from_user_id, to_user_id, amount numeric(12,2), currency,
  status check in ('pending','completed'), note, recorded_by_id, created_at)
```

### Events
```sql
events (id, title, description, venue_type check in ('online','in_person'),
  location, online_url, city, state, start_time timestamptz, end_time,
  capacity int, organizer_id, cover_image_url, status check in ('scheduled','cancelled','completed'),
  created_at, updated_at, deleted_at)

event_rsvps (event_id, user_id, status check in ('going','interested','waitlist','declined'),
  created_at, pk)
```

### Notifications
```sql
notifications (id, user_id, type text, title, body, deep_link, read boolean, created_at)
notification_preferences (user_id pk, push_enabled, email_enabled, messages, rides, listings,
  posts, events, expenses, moderation, marketing, all boolean defaults)
push_tokens (id, user_id, token, platform, device_id, created_at, unique(user_id,token))
```

### Moderation & Audit
```sql
reports (id, reporter_id, target_type, target_id, reason, detail,
  status check in ('open','reviewing','resolved','dismissed'), assigned_to_id, resolution,
  created_at, updated_at)

moderation_actions (id, type check in ('warning','content_removed','user_suspended','user_banned',
  'user_reinstated','report_resolved','report_dismissed','note'),
  moderator_id, target_user_id, report_id, target_type, target_id, note, created_at)

moderation_notes (id, moderator_id, target_user_id, body, created_at)

audit_logs (id, actor_id, action, entity_type, entity_id, metadata jsonb, created_at)
```

### Trust & Safety
```sql
user_ratings (id, to_user_id, from_user_id, score int 1..5, context text default 'general', comment, created_at)
blocked_users (id, user_id, blocked_user_id, created_at, unique(user_id,blocked_user_id))
saved_items (id, user_id, item_type check in ('room','job','event','post','resource'), item_id, created_at, unique)
```

## Indexes (selected)
- FK indexes on all foreign keys
- `idx_room_listings_poster`, `idx_room_listings_city_state`, `idx_room_listings_status`, `idx_room_listings_rent`
- `idx_rides_driver`, `idx_rides_status_date`
- `idx_communities_slug`, `idx_community_memberships_user`
- `idx_posts_community`, `idx_posts_status`, `idx_posts_search` (GIN on search_vector)
- `idx_conversation_members_user`, `idx_messages_conversation_created`
- `idx_jobs_state`, `idx_jobs_status`, `idx_jobs_search` (GIN)
- `idx_immigration_resources_category`, `idx_immigration_resources_search` (GIN)
- `idx_expenses_group`, `idx_settlements_group`
- `idx_events_start`, `idx_events_status`
- `idx_notifications_user_read`
- `idx_reports_status`, `idx_reports_target`
- Triggers: `updated_at` auto, `search_vector` FTS on posts/jobs/immigration_resources

## Full-Text Search
- `posts.search_vector`: `to_tsvector('english', coalesce(title,'') || ' ' || coalesce(body,''))`
- `jobs.search_vector`: `to_tsvector('english', title || ' ' || company || ' ' || location || ' ' || array_to_string(skills,' '))`
- `immigration_resources.search_vector`: `to_tsvector('english', title || ' ' || body)`
- Triggers on insert/update maintain vectors.

## RLS Policy Summary
- Profiles: public read, self write
- Rooms/Rides: public read (active), owner write
- Communities: public read; members write posts; membership self-manage
- Chat: members only (enforced via conversation_members)
- Jobs: public read (active); owner/staff write
- Immigration: public read (published); staff write
- Expenses/Events: group/organizer members only
- Notifications/Preferences/Tokens: strictly own
- Reports: reporter read; staff read all
- Moderation/Audit: staff only
- Trust tables: self or public as appropriate

## Migration Files
1. `20260101000000_init.sql` — all tables, indexes, constraints
2. `20260101000001_rls_and_search.sql` — RLS policies, FTS triggers, updated_at triggers
3. `20260101000002_auth_trigger.sql` — auto-create profile on auth signup

## Seed Data
`supabase/seed/seed.sql` — demo user, categories, resources, communities, sample listings/rides/jobs/events.

## Test Schema
Backend integration tests use Flyway with `backend/src/test/resources/db/migration/V1__schema.sql` (mirror of Supabase schema for Testcontainers).