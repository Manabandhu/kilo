export const env = {
  supabaseUrl: process.env.EXPO_PUBLIC_SUPABASE_URL ?? "http://localhost:54321",
  supabaseAnonKey: process.env.EXPO_PUBLIC_SUPABASE_ANON_KEY ?? "public-anon-key",
  apiBaseUrl: process.env.EXPO_PUBLIC_API_URL ?? "http://localhost:8080/api/v1",
  appEnv: process.env.EXPO_PUBLIC_APP_ENV ?? "development",
  mapboxToken: process.env.EXPO_PUBLIC_MAPBOX_TOKEN ?? "",
} as const;
