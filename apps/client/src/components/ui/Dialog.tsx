import React from "react";
import {
  Modal,
  Pressable,
  Text,
  View,
  type StyleProp,
  type ViewStyle,
} from "react-native";
import { cn } from "@/lib/cn";
import { useTheme } from "@/theme/ThemeProvider";

export interface DialogProps {
  visible: boolean;
  onClose: () => void;
  title?: string;
  description?: string;
  children?: React.ReactNode;
  footer?: React.ReactNode;
  dismissable?: boolean;
  style?: StyleProp<ViewStyle>;
  className?: string;
}

/** Accessible modal dialog with a focus-trapped, labelled surface. */
export const Dialog: React.FC<DialogProps> = ({
  visible,
  onClose,
  title,
  description,
  children,
  footer,
  dismissable = true,
  style,
  className,
}) => {
  const { colors } = useTheme();

  return (
    <Modal
      visible={visible}
      transparent
      animationType="fade"
      onRequestClose={dismissable ? onClose : undefined}
      statusBarTranslucent
    >
      <Pressable
        className="flex-1 items-center justify-center px-6"
        style={{ backgroundColor: "rgba(0,0,0,0.5)" }}
        accessibilityRole="button"
        accessibilityLabel="Close dialog"
        disabled={!dismissable}
        onPress={dismissable ? onClose : undefined}
      >
        <Pressable
          className={cn("w-full max-w-md rounded-xl border p-5", className)}
          style={[
            { backgroundColor: colors.surface, borderColor: colors.border },
            style,
          ]}
          accessibilityViewIsModal
          accessibilityRole="dialog"
          accessibilityLabel={title}
        >
          {title ? (
            <Text className="text-lg font-bold" style={{ color: colors.text }}>
              {title}
            </Text>
          ) : null}
          {description ? (
            <Text className="mt-1 text-sm" style={{ color: colors.textMuted }}>
              {description}
            </Text>
          ) : null}
          {children ? <View className="mt-3">{children}</View> : null}
          {footer ? <View className="mt-4 flex-row justify-end gap-2">{footer}</View> : null}
        </Pressable>
      </Pressable>
    </Modal>
  );
};
