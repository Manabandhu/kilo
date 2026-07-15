import React from "react";
import {
  Pressable,
  ScrollView,
  Text,
  View,
  type ScrollViewProps,
  type StyleProp,
  type ViewStyle,
} from "react-native";
import { ChevronRight } from "lucide-react-native";
import { cn } from "@/lib/cn";
import { useTheme } from "@/theme/ThemeProvider";

export interface ListProps extends Omit<ScrollViewProps, "style"> {
  children: React.ReactNode;
  style?: StyleProp<ViewStyle>;
}

export const List: React.FC<ListProps> = ({ children, className, style, ...rest }) => {
  return (
    <ScrollView
      className={cn("w-full", className)}
      style={style}
      keyboardShouldPersistTaps="handled"
      {...rest}
    >
      {children}
    </ScrollView>
  );
};

export interface ListItemProps {
  title: string;
  description?: string;
  left?: React.ReactNode;
  right?: React.ReactNode;
  onPress?: () => void;
  chevron?: boolean;
  divider?: boolean;
  accessibilityLabel?: string;
  style?: StyleProp<ViewStyle>;
}

export const ListItem: React.FC<ListItemProps> = ({
  title,
  description,
  left,
  right,
  onPress,
  chevron = false,
  divider = true,
  accessibilityLabel,
  style,
}) => {
  const { colors } = useTheme();
  const interactive = Boolean(onPress);

  const content = (
    <View
      className={cn("flex-row items-center gap-3 px-4 py-3", divider && "border-b")}
      style={{
        minHeight: 56,
        borderColor: colors.border,
        backgroundColor: colors.surface,
      }}
    >
      {left ? <View className="items-center justify-center">{left}</View> : null}
      <View className="flex-1">
        <Text className="text-base font-medium" style={{ color: colors.text }}>
          {title}
        </Text>
        {description ? (
          <Text className="mt-0.5 text-sm" style={{ color: colors.textMuted }} numberOfLines={2}>
            {description}
          </Text>
        ) : null}
      </View>
      {right ? <View className="items-center justify-center">{right}</View> : null}
      {chevron ? <ChevronRight size={18} color={colors.textMuted} /> : null}
    </View>
  );

  if (!interactive) return <View style={style}>{content}</View>;

  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel={accessibilityLabel ?? title}
      onPress={onPress}
      style={({ pressed }) => [{ opacity: pressed ? 0.7 : 1 }, style]}
    >
      {content}
    </Pressable>
  );
};
