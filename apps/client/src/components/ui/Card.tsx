import React from "react";
import { View } from "react-native";
import { cn } from "../../lib/cn";

export function Card({ children, className, onPress }: { children: React.ReactNode; className?: string; onPress?: () => void }) {
  const Wrapper = onPress ? View : View;
  return (
    <Wrapper
      className={cn("rounded-lg border border-border bg-surface p-4", className)}
      {...(onPress ? { onTouchEnd: onPress, accessibilityRole: "button" as const } : {})}
    >
      {children}
    </Wrapper>
  );
}
