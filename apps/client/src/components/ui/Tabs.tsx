import React from "react";
import { Pressable, Text, View, type StyleProp, type ViewStyle } from "react-native";
import { cn } from "@/lib/cn";
import { useTheme } from "@/theme/ThemeProvider";

export interface TabItem {
  key: string;
  label: string;
  icon?: React.ReactNode;
}

export interface TabsProps {
  tabs: TabItem[];
  activeKey: string;
  onChange: (key: string) => void;
  variant?: "segmented" | "underline";
  style?: StyleProp<ViewStyle>;
  className?: string;
}

export const Tabs: React.FC<TabsProps> = ({
  tabs,
  activeKey,
  onChange,
  variant = "segmented",
  style,
  className,
}) => {
  const { colors } = useTheme();

  if (variant === "underline") {
    return (
      <View className={cn("flex-row border-b", className)} style={[{ borderColor: colors.border }, style]}>
        {tabs.map((tab) => {
          const active = tab.key === activeKey;
          return (
            <Pressable
              key={tab.key}
              accessibilityRole="tab"
              accessibilityState={{ selected: active }}
              accessibilityLabel={tab.label}
              onPress={() => onChange(tab.key)}
              className="flex-1 items-center px-3 py-3"
            >
              <View className="flex-row items-center gap-2">
                {tab.icon ? <View>{tab.icon}</View> : null}
                <Text
                  className="text-sm font-semibold"
                  style={{ color: active ? colors.primary : colors.textMuted }}
                >
                  {tab.label}
                </Text>
              </View>
              {active ? (
                <View
                  className="absolute bottom-0 h-0.5 w-full rounded-full"
                  style={{ backgroundColor: colors.primary }}
                />
              ) : null}
            </Pressable>
          );
        })}
      </View>
    );
  }

  return (
    <View
      className={cn("flex-row rounded-lg border p-1", className)}
      style={[{ borderColor: colors.border, backgroundColor: colors.surfaceAlt }, style]}
    >
      {tabs.map((tab) => {
        const active = tab.key === activeKey;
        return (
          <Pressable
            key={tab.key}
            accessibilityRole="tab"
            accessibilityState={{ selected: active }}
            accessibilityLabel={tab.label}
            onPress={() => onChange(tab.key)}
            className="flex-1 flex-row items-center justify-center gap-2 rounded-md px-3 py-2"
            style={[
              { minHeight: 40, backgroundColor: active ? colors.surface : "transparent" },
              active ? { shadowColor: "#000", shadowOpacity: 0.05, shadowRadius: 2 } : null,
            ]}
          >
            {tab.icon ? <View>{tab.icon}</View> : null}
            <Text
              className="text-sm font-semibold"
              style={{ color: active ? colors.primary : colors.textMuted }}
            >
              {tab.label}
            </Text>
          </Pressable>
        );
      })}
    </View>
  );
};
