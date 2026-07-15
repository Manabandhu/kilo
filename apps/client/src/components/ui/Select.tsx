import React from "react";
import {
  Picker,
  Text,
  View,
  type StyleProp,
  type ViewStyle,
} from "react-native";
import { cn } from "@/lib/cn";
import { useTheme } from "@/theme/ThemeProvider";

export interface SelectOption<T extends string = string> {
  label: string;
  value: T;
  disabled?: boolean;
}

export interface SelectProps<T extends string = string> {
  label?: string;
  error?: string;
  helper?: string;
  options: SelectOption<T>[];
  selectedValue: T | undefined;
  onValueChange: (value: T) => void;
  placeholder?: string;
  disabled?: boolean;
  accessibilityLabel?: string;
  containerStyle?: StyleProp<ViewStyle>;
  className?: string;
}

export function Select<T extends string = string>({
  label,
  error,
  helper,
  options,
  selectedValue,
  onValueChange,
  placeholder,
  disabled = false,
  accessibilityLabel,
  containerStyle,
  className,
}: SelectProps<T>): React.ReactElement {
  const { colors } = useTheme();
  const hasError = Boolean(error);
  const borderColor = hasError ? colors.danger : colors.border;
  const labelId = label ?? accessibilityLabel;

  return (
    <View style={containerStyle}>
      {label ? (
        <Text className="mb-1 text-sm font-medium" style={{ color: colors.text }}>
          {label}
        </Text>
      ) : null}
      <View
        className={cn(
          "flex-row items-center rounded-lg border bg-surface px-2",
          className,
        )}
        style={{ minHeight: 44, borderColor, backgroundColor: colors.surface, opacity: disabled ? 0.5 : 1 }}
      >
        <Picker
          style={{ flex: 1, color: colors.text, minHeight: 44 }}
          selectedValue={selectedValue}
          onValueChange={(value) => onValueChange(value as T)}
          enabled={!disabled}
          accessibilityLabel={labelId}
          dropdownIconColor={colors.textMuted}
        >
          {placeholder ? (
            <Picker.Item label={placeholder} value={undefined as unknown as T} />
          ) : null}
          {options.map((option) => (
            <Picker.Item
              key={option.value}
              label={option.label}
              value={option.value}
              enabled={!option.disabled}
              color={colors.text}
            />
          ))}
        </Picker>
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
}
