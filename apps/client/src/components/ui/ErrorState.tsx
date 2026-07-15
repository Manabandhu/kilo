import React from "react";
import { Text, View } from "react-native";
import { AlertTriangle } from "lucide-react-native";
import { useTheme } from "@/theme/ThemeProvider";
import { Button } from "./Button";

export interface ErrorStateProps {
  title?: string;
  message?: string;
  onRetry?: () => void;
  retryLabel?: string;
}

export const ErrorState: React.FC<ErrorStateProps> = ({
  title = "Something went wrong",
  message,
  onRetry,
  retryLabel = "Try again",
}) => {
  const { colors } = useTheme();
  return (
    <View className="items-center justify-center px-6 py-10">
      <View
        className="mb-4 items-center justify-center rounded-full p-4"
        style={{ backgroundColor: colors.dangerSoft }}
      >
        <AlertTriangle size={28} color={colors.danger} accessibilityLabel="Error" />
      </View>
      <Text className="text-lg font-semibold" style={{ color: colors.text }}>
        {title}
      </Text>
      {message ? (
        <Text className="mt-1 text-center text-sm" style={{ color: colors.textMuted }}>
          {message}
        </Text>
      ) : null}
      {onRetry ? (
        <View className="mt-4 w-full items-center">
          <Button
            title={retryLabel}
            variant="secondary"
            onPress={onRetry}
            accessibilityLabel={retryLabel}
          />
        </View>
      ) : null}
    </View>
  );
};
