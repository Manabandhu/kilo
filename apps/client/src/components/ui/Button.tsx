import React from "react";
import { Pressable, ActivityIndicator, View, Text } from "react-native";
import { cn } from "../../lib/cn";

type Variant = "primary" | "secondary" | "ghost" | "danger" | "outline";
type Size = "sm" | "md" | "lg";

interface ButtonProps {
  variant?: Variant;
  size?: Size;
  loading?: boolean;
  disabled?: boolean;
  fullWidth?: boolean;
  onPress?: () => void;
  children: React.ReactNode;
  accessibilityLabel?: string;
  className?: string;
}

const base = "flex-row items-center justify-center rounded-md font-semibold";
const variants: Record<Variant, string> = {
  primary: "bg-primary",
  secondary: "bg-surface-alt",
  ghost: "bg-transparent",
  danger: "bg-danger",
  outline: "border border-border bg-transparent",
};
const sizes: Record<Size, string> = {
  sm: "px-3 py-1.5 text-sm min-h-[36px]",
  md: "px-4 py-2.5 text-base min-h-[44px]",
  lg: "px-5 py-3 text-lg min-h-[52px]",
};
const textColor: Record<Variant, string> = {
  primary: "text-text-inverse",
  secondary: "text-text",
  ghost: "text-primary",
  danger: "text-text-inverse",
  outline: "text-text",
};

export function Button({
  variant = "primary",
  size = "md",
  loading,
  disabled,
  fullWidth,
  onPress,
  children,
  accessibilityLabel,
  className,
}: ButtonProps) {
  const isDisabled = disabled || loading;
  return (
    <Pressable
      onPress={isDisabled ? undefined : onPress}
      disabled={isDisabled}
      accessibilityRole="button"
      accessibilityLabel={accessibilityLabel}
      accessibilityState={{ disabled: isDisabled, busy: loading }}
      className={cn(
        base,
        variants[variant],
        sizes[size],
        textColor[variant],
        fullWidth && "w-full",
        isDisabled && "opacity-50",
        className,
      )}
    >
      {loading ? <ActivityIndicator color={variant === "primary" || variant === "danger" ? "#fff" : "#000"} /> : null}
      {!loading && <Text className={cn(textColor[variant], "font-semibold")}>{children}</Text>}
    </Pressable>
  );
}
