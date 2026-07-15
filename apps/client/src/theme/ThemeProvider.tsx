import React, { createContext, useContext, useEffect, useState } from "react";
import { Appearance, View } from "react-native";
import * as SecureStore from "expo-secure-store";
import { tokens, type ColorScheme, type ThemeMode, type ThemeContextValue } from "./tokens";

const THEME_KEY = "manabandhu.theme.mode";

const ThemeContext = createContext<ThemeContextValue | null>(null);

const getSystemMode = (): ThemeMode => {
  const scheme = Appearance.getColorScheme();
  return scheme === "dark" ? "dark" : "light";
};

const getInitialMode = async (): Promise<ThemeMode> => {
  try {
    const stored = await SecureStore.getItemAsync(THEME_KEY);
    if (stored === "light" || stored === "dark") return stored;
  } catch {
    // ignore storage errors
  }
  return getSystemMode();
};

export const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [mode, setModeState] = useState<ThemeMode>("light");
  const [ready, setReady] = useState(false);

  useEffect(() => {
    let cancelled = false;
    getInitialMode().then((m) => {
      if (!cancelled) {
        setModeState(m);
        setReady(true);
      }
    });
    const subscription = Appearance.addChangeListener(({ colorScheme }) => {
      const stored = mode; // may be stale, but we'll sync on setMode
    });
    return () => {
      cancelled = true;
      subscription.remove();
    };
  }, []);

  const setMode = async (newMode: ThemeMode) => {
    setModeState(newMode);
    try {
      await SecureStore.setItemAsync(THEME_KEY, newMode);
    } catch {
      // ignore
    }
  };

  const toggleTheme = () => setMode(mode === "light" ? "dark" : "light");

  const isDark = mode === "dark";
  const colors = isDark ? tokens.colors.dark : tokens.colors.light;

  const value: ThemeContextValue = {
    mode,
    colors,
    isDark,
    setMode,
    toggleTheme,
  };

  if (!ready) {
    return null;
  }

  return (
    <ThemeContext.Provider value={value}>
      <View className={`flex-1 ${isDark ? "dark" : ""} bg-background`} style={{ flex: 1 }}>
        {children}
      </View>
    </ThemeContext.Provider>
  );
};

export const useTheme = (): ThemeContextValue => {
  const ctx = useContext(ThemeContext);
  if (!ctx) {
    throw new Error("useTheme must be used within a ThemeProvider");
  }
  return ctx;
};
