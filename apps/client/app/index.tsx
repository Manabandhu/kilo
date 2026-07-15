import { useEffect } from "react";
import { Redirect, useRouter } from "expo-router";
import { useAuth } from "../src/hooks/useAuth";
import { useOnboardingStore } from "../src/store/onboardingStore";

export default function Index() {
  const { isAuthenticated } = useAuth();
  const isComplete = useOnboardingStore((s) => s.completed.length > 0);
  const router = useRouter();

  useEffect(() => {
    if (!isAuthenticated) {
      router.replace("/(auth)/login");
    } else if (!isComplete) {
      router.replace("/(onboarding)");
    } else {
      router.replace("/(app)/home");
    }
  }, [isAuthenticated, isComplete, router]);

  return null;
}
