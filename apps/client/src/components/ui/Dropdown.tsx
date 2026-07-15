import React, { useState } from "react";
import {
  Modal,
  Pressable,
  Text,
  View,
  type StyleProp,
  type ViewStyle,
} from "react-native";
import { ChevronDown } from "lucide-react-native";
import { cn } from "@/lib/cn";
import { useTheme } from "@/theme/ThemeProvider";

export interface DropdownItem {
  label: string;
  icon?: React.ReactNode;
  onPress: () => void;
  danger?: boolean;
  disabled?: boolean;
}

export interface DropdownProps {
  triggerLabel?: string;
  trigger?: React.ReactNode;
  items: DropdownItem[];
  accessibilityLabel?: string;
  style?: StyleProp<ViewStyle>;
  className?: string;
}

/** A custom menu dropdown (action menu), distinct from the native Select. */
export const Dropdown: React.FC<DropdownProps> = ({
  triggerLabel,
  trigger,
  items,
  accessibilityLabel = "Open menu",
  style,
  className,
}) => {
  const { colors } = useTheme();
  const [open, setOpen] = useState(false);

  return (
    <>
      <Pressable
        accessibilityRole="button"
        accessibilityLabel={accessibilityLabel}
        accessibilityState={{ expanded: open }}
        onPress={() => setOpen(true)}
        style={({ pressed }) => ({ opacity: pressed ? 0.8 : 1 })}
        className={cn("flex-row items-center", className)}
      >
        {trigger ?? (
          <View
            className="flex-row items-center gap-1 rounded-lg border px-3"
            style={{ minHeight: 44, borderColor: colors.border, backgroundColor: colors.surface }}
          >
            <Text className="text-sm font-medium" style={{ color: colors.text }}>
              {triggerLabel ?? "Menu"}
            </Text>
            <ChevronDown size={18} color={colors.textMuted} />
          </View>
        )}
      </Pressable>

      <Modal visible={open} transparent animationType="fade" onRequestClose={() => setOpen(false)}>
        <Pressable
          className="flex-1"
          style={{ backgroundColor: "rgba(0,0,0,0.4)" }}
          accessibilityRole="button"
          accessibilityLabel="Close menu"
          onPress={() => setOpen(false)}
        >
          <View className="flex-1 justify-center px-8">
            <View
              className="w-full rounded-xl border p-2"
              style={{ backgroundColor: colors.surface, borderColor: colors.border }}
              accessibilityRole="menu"
            >
              {items.map((item) => (
                <Pressable
                  key={item.label}
                  accessibilityRole="menuitem"
                  accessibilityLabel={item.label}
                  disabled={item.disabled}
                  onPress={() => {
                    if (item.disabled) return;
                    setOpen(false);
                    item.onPress();
                  }}
                  className="flex-row items-center gap-3 rounded-lg px-3 py-3"
                  style={({ pressed }) => ({ opacity: item.disabled ? 0.4 : pressed ? 0.8 : 1 })}
                >
                  {item.icon ? <View>{item.icon}</View> : null}
                  <Text
                    className="text-sm font-medium"
                    style={{ color: item.danger ? colors.danger : colors.text }}
                  >
                    {item.label}
                  </Text>
                </Pressable>
              ))}
            </View>
          </View>
        </Pressable>
      </Modal>
    </>
  );
};
