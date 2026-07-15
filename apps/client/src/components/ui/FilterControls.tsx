import React from "react";
import { ScrollView, Text, View } from "react-native";
import { useTheme } from "@/theme/ThemeProvider";
import { Button } from "./Button";

export interface FilterControlsProps {
  title?: string;
  children: React.ReactNode;
  onReset?: () => void;
  resetLabel?: string;
  /** Lay children out in a horizontal scroll (default) or a vertical stack. */
  layout?: "row" | "column";
}

/** A consistent container for filter UI (chips, selects, toggles). */
export const FilterControls: React.FC<FilterControlsProps> = ({
  title,
  children,
  onReset,
  resetLabel = "Reset",
  layout = "row",
}) => {
  const { colors } = useTheme();

  const body =
    layout === "row" ? (
      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerClassName="gap-2 py-1"
        keyboardShouldPersistTaps="handled"
      >
        {children}
      </ScrollView>
    ) : (
      <View className="gap-2 py-1">{children}</View>
    );

  return (
    <View className="border-b px-4 py-2" style={{ borderColor: colors.border }}>
      {title ? (
        <Text className="mb-1 text-xs font-semibold uppercase" style={{ color: colors.textMuted }}>
          {title}
        </Text>
      ) : null}
      <View className="flex-row items-center gap-2">
        <View className="flex-1">{body}</View>
        {onReset ? (
          <Button
            title={resetLabel}
            variant="ghost"
            size="sm"
            onPress={onReset}
            accessibilityLabel={resetLabel}
          />
        ) : null}
      </View>
    </View>
  );
};
