import { create } from "zustand";
import { persist, createJSONStorage } from "zustand/middleware";
import { ONBOARDING_STEPS, type OnboardingStep } from "@manabandhu/contracts";
import { zustandSecureStorage } from "../lib/storage";

interface OnboardingState {
  completed: OnboardingStep[];
  markStep: (step: OnboardingStep) => void;
  isComplete: () => boolean;
  reset: () => void;
}

export const useOnboardingStore = create<OnboardingState>()(
  persist(
    (set, get) => ({
      completed: [],
      markStep: (step) =>
        set((s) => ({
          completed: s.completed.includes(step) ? s.completed : [...s.completed, step],
        })),
      isComplete: () => get().completed.length >= ONBOARDING_STEPS.length,
      reset: () => set({ completed: [] }),
    }),
    {
      name: "manabandhu.onboarding",
      storage: createJSONStorage(() => zustandSecureStorage),
    },
  ),
);
