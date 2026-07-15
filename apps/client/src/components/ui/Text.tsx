import React from "react";
import { Text as RNText } from "react-native";
import { cn } from "../../lib/cn";

type Weight = "regular" | "medium" | "semibold" | "bold";
type Size = "xs" | "sm" | "base" | "lg" | "xl" | "2xl" | "3xl";

const weights: Record<Weight, string> = {
  regular: "font-normal",
  medium: "font-medium",
  semibold: "font-semibold",
  bold: "font-bold",
};

const sizes: Record<Size, string> = {
  xs: "text-xs",
  sm: "text-sm",
  base: "text-base",
  lg: "text-lg",
  xl: "text-xl",
  "2xl": "text-2xl",
  "3xl": "text-3xl",
};

export function Text({
  children,
  size = "base",
  weight = "regular",
  className,
  color = "text-text",
  ...rest
}: {
  children: React.ReactNode;
  size?: Size;
  weight?: Weight;
  className?: string;
  color?: string;
} & React.ComponentProps<typeof RNText>) {
  return (
    <RNText className={cn(sizes[size], weights[weight], color, className)} {...rest}>
      {children}
    </RNText>
  );
}
