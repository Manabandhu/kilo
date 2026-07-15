import React from "react";
import {
  Pressable,
  ActivityIndicator,
  Text,
  type PressableProps,
  type StyleProp,
  type ViewStyle,
  type TextStyle,
} from "react-native";
import { cva, type VariantProps } from "class-variance-authority";
import { cn } from "@/lib/cn";
import { useTheme } from "@/theme/ThemeProvider";

const buttonVariants = cva("flex-row items-center justify-center rounded-lg font-medium", {
  variants: {
    variant: {
      primary: "",
      secondary: "",
      ghost: "",
      danger: "",
      outline: "",
    },
    size: {
      sm: "px-3 gap-2",
      md: "px-4 gap-2",
      lg: "px-5 gap-3",
    },
    fullWidth: {
      true: "w-full",
      false: "",
    },
  },
  defaultVariants: {
    variant: "primary",
    size: "md",
    fullWidth: false,
  },
});

type ButtonVariant = NonNullable<VariantProps<typeof buttonVariants>["variant"]>;
type ThemeColors = ReturnType<typeof useTheme>["colors"];

interface VariantStyle {
  backgroundColor: string;
  color: string;
  borderColor?: string;
  borderWidth?: number;
}

function variantStyle(variant: ButtonVariant, colors: ThemeColors): VariantStyle {
  switch (variant) {
    case "secondary":
      return { backgroundColor: colors.surfaceAlt, color: colors.text };
    case "ghost":
      return { backgroundColor: "transparent", color: colors.primary };
    case "danger":
      return { backgroundColor: colors.danger, color: colors.textInverse };
    case "outline":
      return {
        backgroundColor: "transparent",
        color: colors.primary,
        borderColor: colors.primary,
        borderWidth: 1,
      };
    case "primary":
    default:
      return { backgroundColor: colors.primary, color: colors.textInverse };
  }
}

export interface ButtonProps
  extends Omit<PressableProps, "style" | "disabled">,
    VariantProps<typeof buttonVariants> {
  title: string;
  loading?: boolean;
  disabled?: boolean;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
  style?: StyleProp<ViewStyle>;
  textStyle?: StyleProp<TextStyle>;
  accessibilityLabel?: string;
}

export const Button: React.FC<ButtonProps> = ({
  title,
  variant = "primary",
  size = "md",
  fullWidth = false,
  loading = false,
  disabled = false,
  leftIcon,
  rightIcon,
  style,
  textStyle,
  className,
  accessibilityLabel,
  ...rest
}) => {
  const { colors } = useTheme();
  const vs = variantStyle(variant as ButtonVariant, colors);
  const isDisabled = disabled || loading;

  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel={accessibilityLabel ?? title}
      accessibilityState={{ disabled: isDisabled, busy: loading }}
      disabled={isDisabled}
      className={cn(buttonVariants({ variant, size, fullWidth }), className)}
      style={({ pressed }) => [
        vs,
        { minHeight: 44, opacity: isDisabled ? 0.5 : pressed ? 0.85 : 1 },
        style as ViewStyle,
      ]}
      {...rest}
    >
      {loading ? (
        <ActivityIndicator color={vs.color} />
      ) : (
        leftIcon
      )}
      <Text style={[{ color: vs.color, fontWeight: "600" }, textStyle]}>{title}</Text>
      {!loading && rightIcon}
    </Pressable>
  );
};
