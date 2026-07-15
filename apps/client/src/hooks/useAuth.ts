import { useAuthStore } from "../store/authStore";
import { authService } from "../services/auth.service";
import type { Session } from "@manabandhu/contracts";

export function useAuth() {
  const session = useAuthStore((s) => s.session);
  const user = useAuthStore((s) => s.user);
  const setSession = useAuthStore((s) => s.setSession);
  const clear = useAuthStore((s) => s.clear);

  return {
    session,
    user,
    isAuthenticated: !!session,
    isAdmin: user?.role === "admin",
    isModerator: user?.role === "moderator" || user?.role === "admin",
    signIn: async (email: string, password: string): Promise<Session> => {
      const s = await authService.signIn(email, password);
      setSession(s);
      return s;
    },
    signUp: async (email: string, password: string, displayName: string): Promise<Session> => {
      const s = await authService.signUp(email, password, displayName);
      setSession(s);
      return s;
    },
    signOut: async () => {
      await authService.signOut();
      clear();
    },
    resetPassword: (email: string) => authService.resetPassword(email),
  };
}
