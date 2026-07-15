// ManaBandhu design tokens.
// Brand: deep indigo primary, warm amber accent, green success, red error.
// Consumed by the NativeWind tailwind.config and the runtime theme provider.

export const palette = {
  indigo: {
    50: "#eef2ff",
    100: "#e0e7ff",
    200: "#c7d2fe",
    300: "#a5b4fc",
    400: "#818cf8",
    500: "#6366f1",
    600: "#4f46e5",
    700: "#4338ca",
    800: "#3730a3",
    900: "#312e81",
    950: "#1e1b4b",
  },
  amber: {
    50: "#fffbeb",
    100: "#fef3c7",
    200: "#fde68a",
    300: "#fcd34d",
    400: "#fbbf24",
    500: "#f59e0b",
    600: "#d97706",
    700: "#b45309",
    800: "#92400e",
    900: "#78350f",
  },
  green: {
    50: "#ecfdf5",
    100: "#d1fae5",
    500: "#10b981",
    600: "#059669",
    700: "#047857",
  },
  red: {
    50: "#fef2f2",
    100: "#fee2e2",
    500: "#ef4444",
    600: "#dc2626",
    700: "#b91c1c",
  },
  neutral: {
    0: "#ffffff",
    50: "#f8fafc",
    100: "#f1f5f9",
    200: "#e2e8f0",
    300: "#cbd5e1",
    400: "#94a3b8",
    500: "#64748b",
    600: "#475569",
    700: "#334155",
    800: "#1e293b",
    900: "#0f172a",
    950: "#020617",
  },
} as const;

export type ColorScheme = "light" | "dark";

export const colors = {
  light: {
    background: palette.neutral[50],
    surface: palette.neutral[0],
    surfaceAlt: palette.neutral[100],
    border: palette.neutral[200],
    text: palette.neutral[900],
    textMuted: palette.neutral[500],
    textInverse: palette.neutral[0],
    primary: palette.indigo[600],
    primaryHover: palette.indigo[700],
    primarySoft: palette.indigo[50],
    accent: palette.amber[500],
    accentHover: palette.amber[600],
    success: palette.green[600],
    successSoft: palette.green[50],
    danger: palette.red[600],
    dangerSoft: palette.red[50],
    warning: palette.amber[500],
  },
  dark: {
    background: palette.neutral[950],
    surface: palette.neutral[900],
    surfaceAlt: palette.neutral[800],
    border: palette.neutral[800],
    text: palette.neutral[50],
    textMuted: palette.neutral[400],
    textInverse: palette.neutral[950],
    primary: palette.indigo[400],
    primaryHover: palette.indigo[300],
    primarySoft: palette.indigo[950],
    accent: palette.amber[400],
    accentHover: palette.amber[300],
    success: palette.green[500],
    successSoft: palette.green[700],
    danger: palette.red[500],
    dangerSoft: palette.red[700],
    warning: palette.amber[400],
  },
} as const;

export const spacing = {
  xs: 4,
  sm: 8,
  md: 12,
  lg: 16,
  xl: 24,
  "2xl": 32,
  "3xl": 48,
} as const;

export const radii = {
  sm: 8,
  md: 12,
  lg: 16,
  xl: 24,
  full: 9999,
} as const;

export const typography = {
  fontSizes: {
    xs: 12,
    sm: 14,
    base: 16,
    lg: 18,
    xl: 22,
    "2xl": 28,
    "3xl": 34,
    "4xl": 42,
  },
  fontWeights: {
    regular: "400",
    medium: "500",
    semibold: "600",
    bold: "700",
  },
  lineHeights: {
    tight: 1.2,
    normal: 1.5,
    relaxed: 1.7,
  },
} as const;

export const z = {
  base: 0,
  dropdown: 1000,
  sticky: 1100,
  overlay: 1200,
  modal: 1300,
  toast: 1400,
} as const;

export const tokens = { palette, colors, spacing, radii, typography, z } as const;
export type Tokens = typeof tokens;

export default tokens;
