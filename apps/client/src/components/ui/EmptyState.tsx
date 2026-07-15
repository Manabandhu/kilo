import React from "react";
import { View, Text } from "react-native";
import { Button } from "./Button";

export function EmptyState({
  title,
  message,
  actionLabel,
  onAction,
  icon,
}: {
  title: string;
  message?: string;
  actionLabel?: string;
  onAction?: () => void;
  icon?: React.ReactNode;
}) {
  return (
    <View className="items-center justify-center px-6 py-12" accessibilityRole="summary">
      {icon ? <View className="mb-3">{icon}</View> : null}
      <Text className="text-lg font-semibold text-text">{title}</Text>
      {message ? <Text className="mt-1 text-center text-sm text-muted">{message}</Text> : null}
      {actionLabel && onAction ? (
        <Button variant="primary" size="sm" className="mt-4" onPress={onAction} accessibilityLabel={actionLabel}>
          {actionLabel}
        </Button>
      ) : null}
    </View>
  );
}
