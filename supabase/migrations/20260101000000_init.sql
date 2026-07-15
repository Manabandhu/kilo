-- ============================================================================
-- ManaBandhu - Initial schema (Supabase is the source of truth for the schema)
-- UUID PKs, created_at/updated_at/created_by/updated_by, soft deletes where
-- appropriate, indexes, Row Level Security, and PostgreSQL full-text search.
-- NOTE: Flyway in the backend is used only for integration tests (see
-- backend/src/test/resources/db/migration). The production schema is owned
-- here to avoid two systems editing the same schema unpredictably.
-- ============================================================================

create extension if not exists "pgcrypto";
create extension if not exists "pg_trgm";
-- create extension if not exists postgis; -- enable when geo radius search needs it

-- ---------------------------------------------------------------------------
-- Profiles (1:1 with auth.users)
-- ---------------------------------------------------------------------------
create table if not exists public.profiles (
    id uuid primary key references auth.users (id) on delete cascade,
    display_name text not null,
    avatar_url text,
    bio text,
    city text,
    state text,
    languages text[] not null default '{}',
    occupation text,
    archetype text check (archetype in ('student', 'professional', 'family', 'newcomer')),
    university text,
    employer text,
    interests text[] not null default '{}',
    profile_completion_score int not null default 0 check (profile_completion_score between 0 and 100),
    rating_average numeric(3, 2) not null default 0,
    rating_count int not null default 0,
    contribution_level int not null default 0,
    privacy jsonb not null default '{"showCity":true,"showEmployer":true,"showUniversity":true,"allowDirectMessages":true,"showOnlineStatus":true}'::jsonb,
    onboarding_completed boolean not null default false,
    role text not null default 'user' check (role in ('user', 'moderator', 'admin')),
    account_status text not null default 'active' check (account_status in ('active', 'warned', 'suspended', 'banned', 'deleted')),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

-- ---------------------------------------------------------------------------
-- Rooms / housing listings
-- ---------------------------------------------------------------------------
create table if not exists public.room_listings (
    id uuid primary key default gen_random_uuid(),
    title text not null,
    description text not null,
    monthly_rent numeric(12, 2) not null check (monthly_rent >= 0),
    security_deposit numeric(12, 2),
    city text not null,
    state text not null,
    zip text,
    approximate_location jsonb,
    exact_address_shared boolean not null default false,
    room_type text not null,
    property_type text not null,
    furnished text not null,
    available_date date,
    lease_duration_months int,
    utilities_included boolean not null default false,
    amenities text[] not null default '{}',
    parking boolean not null default false,
    pet_policy text not null default 'none',
    smoking_policy text not null default 'no_smoking',
    occupancy int not null default 1 check (occupancy > 0),
    gender_preference text not null default 'any',
    contact_preference text not null default 'in_app',
    images text[] not null default '{}',
    status text not null default 'active' check (status in ('active', 'paused', 'closed', 'deleted')),
    poster_id uuid not null references public.profiles (id) on delete cascade,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    created_by uuid references public.profiles (id),
    updated_by uuid references public.profiles (id),
    deleted_at timestamptz
);

-- ---------------------------------------------------------------------------
-- Rides
-- ---------------------------------------------------------------------------
create table if not exists public.rides (
    id uuid primary key default gen_random_uuid(),
    type text not null check (type in ('offer', 'request')),
    frequency text not null default 'one_time' check (frequency in ('one_time', 'daily', 'weekly')),
    origin text not null,
    origin_location jsonb not null,
    destination text not null,
    destination_location jsonb not null,
    stops jsonb not null default '[]',
    date date not null,
    time text not null,
    time_flexibility text not null default 'exact',
    available_seats int not null default 1 check (available_seats >= 0),
    suggested_contribution numeric(12, 2),
    luggage_info text,
    driver_notes text,
    status text not null default 'draft' check (status in ('draft', 'published', 'full', 'in_progress', 'completed', 'cancelled')),
    driver_id uuid not null references public.profiles (id) on delete cascade,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    created_by uuid references public.profiles (id),
    updated_by uuid references public.profiles (id),
    deleted_at timestamptz
);

create table if not exists public.ride_requests (
    id uuid primary key default gen_random_uuid(),
    ride_id uuid not null references public.rides (id) on delete cascade,
    rider_id uuid not null references public.profiles (id) on delete cascade,
    seats_requested int not null default 1 check (seats_requested > 0),
    rider_notes text,
    status text not null default 'pending' check (status in ('pending', 'approved', 'rejected', 'cancelled')),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique (ride_id, rider_id)
);

create table if not exists public.ride_ratings (
    id uuid primary key default gen_random_uuid(),
    ride_id uuid not null references public.rides (id) on delete cascade,
    from_user_id uuid not null references public.profiles (id) on delete cascade,
    to_user_id uuid not null references public.profiles (id) on delete cascade,
    score int not null check (score between 1 and 5),
    comment text,
    created_at timestamptz not null default now()
);

-- ---------------------------------------------------------------------------
-- Community
-- ---------------------------------------------------------------------------
create table if not exists public.communities (
    id uuid primary key default gen_random_uuid(),
    name text not null,
    slug text not null unique,
    description text not null,
    kind text not null check (kind in ('city', 'topic')),
    category text not null,
    city text,
    state text,
    member_count int not null default 0,
    cover_image_url text,
    pinned_post_ids uuid[] not null default '{}',
    created_at timestamptz not null default now()
);

create table if not exists public.community_memberships (
    community_id uuid not null references public.communities (id) on delete cascade,
    user_id uuid not null references public.profiles (id) on delete cascade,
    role text not null default 'member' check (role in ('member', 'moderator')),
    joined_at timestamptz not null default now(),
    primary key (community_id, user_id)
);

create table if not exists public.posts (
    id uuid primary key default gen_random_uuid(),
    community_id uuid not null references public.communities (id) on delete cascade,
    author_id uuid not null references public.profiles (id) on delete cascade,
    title text,
    body text not null,
    tags text[] not null default '{}',
    pinned boolean not null default false,
    reactions jsonb not null default '{"like":0,"helpful":0,"celebrate":0,"support":0}'::jsonb,
    status text not null default 'published' check (status in ('published', 'deleted', 'moderated')),
    search_vector tsvector,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    created_by uuid references public.profiles (id),
    updated_by uuid references public.profiles (id),
    deleted_at timestamptz
);

create table if not exists public.comments (
    id uuid primary key default gen_random_uuid(),
    post_id uuid not null references public.posts (id) on delete cascade,
    parent_id uuid references public.comments (id) on delete cascade,
    author_id uuid not null references public.profiles (id) on delete cascade,
    body text not null,
    helpful_votes int not null default 0,
    status text not null default 'published' check (status in ('published', 'deleted', 'moderated')),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

-- ---------------------------------------------------------------------------
-- Chat
-- ---------------------------------------------------------------------------
create table if not exists public.conversations (
    id uuid primary key default gen_random_uuid(),
    kind text not null check (kind in ('direct', 'group')),
    title text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table if not exists public.conversation_members (
    conversation_id uuid not null references public.conversations (id) on delete cascade,
    user_id uuid not null references public.profiles (id) on delete cascade,
    role text not null default 'member' check (role in ('member', 'admin')),
    muted boolean not null default false,
    joined_at timestamptz not null default now(),
    primary key (conversation_id, user_id)
);

create table if not exists public.messages (
    id uuid primary key default gen_random_uuid(),
    conversation_id uuid not null references public.conversations (id) on delete cascade,
    sender_id uuid not null references public.profiles (id) on delete cascade,
    body text not null,
    attachments jsonb not null default '[]',
    reply_to_id uuid references public.messages (id) on delete set null,
    reaction text,
    edited boolean not null default false,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table if not exists public.message_reads (
    message_id uuid not null references public.messages (id) on delete cascade,
    user_id uuid not null references public.profiles (id) on delete cascade,
    read_at timestamptz not null default now(),
    primary key (message_id, user_id)
);

-- ---------------------------------------------------------------------------
-- Jobs
-- ---------------------------------------------------------------------------
create table if not exists public.jobs (
    id uuid primary key default gen_random_uuid(),
    title text not null,
    company text not null,
    location text not null,
    state text,
    work_mode text not null check (work_mode in ('remote', 'hybrid', 'onsite')),
    employment_type text not null,
    experience_level text not null,
    salary_min numeric(12, 2),
    salary_max numeric(12, 2),
    currency text not null default 'USD',
    skills text[] not null default '{}',
    sponsorship_offered boolean,
    source text not null default 'community' check (source in ('community', 'company', 'feed', 'admin')),
    application_url text,
    posted_by_id uuid references public.profiles (id) on delete set null,
    expires_at date,
    posted_at timestamptz not null default now(),
    status text not null default 'active' check (status in ('active', 'closed', 'deleted')),
    search_vector tsvector,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    deleted_at timestamptz
);

create table if not exists public.referrals (
    id uuid primary key default gen_random_uuid(),
    job_id uuid references public.jobs (id) on delete cascade,
    kind text not null check (kind in ('request', 'offer')),
    from_user_id uuid not null references public.profiles (id) on delete cascade,
    to_user_id uuid references public.profiles (id) on delete cascade,
    company text,
    note text,
    status text not null default 'open' check (status in ('open', 'connected', 'closed')),
    created_at timestamptz not null default now()
);

-- ---------------------------------------------------------------------------
-- Immigration resources
-- ---------------------------------------------------------------------------
create table if not exists public.immigration_categories (
    id uuid primary key default gen_random_uuid(),
    slug text not null unique,
    name text not null,
    description text not null,
    order_index int not null default 0
);

create table if not exists public.immigration_resources (
    id uuid primary key default gen_random_uuid(),
    category_id uuid not null references public.immigration_categories (id) on delete cascade,
    type text not null check (type in ('guide', 'checklist', 'faq')),
    title text not null,
    body text not null,
    tags text[] not null default '{}',
    version_date date not null default current_date,
    last_reviewed_at timestamptz not null default now(),
    source_references text[] not null default '{}',
    trusted_contributor boolean not null default false,
    author_id uuid references public.profiles (id) on delete set null,
    helpful_count int not null default 0,
    status text not null default 'published' check (status in ('draft', 'published', 'archived')),
    search_vector tsvector,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table if not exists public.immigration_questions (
    id uuid primary key default gen_random_uuid(),
    resource_id uuid references public.immigration_resources (id) on delete cascade,
    author_id uuid not null references public.profiles (id) on delete cascade,
    body text not null,
    answer text,
    answered_by_id uuid references public.profiles (id) on delete set null,
    status text not null default 'open' check (status in ('open', 'answered', 'flagged')),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

-- ---------------------------------------------------------------------------
-- Expenses
-- ---------------------------------------------------------------------------
create table if not exists public.expense_groups (
    id uuid primary key default gen_random_uuid(),
    name text not null,
    currency text not null default 'USD',
    created_by_id uuid not null references public.profiles (id) on delete cascade,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table if not exists public.expense_group_members (
    group_id uuid not null references public.expense_groups (id) on delete cascade,
    user_id uuid not null references public.profiles (id) on delete cascade,
    joined_at timestamptz not null default now(),
    primary key (group_id, user_id)
);

create table if not exists public.expenses (
    id uuid primary key default gen_random_uuid(),
    group_id uuid not null references public.expense_groups (id) on delete cascade,
    title text not null,
    description text,
    total_amount numeric(12, 2) not null,
    currency text not null default 'USD',
    paid_by_ids uuid[] not null,
    split_strategy text not null check (split_strategy in ('equal', 'exact', 'percentage', 'shares')),
    splits jsonb not null,
    date date not null,
    category text,
    receipt_url text,
    created_by_id uuid not null references public.profiles (id) on delete cascade,
    created_at timestamptz not null default now()
);

create table if not exists public.settlements (
    id uuid primary key default gen_random_uuid(),
    group_id uuid not null references public.expense_groups (id) on delete cascade,
    from_user_id uuid not null references public.profiles (id) on delete cascade,
    to_user_id uuid not null references public.profiles (id) on delete cascade,
    amount numeric(12, 2) not null,
    currency text not null default 'USD',
    status text not null default 'pending' check (status in ('pending', 'completed')),
    note text,
    recorded_by_id uuid not null references public.profiles (id) on delete cascade,
    created_at timestamptz not null default now()
);

-- ---------------------------------------------------------------------------
-- Events
-- ---------------------------------------------------------------------------
create table if not exists public.events (
    id uuid primary key default gen_random_uuid(),
    title text not null,
    description text not null,
    venue_type text not null check (venue_type in ('online', 'in_person')),
    location text,
    online_url text,
    city text,
    state text,
    start_time timestamptz not null,
    end_time timestamptz,
    capacity int,
    organizer_id uuid not null references public.profiles (id) on delete cascade,
    cover_image_url text,
    status text not null default 'scheduled' check (status in ('scheduled', 'cancelled', 'completed')),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    deleted_at timestamptz
);

create table if not exists public.event_rsvps (
    event_id uuid not null references public.events (id) on delete cascade,
    user_id uuid not null references public.profiles (id) on delete cascade,
    status text not null check (status in ('going', 'interested', 'waitlist', 'declined')),
    created_at timestamptz not null default now(),
    primary key (event_id, user_id)
);

-- ---------------------------------------------------------------------------
-- Notifications
-- ---------------------------------------------------------------------------
create table if not exists public.notifications (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references public.profiles (id) on delete cascade,
    type text not null,
    title text not null,
    body text not null,
    deep_link text,
    read boolean not null default false,
    created_at timestamptz not null default now()
);

create table if not exists public.notification_preferences (
    user_id uuid primary key references public.profiles (id) on delete cascade,
    push_enabled boolean not null default true,
    email_enabled boolean not null default true,
    messages boolean not null default true,
    rides boolean not null default true,
    listings boolean not null default true,
    posts boolean not null default true,
    events boolean not null default true,
    expenses boolean not null default true,
    moderation boolean not null default true,
    marketing boolean not null default false
);

create table if not exists public.push_tokens (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references public.profiles (id) on delete cascade,
    token text not null,
    platform text not null,
    device_id text,
    created_at timestamptz not null default now(),
    unique (user_id, token)
);

-- ---------------------------------------------------------------------------
-- Moderation
-- ---------------------------------------------------------------------------
create table if not exists public.reports (
    id uuid primary key default gen_random_uuid(),
    reporter_id uuid not null references public.profiles (id) on delete cascade,
    target_type text not null,
    target_id uuid not null,
    reason text not null,
    detail text,
    status text not null default 'open' check (status in ('open', 'reviewing', 'resolved', 'dismissed')),
    assigned_to_id uuid references public.profiles (id),
    resolution text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table if not exists public.moderation_actions (
    id uuid primary key default gen_random_uuid(),
    type text not null,
    moderator_id uuid not null references public.profiles (id) on delete cascade,
    target_user_id uuid references public.profiles (id) on delete set null,
    report_id uuid references public.reports (id) on delete set null,
    target_type text,
    target_id uuid,
    note text,
    created_at timestamptz not null default now()
);

create table if not exists public.moderation_notes (
    id uuid primary key default gen_random_uuid(),
    moderator_id uuid not null references public.profiles (id) on delete cascade,
    target_user_id uuid not null references public.profiles (id) on delete cascade,
    body text not null,
    created_at timestamptz not null default now()
);

create table if not exists public.audit_logs (
    id uuid primary key default gen_random_uuid(),
    actor_id uuid references public.profiles (id),
    action text not null,
    entity_type text,
    entity_id uuid,
    metadata jsonb,
    created_at timestamptz not null default now()
);

-- ---------------------------------------------------------------------------
-- Trust & safety
-- ---------------------------------------------------------------------------
create table if not exists public.user_ratings (
    id uuid primary key default gen_random_uuid(),
    to_user_id uuid not null references public.profiles (id) on delete cascade,
    from_user_id uuid not null references public.profiles (id) on delete cascade,
    score int not null check (score between 1 and 5),
    context text not null default 'general',
    comment text,
    created_at timestamptz not null default now()
);

create table if not exists public.blocked_users (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references public.profiles (id) on delete cascade,
    blocked_user_id uuid not null references public.profiles (id) on delete cascade,
    created_at timestamptz not null default now(),
    unique (user_id, blocked_user_id)
);

create table if not exists public.saved_items (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references public.profiles (id) on delete cascade,
    item_type text not null check (item_type in ('room', 'job', 'event', 'post', 'resource')),
    item_id uuid not null,
    created_at timestamptz not null default now(),
    unique (user_id, item_type, item_id)
);

-- Indexes
create index if not exists idx_room_listings_poster on public.room_listings (poster_id);
create index if not exists idx_room_listings_city_state on public.room_listings (city, state);
create index if not exists idx_room_listings_status on public.room_listings (status);
create index if not exists idx_room_listings_rent on public.room_listings (monthly_rent);
create index if not exists idx_rides_driver on public.rides (driver_id);
create index if not exists idx_rides_status_date on public.rides (status, date);
create index if not exists idx_rides_requests_ride on public.ride_requests (ride_id);
create index if not exists idx_communities_slug on public.communities (slug);
create index if not exists idx_community_memberships_user on public.community_memberships (user_id);
create index if not exists idx_posts_community on public.posts (community_id);
create index if not exists idx_posts_status on public.posts (status);
create index if not exists idx_posts_search on public.posts using gin (search_vector);
create index if not exists idx_comments_post on public.comments (post_id);
create index if not exists idx_conversation_members_user on public.conversation_members (user_id);
create index if not exists idx_messages_conversation on public.messages (conversation_id, created_at);
create index if not exists idx_jobs_state on public.jobs (state);
create index if not exists idx_jobs_status on public.jobs (status);
create index if not exists idx_jobs_search on public.jobs using gin (search_vector);
create index if not exists idx_immigration_resources_category on public.immigration_resources (category_id);
create index if not exists idx_immigration_resources_search on public.immigration_resources using gin (search_vector);
create index if not exists idx_expenses_group on public.expenses (group_id);
create index if not exists idx_settlements_group on public.settlements (group_id);
create index if not exists idx_events_start on public.events (start_time);
create index if not exists idx_events_status on public.events (status);
create index if not exists idx_event_rsvps_user on public.event_rsvps (user_id);
create index if not exists idx_notifications_user_read on public.notifications (user_id, read);
create index if not exists idx_reports_status on public.reports (status);
create index if not exists idx_reports_target on public.reports (target_type, target_id);
