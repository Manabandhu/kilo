-- ============================================================================
-- Row Level Security, full-text search triggers, and helper functions.
-- Direct Supabase access is protected by RLS; the Spring Boot backend applies
-- business rules on top of this. Frontend uses the anon key (RLS enforced).
-- ============================================================================

alter table public.profiles enable row level security;
alter table public.room_listings enable row level security;
alter table public.rides enable row level security;
alter table public.ride_requests enable row level security;
alter table public.ride_ratings enable row level security;
alter table public.communities enable row level security;
alter table public.community_memberships enable row level security;
alter table public.posts enable row level security;
alter table public.comments enable row level security;
alter table public.conversations enable row level security;
alter table public.conversation_members enable row level security;
alter table public.messages enable row level security;
alter table public.message_reads enable row level security;
alter table public.jobs enable row level security;
alter table public.referrals enable row level security;
alter table public.immigration_categories enable row level security;
alter table public.immigration_resources enable row level security;
alter table public.immigration_questions enable row level security;
alter table public.expense_groups enable row level security;
alter table public.expense_group_members enable row level security;
alter table public.expenses enable row level security;
alter table public.settlements enable row level security;
alter table public.events enable row level security;
alter table public.event_rsvps enable row level security;
alter table public.notifications enable row level security;
alter table public.notification_preferences enable row level security;
alter table public.push_tokens enable row level security;
alter table public.reports enable row level security;
alter table public.moderation_actions enable row level security;
alter table public.moderation_notes enable row level security;
alter table public.audit_logs enable row level security;
alter table public.user_ratings enable row level security;
alter table public.blocked_users enable row level security;
alter table public.saved_items enable row level security;

-- Helper: current user id
create or replace function public.current_uid() returns uuid
  language sql stable as $$ select auth.uid() $$;

-- Helper: is current user an admin/moderator
create or replace function public.is_staff() returns boolean
  language sql stable as $$
    select exists (
      select 1 from public.profiles p
      where p.id = auth.uid() and p.role in ('admin', 'moderator')
    )
  $$;

-- Profiles: anyone authenticated can read; users manage only their own.
create policy profiles_select on public.profiles for select using (true);
create policy profiles_insert on public.profiles for insert with check (id = public.current_uid());
create policy profiles_update on public.profiles for update using (id = public.current_uid()) with check (id = public.current_uid());

-- Room listings: public read of active; owner write.
create policy rooms_select on public.room_listings for select using (status <> 'deleted' and deleted_at is null);
create policy rooms_insert on public.room_listings for insert with check (poster_id = public.current_uid());
create policy rooms_update on public.room_listings for update using (poster_id = public.current_uid()) with check (poster_id = public.current_uid());
create policy rooms_delete on public.room_listings for update using (poster_id = public.current_uid()) with check (poster_id = public.current_uid());

-- Rides
create policy rides_select on public.rides for select using (deleted_at is null);
create policy rides_insert on public.rides for insert with check (driver_id = public.current_uid());
create policy rides_update on public.rides for update using (driver_id = public.current_uid()) with check (driver_id = public.current_uid());
create policy ride_requests_select on public.ride_requests for select using (rider_id = public.current_uid() or exists (select 1 from public.rides r where r.id = ride_id and r.driver_id = public.current_uid()));
create policy ride_requests_insert on public.ride_requests for insert with check (rider_id = public.current_uid());

-- Community: public read; members can post within memberships.
create policy communities_select on public.communities for select using (true);
create policy community_memberships_select on public.community_memberships for select using (user_id = public.current_uid() or public.is_staff());
create policy community_memberships_insert on public.community_memberships for insert with check (user_id = public.current_uid());
create policy community_memberships_delete on public.community_memberships for delete using (user_id = public.current_uid());

create policy posts_select on public.posts for select using (status <> 'deleted' and deleted_at is null);
create policy posts_insert on public.posts for insert with check (author_id = public.current_uid());
create policy posts_update on public.posts for update using (author_id = public.current_uid() or public.is_staff()) with check (author_id = public.current_uid() or public.is_staff());
create policy comments_select on public.comments for select using (status <> 'deleted');
create policy comments_insert on public.comments for insert with check (author_id = public.current_uid());
create policy comments_update on public.comments for update using (author_id = public.current_uid() or public.is_staff());

-- Chat: members only
create policy conversations_select on public.conversations for select using (exists (select 1 from public.conversation_members cm where cm.conversation_id = id and cm.user_id = public.current_uid()));
create policy conversations_insert on public.conversations for insert with check (true);
create policy conversation_members_select on public.conversation_members for select using (user_id = public.current_uid());
create policy conversation_members_insert on public.conversation_members for insert with check (user_id = public.current_uid());
create policy messages_select on public.messages for select using (exists (select 1 from public.conversation_members cm where cm.conversation_id = conversation_id and cm.user_id = public.current_uid()));
create policy messages_insert on public.messages for insert with check (sender_id = public.current_uid());
create policy messages_update on public.messages for update using (sender_id = public.current_uid());
create policy message_reads_upsert on public.message_reads for insert with check (user_id = public.current_uid());

-- Jobs: public read of active
create policy jobs_select on public.jobs for select using (status <> 'deleted' and deleted_at is null);
create policy jobs_insert on public.jobs for insert with check (posted_by_id = public.current_uid() or public.is_staff());
create policy jobs_update on public.jobs for update using (posted_by_id = public.current_uid() or public.is_staff()) with check (posted_by_id = public.current_uid() or public.is_staff());

