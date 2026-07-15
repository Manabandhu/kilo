/// <reference types="expo/types" />

declare module "expo-env" {
  export const EXPO_PUBLIC_SUPABASE_URL: string | undefined;
  export const EXPO_PUBLIC_SUPABASE_ANON_KEY: string | undefined;
  export const EXPO_PUBLIC_APP_ENV: string | undefined;
  export const EXPO_PUBLIC_API_URL: string | undefined;
}

declare global {
  namespace NodeJS {
    interface ProcessEnv {
      EXPO_PUBLIC_SUPABASE_URL: string;
      EXPO_PUBLIC_SUPABASE_ANON_KEY: string;
      EXPO_PUBLIC_APP_ENV: string;
      EXPO_PUBLIC_API_URL: string;
    }
  }
}

export {};
