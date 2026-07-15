import React from "react";
import { View, Text } from "react-native";
import { cn } from "../../lib/cn";

type Tone = "success" | "danger" | "warning" | "info" | "neutral";

const tones: Record<Tone, string> = {
  success: "bg-success-soft text-success",
  danger: "bg-danger-soft text-danger",
  warning: "bg-warning/20 text-warning",
  info: "bg-primary-soft text-primary",
  neutral: "bg-surface-alt text-muted",
};

export function Badge({ children, tone = "neutral", className }: { children: React.ReactNode; tone?: Tone; className?: string }) {
  return (
    <View className={cn("self-start rounded-full px-2.5 py-0.5", tones[tone], className)}>
      <Text className={cn("text-xs font-semibold", tones[tone].split(" ").slice(1))}>{children}</Text>
    </View>
  );
}
