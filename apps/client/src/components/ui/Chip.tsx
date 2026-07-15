import React from "react";
import {
  Pressable,
  Text,
  View,
  type StyleProp,
  type ViewStyle,
} from "react-native";
import { cn } from "@/lib/cn";
import { useTheme } from "@/theme/ThemeProvider";

export interface ChipProps {
  label: string;
  selected?: boolean;
  onPress?: () => void;
  icon?: React.ReactNode;
  disabled?: boolean;
  accessibilityLabel?: string;
  style?: StyleProp<ViewStyle>;
  className?: string;
}

export const Chip: React.FC<ChipProps> = ({
  label,
  selected = false,
  onPress,
  icon,
  disabled = false,
  accessibilityLabel,
  style,
  className,
}) => {
  const { colors } = useTheme();
  const interactive = Boolean(onPress);

  const base: ViewStyle = {
    minHeight: 44,
    paddingHorizontal: 14,
    borderRadius: 9999,
    borderWidth: 1,
    borderColor: selected ? colors.primary : colors.border,
    backgroundColor: selected ? colors.primarySoft : colors.surface,
    opacity: disabled ? 0.5 : 1,
  };

  const content = (
    <View className={cn("flex-row items-center gap-2", className)}>
      {icon ? <View>{icon}</View> : null}
      <Text
        className="text-sm font-medium"
        style={{ color: selected ? colors.primary : colors.text }}
      >
        {label}
      </Text>
    </View>
  );

  if (!interactive) {
    return (
      <View className="items-center justify-center" style={[base, style]}>
        {content}
      </View>
    );
  }

  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel={accessibilityLabel ?? label}
      accessibilityState={{ selected, disabled }}
      disabled={disabled}
      onPress={onPress}
      className="items-center justify-center"
      style={({ pressed }) => [base, pressed && !disabled ? { opacity: 0.8 } : null, style]}
    >
      {content}
    </Pressable>
  );
};
