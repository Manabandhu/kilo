import React from "react";
import {
  Pressable,
  ActivityIndicator,
  type PressableProps,
  type StyleProp,
  type ViewStyle,
} from "react-native";
import { cn } from "@/lib/cn";
import { useTheme } from "@/theme/ThemeProvider";

export type IconButtonVariant = "primary" | "secondary" | "ghost" | "danger";
export type IconButtonSize = "sm" | "md" | "lg";

export interface IconButtonProps extends Omit<PressableProps, "style" | "disabled"> {
  /** Icon element (e.g. <Search />). Should accept color/size via props. */
  icon: React.ReactNode;
  accessibilityLabel: string;
  variant?: IconButtonVariant;
  size?: IconButtonSize;
  loading?: boolean;
  disabled?: boolean;
  style?: StyleProp<ViewStyle>;
}

const sizeMap: Record<IconButtonSize, number> = { sm: 44, md: 48, lg: 56 };

function variantStyle(
  variant: IconButtonVariant,
  colors: ReturnType<typeof useTheme>["colors"],
): { backgroundColor: string; color: string; borderColor?: string; borderWidth?: number } {
  switch (variant) {
    case "secondary":
      return { backgroundColor: colors.surfaceAlt, color: colors.text };
    case "ghost":
      return { backgroundColor: "transparent", color: colors.text };
    case "danger":
      return { backgroundColor: colors.dangerSoft, color: colors.danger };
    case "primary":
    default:
      return { backgroundColor: colors.primarySoft, color: colors.primary };
  }
}

export const IconButton: React.FC<IconButtonProps> = ({
  icon,
  accessibilityLabel,
  variant = "ghost",
  size = "md",
  loading = false,
  disabled = false,
  style,
  className,
  ...rest
}) => {
  const { colors } = useTheme();
  const vs = variantStyle(variant, colors);
  const dim = sizeMap[size];
  const isDisabled = disabled || loading;

  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel={accessibilityLabel}
      accessibilityState={{ disabled: isDisabled, busy: loading }}
      disabled={isDisabled}
      className={cn("items-center justify-center rounded-full", className)}
      style={({ pressed }) => [
        vs,
        {
          width: dim,
          height: dim,
          minWidth: 44,
          minHeight: 44,
          opacity: isDisabled ? 0.5 : pressed ? 0.8 : 1,
        },
        style as ViewStyle,
      ]}
      {...rest}
    >
      {loading ? <ActivityIndicator color={vs.color} /> : icon}
    </Pressable>
  );
};
