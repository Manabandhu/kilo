import { useState } from "react";
import { View, ScrollView } from "react-native";
import { useRouter } from "expo-router";
import { apiClient } from "../../src/lib/apiClient";
import { useAuth } from "../../src/hooks/useAuth";
import { useOnboardingStore } from "../../src/store/onboardingStore";
import { Button } from "../../src/components/ui/Button";
import { Input } from "../../src/components/ui/Input";
import { Screen } from "../../src/components/ui/Screen";
import { Text as T } from "../../src/components/ui/Text";
import type { OnboardingStep } from "@manabandhu/contracts";

const STEPS: { key: OnboardingStep; title: string }[] = [
  { key: "profile", title: "About you" },
  { key: "location", title: "Where are you?" },
  { key: "languages", title: "Languages" },
  { key: "occupation", title: "Occupation" },
  { key: "interests", title: "Interests" },
  { key: "notifications", title: "Notifications" },
  { key: "privacy", title: "Privacy" },
];

export default function OnboardingScreen() {
  const router = useRouter();
  const { user } = useAuth();
  const markStep = useOnboardingStore((s) => s.markStep);
  const [step, setStep] = useState(0);
  const [form, setForm] = useState({
    displayName: user?.displayName ?? "",
    bio: "",
    city: "",
    state: "",
    languages: "English",
    occupation: "",
    interests: "housing,jobs",
    pushEnabled: true,
    showCity: true,
  });

  const set = (k: string, v: string | boolean) => setForm((f) => ({ ...f, [k]: v }));
  const isLast = step === STEPS.length - 1;

  const next = async () => {
    const current = STEPS[step];
    markStep(current.key);
    if (!isLast) {
      setStep((s) => s + 1);
      return;
    }
    // final step: persist profile
    try {
      await apiClient.put("/profiles/me", {
        displayName: form.displayName,
        bio: form.bio,
        city: form.city,
        state: form.state,
        languages: form.languages.split(",").map((s) => s.trim()).filter(Boolean),
        occupation: form.occupation,
        interests: form.interests.split(",").map((s) => s.trim()).filter(Boolean),
        privacy: { showCity: form.showCity },
      });
      await apiClient.patch("/notifications/preferences", { pushEnabled: form.pushEnabled });
    } catch {
      /* best-effort; still complete onboarding locally */
    }
    markStep("privacy");
    router.replace("/(app)/home");
  };

  const stepKey = STEPS[step].key;

  return (
    <Screen title={`${STEPS[step].title} (${step + 1}/${STEPS.length})`}>
      <View className="gap-3">
        <T className="text-sm text-muted">Step {step + 1} of {STEPS.length}</T>
        {stepKey === "profile" && (
          <View className="gap-3">
            <Input label="Display name" value={form.displayName} onChangeText={(v) => set("displayName", v)} accessibilityLabel="Display name" />
            <Input label="Bio" value={form.bio} onChangeText={(v) => set("bio", v)} multiline accessibilityLabel="Bio" />
          </View>
        )}
        {stepKey === "location" && (
          <View className="gap-3">
            <Input label="City" value={form.city} onChangeText={(v) => set("city", v)} accessibilityLabel="City" />
            <Input label="State (2-letter)" value={form.state} onChangeText={(v) => set("state", v.toUpperCase())} maxLength={2} accessibilityLabel="State" />
          </View>
        )}
        {stepKey === "languages" && (
          <Input label="Languages (comma separated)" value={form.languages} onChangeText={(v) => set("languages", v)} accessibilityLabel="Languages" />
        )}
        {stepKey === "occupation" && (
          <Input label="Occupation" value={form.occupation} onChangeText={(v) => set("occupation", v)} accessibilityLabel="Occupation" />
        )}
        {stepKey === "interests" && (
          <Input label="Interests (comma separated)" value={form.interests} onChangeText={(v) => set("interests", v)} accessibilityLabel="Interests" />
        )}
        {stepKey === "notifications" && (
          <Button variant={form.pushEnabled ? "primary" : "outline"} onPress={() => set("pushEnabled", !form.pushEnabled)} accessibilityLabel="Toggle notifications">
            Push notifications: {form.pushEnabled ? "On" : "Off"}
          </Button>
        )}
        {stepKey === "privacy" && (
          <Button variant={form.showCity ? "primary" : "outline"} onPress={() => set("showCity", !form.showCity)} accessibilityLabel="Toggle city visibility">
            Show my city: {form.showCity ? "Yes" : "No"}
          </Button>
        )}
        <View className="flex-row justify-between">
          <Button variant="ghost" onPress={() => setStep((s) => Math.max(0, s - 1))} disabled={step === 0} accessibilityLabel="Back">
            Back
          </Button>
          <Button onPress={next} accessibilityLabel={isLast ? "Finish" : "Next"}>
            {isLast ? "Finish" : "Next"}
          </Button>
        </View>
      </View>
    </Screen>
  );
}