-- Immigration: read public; staff manages
create policy imm_categories_select on public.immigration_categories for select using (true);
create policy imm_resources_select on public.immigration_resources for select using (status = 'published');
create policy imm_resources_write on public.immigration_resources for all using (public.is_staff()) with check (public.is_staff());
create policy imm_questions_select on public.immigration_questions for select using (author_id = public.current_uid() or public.is_staff() or status = 'answered');
create policy imm_questions_insert on public.immigration_questions for insert with check (author_id = public.current_uid());

-- Expenses: group members only
create policy expense_groups_select on public.expense_groups for select using (exists (select 1 from public.expense_group_members m where m.group_id = id and m.user_id = public.current_uid()));
create policy expense_groups_insert on public.expense_groups for insert with check (created_by_id = public.current_uid());
create policy expense_group_members_select on public.expense_group_members for select using (user_id = public.current_uid() or exists (select 1 from public.expense_group_members m where m.group_id = group_id and m.user_id = public.current_uid()));
create policy expenses_select on public.expenses for select using (exists (select 1 from public.expense_group_members m where m.group_id = group_id and m.user_id = public.current_uid()));
create policy expenses_insert on public.expenses for insert with check (exists (select 1 from public.expense_group_members m where m.group_id = group_id and m.user_id = public.current_uid()));
create policy settlements_select on public.settlements for select using (from_user_id = public.current_uid() or to_user_id = public.current_uid() or exists (select 1 from public.expense_group_members m where m.group_id = group_id and m.user_id = public.current_uid()));

-- Events
create policy events_select on public.events for select using (status <> 'deleted' and deleted_at is null);
create policy events_insert on public.events for insert with check (organizer_id = public.current_uid());
create policy events_update on public.events for update using (organizer_id = public.current_uid() or public.is_staff()) with check (organizer_id = public.current_uid() or public.is_staff());
create policy event_rsvps_select on public.event_rsvps for select using (user_id = public.current_uid());
create policy event_rsvps_upsert on public.event_rsvps for insert with check (user_id = public.current_uid());

-- Notifications: strictly own
create policy notifications_select on public.notifications for select using (user_id = public.current_uid());
create policy notifications_update on public.notifications for update using (user_id = public.current_uid());
create policy notification_prefs_all on public.notification_preferences for all using (user_id = public.current_uid()) with check (user_id = public.current_uid());
create policy push_tokens_all on public.push_tokens for all using (user_id = public.current_uid()) with check (user_id = public.current_uid());

-- Reports: own insert; staff reads
create policy reports_insert on public.reports for insert with check (reporter_id = public.current_uid());
create policy reports_select on public.reports for select using (reporter_id = public.current_uid() or public.is_staff());

-- Trust
create policy user_ratings_select on public.user_ratings for select using (true);
create policy user_ratings_insert on public.user_ratings for insert with check (from_user_id = public.current_uid());
create policy blocked_users_all on public.blocked_users for all using (user_id = public.current_uid()) with check (user_id = public.current_uid());
create policy saved_items_all on public.saved_items for all using (user_id = public.current_uid()) with check (user_id = public.current_uid());

-- Staff-only tables
create policy moderation_actions_staff on public.moderation_actions for select using (public.is_staff());
create policy moderation_notes_staff on public.moderation_notes for select using (public.is_staff());
create policy audit_logs_staff on public.audit_logs for select using (public.is_staff());

-- Full-text search vector maintenance
create or replace function public.posts_tsvector() returns trigger as $$
begin
  new.search_vector := to_tsvector('english', coalesce(new.title, '') || ' ' || coalesce(new.body, ''));
  return new;
end;
$$ language plpgsql;

create trigger posts_search_update before insert or update on public.posts
  for each row execute function public.posts_tsvector();

create or replace function public.jobs_tsvector() returns trigger as $$
begin
  new.search_vector := to_tsvector('english', coalesce(new.title, '') || ' ' || coalesce(new.company, '') || ' ' || coalesce(new.location, '') || ' ' || coalesce(array_to_string(new.skills, ' '), ''));
  return new;
end;
$$ language plpgsql;

create trigger jobs_search_update before insert or update on public.jobs
  for each row execute function public.jobs_tsvector();

create or replace function public.immigration_tsvector() returns trigger as $$
begin
  new.search_vector := to_tsvector('english', coalesce(new.title, '') || ' ' || coalesce(new.body, ''));
  return new;
end;
$$ language plpgsql;

create trigger immigration_search_update before insert or update on public.immigration_resources
  for each row execute function public.immigration_tsvector();

-- Updated-at maintenance
create or replace function public.set_updated_at() returns trigger as $$
begin
  new.updated_at := now();
  return new;
end;
$$ language plpgsql;

create trigger profiles_updated_at before update on public.profiles
  for each row execute function public.set_updated_at();
create trigger room_listings_updated_at before update on public.room_listings
  for each row execute function public.set_updated_at();
create trigger rides_updated_at before update on public.rides
  for each row execute function public.set_updated_at();
create trigger posts_updated_at before update on public.posts
  for each row execute function public.set_updated_at();
create trigger comments_updated_at before update on public.comments
  for each row execute function public.set_updated_at();
create trigger messages_updated_at before update on public.messages
  for each row execute function public.set_updated_at();
create trigger conversations_updated_at before update on public.conversations
  for each row execute function public.set_updated_at();
create trigger jobs_updated_at before update on public.jobs
  for each row execute function public.set_updated_at();
create trigger immigration_resources_updated_at before update on public.immigration_resources
  for each row execute function public.set_updated_at();
create trigger events_updated_at before update on public.events
  for each row execute function public.set_updated_at();
