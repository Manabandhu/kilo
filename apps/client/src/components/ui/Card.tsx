import React from "react";
import {
  Pressable,
  View,
  type StyleProp,
  type ViewStyle,
} from "react-native";
import { cn } from "@/lib/cn";
import { useTheme } from "@/theme/ThemeProvider";

export interface CardProps {
  children: React.ReactNode;
  onPress?: () => void;
  padded?: boolean;
  style?: StyleProp<ViewStyle>;
  className?: string;
  accessibilityLabel?: string;
}

export const Card: React.FC<CardProps> = ({
  children,
  onPress,
  padded = true,
  style,
  className,
  accessibilityLabel,
}) => {
  const { colors } = useTheme();

  const inner = (
    <View
      className={cn("rounded-xl border", padded && "p-4", className)}
      style={{ backgroundColor: colors.surface, borderColor: colors.border }}
    >
      {children}
    </View>
  );

  if (!onPress) {
    return (
      <View style={style}>
        {inner}
      </View>
    );
  }

  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel={accessibilityLabel}
      onPress={onPress}
      style={({ pressed }) => [{ opacity: pressed ? 0.9 : 1 }, style]}
    >
      {inner}
    </Pressable>
  );
};
