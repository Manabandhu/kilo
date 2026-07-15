import React from "react";
import { View, TextInput } from "react-native";
import { cn } from "../../lib/cn";

export function SearchBar({
  value,
  onChangeText,
  placeholder = "Search",
  accessibilityLabel = "Search",
}: {
  value: string;
  onChangeText: (v: string) => void;
  placeholder?: string;
  accessibilityLabel?: string;
}) {
  return (
    <View className="w-full rounded-md border border-border bg-surface px-3 py-2">
      <TextInput
        value={value}
        onChangeText={onChangeText}
        placeholder={placeholder}
        placeholderTextColor="#94a3b8"
        accessibilityLabel={accessibilityLabel}
        accessibilityRole="search"
        className="text-base text-text"
      />
    </View>
  );
}
