import tokens from "@manabandhu/design-tokens";

export type ColorScheme = "light" | "dark" | "system";

export type ThemeMode = Exclude<ColorScheme, "system">;

export interface ThemeContextValue {
  mode: ThemeMode;
  colors: typeof tokens.colors.light;
  isDark: boolean;
  setMode: (mode: ThemeMode) => void;
  toggleTheme: () => void;
}

export const lightColors = tokens.colors.light;
export const darkColors = tokens.colors.dark;
export const spacing = tokens.spacing;
export const radii = tokens.radii;
export const typography = tokens.typography;
export const z = tokens.z;
