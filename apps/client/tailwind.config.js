/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./app/**/*.{ts,tsx}", "./src/**/*.{ts,tsx}"],
  presets: [require("nativewind/preset")],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: "var(--color-primary)",
          hover: "var(--color-primary-hover)",
          soft: "var(--color-primary-soft)",
        },
        accent: {
          DEFAULT: "var(--color-accent)",
          hover: "var(--color-accent-hover)",
        },
        success: {
          DEFAULT: "var(--color-success)",
          soft: "var(--color-success-soft)",
        },
        danger: {
          DEFAULT: "var(--color-danger)",
          soft: "var(--color-danger-soft)",
        },
        warning: "var(--color-warning)",
        background: "var(--color-background)",
        surface: "var(--color-surface)",
        "surface-alt": "var(--color-surface-alt)",
        text: "var(--color-text)",
        muted: "var(--color-text-muted)",
        "text-inverse": "var(--color-text-inverse)",
        border: "var(--color-border)",
      },
      borderRadius: {
        sm: "var(--radius-sm)",
        md: "var(--radius-md)",
        lg: "var(--radius-lg)",
        xl: "var(--radius-xl)",
        full: "var(--radius-full)",
      },
      spacing: {
        xs: "var(--spacing-xs)",
        sm: "var(--spacing-sm)",
        md: "var(--spacing-md)",
        lg: "var(--spacing-lg)",
        xl: "var(--spacing-xl)",
        "2xl": "var(--spacing-2xl)",
        "3xl": "var(--spacing-3xl)",
      },
      fontSize: {
        xs: ["var(--font-xs)", { lineHeight: "var(--font-line-height-tight)" }],
        sm: ["var(--font-sm)", { lineHeight: "var(--font-line-height-normal)" }],
        base: ["var(--font-base)", { lineHeight: "var(--font-line-height-normal)" }],
        lg: ["var(--font-lg)", { lineHeight: "var(--font-line-height-normal)" }],
        xl: ["var(--font-xl)", { lineHeight: "var(--font-line-height-normal)" }],
        "2xl": ["var(--font-2xl)", { lineHeight: "var(--font-line-height-tight)" }],
        "3xl": ["var(--font-3xl)", { lineHeight: "var(--font-line-height-tight)" }],
        "4xl": ["var(--font-4xl)", { lineHeight: "var(--font-line-height-tight)" }],
      },
      fontFamily: {
        sans: ["var(--font-sans)", "System"],
      },
    },
  },
  plugins: [],
};
