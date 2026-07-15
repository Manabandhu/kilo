import { create } from "zustand";
import { persist, createJSONStorage } from "zustand/middleware";
import type { AuthUser, Session } from "@manabandhu/contracts";
import { zustandSecureStorage } from "../lib/storage";
import { setApiTokenProvider } from "../lib/apiClient";

interface AuthState {
  session: Session | null;
  user: AuthUser | null;
  setSession: (session: Session) => void;
  setUser: (user: AuthUser) => void;
  clear: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      session: null,
      user: null,
      setSession: (session) => set({ session, user: session.user }),
      setUser: (user) => set((s) => ({ user: { ...(s.user ?? user), ...user } })),
      clear: () => set({ session: null, user: null }),
    }),
    {
      name: "manabandhu.auth",
      storage: createJSONStorage(() => zustandSecureStorage),
      onRehydrateStorage: () => (state) => {
        setApiTokenProvider(() => state?.session?.accessToken ?? null);
      },
    },
  ),
);

// Ensure the api client always has the latest token after auth changes.
useAuthStore.subscribe((state) => {
  setApiTokenProvider(() => state.session?.accessToken ?? null);
});
