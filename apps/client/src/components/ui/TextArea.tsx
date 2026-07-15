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

export interface TextAreaProps extends Omit<TextInputProps, "style" | "multiline"> {
  label?: string;
  error?: string;
  helper?: string;
  containerStyle?: StyleProp<ViewStyle>;
  inputStyle?: StyleProp<TextInputProps["style"]>;
  maxLength?: number;
}

export const TextArea: React.FC<TextAreaProps> = ({
  label,
  error,
  helper,
  containerStyle,
  inputStyle,
  maxLength,
  value,
  className,
  placeholderTextColor,
  accessibilityLabel,
  ...rest
}) => {
  const { colors } = useTheme();
  const hasError = Boolean(error);
  const borderColor = hasError ? colors.danger : colors.border;

  return (
    <View style={containerStyle}>
      {label ? (
        <Text className="mb-1 text-sm font-medium" style={{ color: colors.text }}>
          {label}
        </Text>
      ) : null}
      <View
        className={cn("rounded-lg border bg-surface px-3 py-2", className)}
        style={{ borderColor, backgroundColor: colors.surface, minHeight: 88 }}
      >
        <TextInput
          multiline
          textAlignVertical="top"
          maxLength={maxLength}
          className="text-base"
          style={[{ color: colors.text, minHeight: 72 }, inputStyle as StyleProp<TextInputProps["style"]>]}
          placeholderTextColor={placeholderTextColor ?? colors.textMuted}
          accessibilityLabel={accessibilityLabel ?? label}
          accessibilityHint={helper}
          accessibilityInvalid={hasError}
          value={value}
          {...rest}
        />
      </View>
      <View className="mt-1 flex-row items-center justify-between">
        <View className="flex-1">
          {hasError ? (
            <Text className="text-xs" style={{ color: colors.danger }}>
              {error}
            </Text>
          ) : helper ? (
            <Text className="text-xs" style={{ color: colors.textMuted }}>
              {helper}
            </Text>
          ) : null}
        </View>
        {maxLength ? (
          <Text className="text-xs" style={{ color: colors.textMuted }}>
            {`${(value ?? "").length}/${maxLength}`}
          </Text>
        ) : null}
      </View>
    </View>
  );
};
