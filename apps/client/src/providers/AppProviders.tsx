import React, { useEffect, useState } from "react";
import { QueryClientProvider } from "@tanstack/react-query";
import { ThemeProvider } from "../theme/ThemeProvider";
import { queryClient } from "../lib/queryClient";
import { useAuthStore } from "../store/authStore";
import { authService } from "../services/auth.service";
import type { Session } from "@manabandhu/contracts";

function SessionProvider({ children }: { children: React.ReactNode }) {
  const setSession = useAuthStore((s) => s.setSession);
  const clear = useAuthStore((s) => s.clear);
  const [bootstrapped, setBootstrapped] = useState(false);

  useEffect(() => {
    let active = true;
    authService.getSession().then((s) => {
      if (!active) return;
      if (s) setSession(s);
      setBootstrapped(true);
    });
    const { data: sub } = authService.onAuthStateChange((session) => {
      if (session) setSession(session);
      else clear();
    });
    return () => {
      active = false;
      sub.subscription.unsubscribe();
    };
  }, [setSession, clear]);

  if (!bootstrapped) return null;
  return <>{children}</>;
}

export function AppProviders({ children }: { children: React.ReactNode }) {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider>
        <SessionProvider>{children}</SessionProvider>
      </ThemeProvider>
    </QueryClientProvider>
  );
}
