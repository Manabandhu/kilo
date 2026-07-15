import React from "react";
import { Text, View } from "react-native";
import { useTheme } from "@/theme/ThemeProvider";

export interface EmptyStateProps {
  icon?: React.ReactNode;
  title: string;
  message?: string;
  action?: React.ReactNode;
}

export const EmptyState: React.FC<EmptyStateProps> = ({
  icon,
  title,
  message,
  action,
}) => {
  const { colors } = useTheme();
  return (
    <View className="items-center justify-center px-6 py-10">
      {icon ? (
        <View className="mb-4 items-center justify-center rounded-full p-4" style={{ backgroundColor: colors.surfaceAlt }}>
          {icon}
        </View>
      ) : null}
      <Text className="text-lg font-semibold" style={{ color: colors.text }}>
        {title}
      </Text>
      {message ? (
        <Text className="mt-1 text-center text-sm" style={{ color: colors.textMuted }}>
          {message}
        </Text>
      ) : null}
      {action ? <View className="mt-4 w-full items-center">{action}</View> : null}
    </View>
  );
};
