import React from "react";
import { Text, View, type StyleProp, type ViewStyle } from "react-native";
import { cn } from "@/lib/cn";
import { useTheme } from "@/theme/ThemeProvider";

export type BadgeTone = "success" | "danger" | "warning" | "info" | "neutral";

export interface BadgeProps {
  children: React.ReactNode;
  tone?: BadgeTone;
  icon?: React.ReactNode;
  style?: StyleProp<ViewStyle>;
  className?: string;
}

export const Badge: React.FC<BadgeProps> = ({
  children,
  tone = "neutral",
  icon,
  style,
  className,
}) => {
  const { colors } = useTheme();

  const resolved = ((): { backgroundColor: string; color: string } => {
    switch (tone) {
      case "success":
        return { backgroundColor: colors.successSoft, color: colors.success };
      case "danger":
        return { backgroundColor: colors.dangerSoft, color: colors.danger };
      case "warning":
        return { backgroundColor: colors.warning, color: colors.textInverse };
      case "info":
        return { backgroundColor: colors.primarySoft, color: colors.primary };
      case "neutral":
      default:
        return { backgroundColor: colors.surfaceAlt, color: colors.textMuted };
    }
  })();

  return (
    <View
      className={cn("flex-row items-center self-start gap-1 rounded-full px-2 py-1", className)}
      style={[resolved, style]}
    >
      {icon ? <View>{icon}</View> : null}
      <Text className="text-xs font-semibold" style={{ color: resolved.color }}>
        {children}
      </Text>
    </View>
  );
};
