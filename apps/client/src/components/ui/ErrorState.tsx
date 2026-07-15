import React from "react";
import { View, Text } from "react-native";
import { Button } from "./Button";

export function ErrorState({
  message = "Something went wrong. Please try again.",
  onRetry,
}: {
  message?: string;
  onRetry?: () => void;
}) {
  return (
    <View className="items-center justify-center px-6 py-12" accessibilityRole="alert">
      <Text className="text-base font-semibold text-danger">Error</Text>
      <Text className="mt-1 text-center text-sm text-muted">{message}</Text>
      {onRetry ? (
        <Button variant="outline" size="sm" className="mt-4" onPress={onRetry} accessibilityLabel="Retry">
          Retry
        </Button>
      ) : null}
    </View>
  );
}
