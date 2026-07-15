import React from "react";
import {
  TextInput,
  Text,
  View,
  type TextInputProps,
  type StyleProp,
  type ViewStyle,
} from "react-native";
import { cn } from "@/lib/cn";
import { useTheme } from "@/theme/ThemeProvider";

export interface InputProps extends Omit<TextInputProps, "style"> {
  label?: string;
  error?: string;
  helper?: string;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
  containerStyle?: StyleProp<ViewStyle>;
  inputStyle?: StyleProp<TextInputProps["style"]>;
}

export const Input: React.FC<InputProps> = ({
  label,
  error,
  helper,
  leftIcon,
  rightIcon,
  containerStyle,
  inputStyle,
  className,
  placeholderTextColor,
  accessibilityLabel,
  ...rest
}) => {
  const { colors, isDark } = useTheme();
  const hasError = Boolean(error);
  const borderColor = hasError ? colors.danger : colors.border;

  return (
    <View style={containerStyle}>
      {label ? (
        <Text
          className="mb-1 text-sm font-medium"
          style={{ color: colors.text }}
          nativeID={label}
        >
          {label}
        </Text>
      ) : null}
      <View
        className={cn(
          "flex-row items-center rounded-lg border bg-surface px-3",
          className,
        )}
        style={{
          minHeight: 44,
          borderColor,
          backgroundColor: colors.surface,
        }}
      >
        {leftIcon ? <View className="mr-2">{leftIcon}</View> : null}
        <TextInput
          className="flex-1 py-2 text-base"
          style={[{ color: colors.text }, inputStyle as StyleProp<TextInputProps["style"]>]}
          placeholderTextColor={placeholderTextColor ?? colors.textMuted}
          accessibilityLabel={accessibilityLabel ?? label}
          accessibilityHint={helper}
          accessibilityInvalid={hasError}
          {...rest}
        />
        {rightIcon ? <View className="ml-2">{rightIcon}</View> : null}
      </View>
      {hasError ? (
        <Text className="mt-1 text-xs" style={{ color: colors.danger }}>
          {error}
        </Text>
      ) : helper ? (
        <Text className="mt-1 text-xs" style={{ color: colors.textMuted }}>
          {helper}
        </Text>
      ) : null}
    </View>
  );
};
