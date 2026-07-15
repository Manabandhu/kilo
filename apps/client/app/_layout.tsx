import { Stack } from "expo-router";
import { AppProviders } from "../src/providers/AppProviders";
import { ErrorBoundary } from "../src/providers/ErrorBoundary";

export default function RootLayout() {
  return (
    <ErrorBoundary>
      <AppProviders>
        <Stack screenOptions={{ headerShown: false }}>
          <Stack.Screen name="index" />
          <Stack.Screen name="(auth)" />
          <Stack.Screen name="(onboarding)" />
          <Stack.Screen name="(app)" />
        </Stack>
      </AppProviders>
    </ErrorBoundary>
  );
}
