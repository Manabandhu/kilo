-- ============================================================================
-- Seed data for local development QA.
-- Creates a demo user, categories, communities, and sample content.
-- Safe to re-run (uses ON CONFLICT / DELETE guards).
-- ============================================================================

-- Demo user (password: demo1234) - only for local dev
do $$
declare
  demo_id uuid := '11111111-1111-1111-1111-111111111111';
begin
  if not exists (select 1 from auth.users where id = demo_id) then
    insert into auth.users (id, email, encrypted_password, raw_user_meta_data, email_confirmed_at, created_at, updated_at)
    values (
      demo_id,
      'demo@manabandhu.app',
      crypt('demo1234', gen_salt('bf')),
      '{"display_name":"Demo User"}'::jsonb,
      now(),
      now(),
      now()
    );
  end if;

  insert into public.profiles (id, display_name, city, state, languages, occupation, archetype, interests, profile_completion_score, role, onboarding_completed)
  values (demo_id, 'Demo User', 'Sunnyvale', 'CA', array['English','Hindi','Telugu'], 'Software Engineer', 'professional', array['housing','jobs','community'], 80, 'admin', true)
  on conflict (id) do nothing;

  -- Immigration categories
  insert into public.immigration_categories (id, slug, name, description, order_index) values
    ('20000000-0000-0000-0000-000000000001', 'getting-started', 'Getting Started', 'First steps after arrival in the US', 1),
    ('20000000-0000-0000-0000-000000000002', 'visas', 'Visas & Status', 'Common visa categories and maintenance', 2),
    ('20000000-0000-0000-0000-000000000003', 'ssn-itin', 'SSN & ITIN', 'Social Security numbers and tax IDs', 3),
    ('20000000-0000-0000-0000-000000000004', 'banking', 'Banking & Credit', 'Opening accounts and building credit', 4),
    ('20000000-0000-0000-0000-000000000005', 'drivers-license', 'DL & State ID', 'Driver licenses and state IDs', 5)
  on conflict (id) do nothing;

  insert into public.immigration_resources (id, category_id, type, title, body, tags, trusted_contributor, version_date, last_reviewed_at, source_references) values
    ('30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'guide', 'First 30 days checklist', 'General educational checklist: SSN, bank, phone, housing, state ID. This is not legal advice.', array['checklist','newcomer'], true, current_date, now(), array['uscis.gov']),
    ('30000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000002', 'guide', 'Understanding visa status maintenance', 'Educational overview of common non-immigrant statuses. Consult a licensed attorney for your case.', array['visa'], true, current_date, now(), array['travel.state.gov']),
    ('30000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000004', 'checklist', 'Build credit in the US', 'Steps to establish a credit history as a newcomer.', array['credit','banking'], false, current_date, now(), array['consumerfinance.gov'])
  on conflict (id) do nothing;

  -- Communities
  insert into public.communities (id, name, slug, description, kind, category, city, state, member_count) values
    ('40000000-0000-0000-0000-000000000001', 'Bay Area Desis', 'bay-area-desis', 'South Asians in the San Francisco Bay Area', 'city', 'city', 'San Francisco', 'CA', 1240),
    ('40000000-0000-0000-0000-000000000002', 'Housing & Roommates', 'housing-roommates', 'Find housing and roommates', 'topic', 'housing', null, null, 3200),
    ('40000000-0000-0000-0000-000000000003', 'Tech Jobs & Referrals', 'tech-jobs-referrals', 'Job posts and referrals in tech', 'topic', 'jobs', null, null, 5400),
    ('40000000-0000-0000-0000-000000000004', 'Students & CPT/OPT', 'students-cpt-opt', 'International student community', 'topic', 'students', null, null, 2100)
  on conflict (id) do nothing;

  insert into public.community_memberships (community_id, user_id, role) values
    ('40000000-0000-0000-0000-000000000001', demo_id, 'member'),
    ('40000000-0000-0000-0000-000000000002', demo_id, 'moderator')
  on conflict do nothing;

  -- Sample room listing
  insert into public.room_listings (id, title, description, monthly_rent, city, state, room_type, property_type, furnished, utilities_included, amenities, parking, pet_policy, smoking_policy, occupancy, gender_preference, contact_preference, images, status, poster_id)
  values ('50000000-0000-0000-0000-000000000001', 'Private room near Caltrain', 'Bright private room in a 2BR apartment, 10 min walk to Caltrain.', 1450.00, 'Sunnyvale', 'CA', 'private', 'apartment', 'furnished', true, array['wifi','laundry','gym'], true, 'cats', 'no_smoking', 1, 'any', 'in_app', array['https://picsum.photos/seed/room1/800/600'], 'active', demo_id)
  on conflict (id) do nothing;

  -- Sample ride
  insert into public.rides (id, type, frequency, origin, origin_location, destination, destination_location, date, time, time_flexibility, available_seats, suggested_contribution, status, driver_id)
  values ('60000000-0000-0000-0000-000000000001', 'offer', 'weekly', 'Sunnyvale', '{"lat":37.3688,"lng":-122.0363}'::jsonb, 'SFO Airport', '{"lat":37.6213,"lng":-122.3790}'::jsonb, current_date + 2, '08:00', 'plus_minus_30', 3, 15.00, 'published', demo_id)
  on conflict (id) do nothing;

  -- Sample job
  insert into public.jobs (id, title, company, location, state, work_mode, employment_type, experience_level, salary_min, salary_max, currency, skills, sponsorship_offered, source, posted_by_id)
  values ('70000000-0000-0000-0000-000000000001', 'Junior Backend Engineer', 'BayTech Inc', 'Remote', 'CA', 'remote', 'full_time', 'entry', 90000, 120000, 'USD', array['Java','Spring','PostgreSQL'], true, 'company', demo_id)
  on conflict (id) do nothing;

  -- Sample event
  insert into public.events (id, title, description, venue_type, city, state, start_time, capacity, organizer_id, status)
  values ('80000000-0000-0000-0000-000000000001', 'Bay Area Newcomers Meetup', 'Casual meetup for newcomers to the Bay Area.', 'in_person', 'San Jose', 'CA', now() + interval '7 days', 50, demo_id, 'scheduled')
  on conflict (id) do nothing;
end $$;
