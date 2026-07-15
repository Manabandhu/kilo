import { createClient } from "@supabase/supabase-js";
import { env } from "../lib/env";

// Browser/anon client for Auth, Storage, and Realtime only.
// Business operations go through the Spring Boot REST API (see lib/apiClient).
export const supabase = createClient(env.supabaseUrl, env.supabaseAnonKey, {
  auth: {
    persistSession: true,
    autoRefreshToken: true,
    detectSessionInUrl: false,
  },
});
