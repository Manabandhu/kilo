import type { ConfigContext, ExpoConfig } from "expo-router/config";

export default ({ config }: ConfigContext): ExpoConfig => {
  return {
    ...config,
    name: "ManaBandhu",
    slug: "manabandhu",
    scheme: "manabandhu",
    version: "0.1.0",
    orientation: "portrait",
    icon: "./assets/icon.png",
    userInterfaceStyle: "automatic",
    splash: {
      image: "./assets/splash.png",
      resizeMode: "contain",
      backgroundColor: "#6366f1",
    },
    assetBundlePatterns: ["**/*"],
    ios: {
      supportsTablet: true,
      bundleIdentifier: "com.manabandhu.app",
      config: {
        usesNonExemptEncryption: false,
      },
    },
    android: {
      adaptiveIcon: {
        foregroundImage: "./assets/adaptive-icon.png",
        backgroundColor: "#6366f1",
      },
      package: "com.manabandhu.app",
      permissions: ["RECEIVE_BOOT_COMPLETED"],
    },
    web: {
      favicon: "./assets/favicon.png",
      bundler: "metro",
      output: "static",
    },
    extra: {
      supabaseUrl: process.env.EXPO_PUBLIC_SUPABASE_URL || "",
      supabaseAnonKey: process.env.EXPO_PUBLIC_SUPABASE_ANON_KEY || "",
      appEnv: process.env.EXPO_PUBLIC_APP_ENV || "development",
      apiUrl: process.env.EXPO_PUBLIC_API_URL || "http://localhost:8080/api/v1",
    },
    plugins: [
      "expo-router",
      "expo-secure-store",
      "expo-notifications",
      [
        "expo-build-properties",
        {
          android: {
            enableProguardInReleaseBuilds: false,
          },
          ios: {
            useFrameworks: "static",
          },
        },
      ],
    ],
    experiments: {
      typedRoutes: true,
    },
  };
};
