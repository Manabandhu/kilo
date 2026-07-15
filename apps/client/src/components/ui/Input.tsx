import React from "react";
import { TextInput, View, Text } from "react-native";
import { cn } from "../../lib/cn";

interface InputProps {
  label?: string;
  value: string;
  onChangeText: (v: string) => void;
  placeholder?: string;
  error?: string;
  secureTextEntry?: boolean;
  keyboardType?: "default" | "email-address" | "numeric" | "phone-pad";
  autoCapitalize?: "none" | "sentences" | "words" | "characters";
  multiline?: boolean;
  numberOfLines?: number;
  accessibilityLabel?: string;
}

export function Input({
  label,
  value,
  onChangeText,
  placeholder,
  error,
  secureTextEntry,
  keyboardType = "default",
  autoCapitalize = "none",
  multiline,
  numberOfLines,
  accessibilityLabel,
}: InputProps) {
  return (
    <View className="w-full">
      {label ? (
        <Text className="mb-1 text-sm font-medium text-text">{label}</Text>
      ) : null}
      <TextInput
        value={value}
        onChangeText={onChangeText}
        placeholder={placeholder}
        placeholderTextColor="#94a3b8"
        secureTextEntry={secureTextEntry}
        keyboardType={keyboardType}
        autoCapitalize={autoCapitalize}
        multiline={multiline}
        numberOfLines={numberOfLines}
        accessibilityLabel={accessibilityLabel ?? label}
        accessibilityRole="text"
        className={cn(
          "w-full rounded-md border bg-surface px-3 py-2.5 text-base text-text",
          multiline ? "min-h-[80px]" : "min-h-[44px]",
          error ? "border-danger" : "border-border",
        )}
      />
      {error ? (
        <Text className="mt-1 text-xs text-danger" accessibilityRole="alert">
          {error}
        </Text>
      ) : null}
    </View>
  );
}
