import type { AuthUser, Session } from "@manabandhu/contracts";
import { supabase } from "./supabase";

function toAuthUser(user: { id: string; email?: string; email_confirmed_at?: string | null; phone?: string | null; created_at?: string; app_metadata?: Record<string, unknown> }): AuthUser {
  const role = (user.app_metadata?.["role"] as string) ?? "user";
  return {
    id: user.id,
    email: user.email ?? "",
    emailVerified: !!user.email_confirmed_at,
    phoneVerified: !!user.phone,
    role: (role === "admin" || role === "moderator" ? role : "user") as AuthUser["role"],
    accountStatus: "active",
    createdAt: user.created_at ?? new Date().toISOString(),
  };
}

function toSession(session: { access_token: string; refresh_token: string; expires_at?: number; expires_in?: number; user: any }): Session {
  const expiresAt = session.expires_at
    ? new Date(session.expires_at * 1000).toISOString()
    : new Date(Date.now() + (session.expires_in ?? 3600) * 1000).toISOString();
  return {
    accessToken: session.access_token,
    refreshToken: session.refresh_token,
    expiresAt,
    user: toAuthUser(session.user),
  };
}

export const authService = {
  async signUp(email: string, password: string, displayName: string): Promise<Session> {
    const { data, error } = await supabase.auth.signUp({
      email,
      password,
      options: { data: { display_name: displayName } },
    });
    if (error || !data.session) {
      throw new Error(error?.message ?? "Sign up failed");
    }
    return toSession(data.session as any);
  },

  async signIn(email: string, password: string): Promise<Session> {
    const { data, error } = await supabase.auth.signInWithPassword({ email, password });
    if (error || !data.session) {
      throw new Error(error?.message ?? "Sign in failed");
    }
    return toSession(data.session as any);
  },

  async signOut(): Promise<void> {
    await supabase.auth.signOut();
  },

  async resetPassword(email: string): Promise<void> {
    const { error } = await supabase.auth.resetPasswordForEmail(email, {
      redirectTo: `${env.supabaseUrl}/auth/reset`,
    });
    if (error) throw new Error(error.message);
  },

  async getSession(): Promise<Session | null> {
    const { data } = await supabase.auth.getSession();
    return data.session ? toSession(data.session as any) : null;
  },

  onAuthStateChange(cb: (session: Session | null) => void) {
    return supabase.auth.onAuthStateChange((_event, session) => {
      cb(session ? toSession(session as any) : null);
    });
  },
};
