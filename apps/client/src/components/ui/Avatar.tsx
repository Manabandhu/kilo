import React, { useState } from "react";
import {
  Image,
  Text,
  View,
  type ImageStyle,
  type StyleProp,
} from "react-native";
import { cn } from "@/lib/cn";
import { useTheme } from "@/theme/ThemeProvider";

export type AvatarSize = "sm" | "md" | "lg" | "xl";

const sizeMap: Record<AvatarSize, number> = {
  sm: 32,
  md: 44,
  lg: 64,
  xl: 96,
};

export interface AvatarProps {
  /** Remote image URL. Falls back to initials when absent or on error. */
  url?: string;
  /** Display name used to derive initials when no image is available. */
  name?: string;
  size?: AvatarSize | number;
  accessibilityLabel?: string;
  style?: StyleProp<ImageStyle>;
  className?: string;
}

function initials(name?: string): string {
  if (!name) return "?";
  const parts = name.trim().split(/\s+/).filter(Boolean);
  if (parts.length === 0) return "?";
  if (parts.length === 1) return parts[0]!.slice(0, 2).toUpperCase();
  return (parts[0]![0]! + parts[parts.length - 1]![0]!).toUpperCase();
}

export const Avatar: React.FC<AvatarProps> = ({
  url,
  name,
  size = "md",
  accessibilityLabel,
  style,
  className,
}) => {
  const { colors } = useTheme();
  const [failed, setFailed] = useState(false);
  const dim = typeof size === "number" ? size : sizeMap[size];
  const showImage = Boolean(url) && !failed;

  return (
    <View
      className={cn("items-center justify-center overflow-hidden rounded-full", className)}
      style={[
        {
          width: dim,
          height: dim,
          borderRadius: dim / 2,
          backgroundColor: colors.primarySoft,
        },
        style as StyleProp<ViewStyle>,
      ]}
      accessibilityElementsHidden={showImage}
      importantForAccessibility={showImage ? "no-hide-descendants" : "auto"}
    >
      {showImage ? (
        <Image
          source={{ uri: url }}
          style={{ width: dim, height: dim, borderRadius: dim / 2 }}
          accessibilityLabel={accessibilityLabel ?? name ?? "Avatar"}
          onError={() => setFailed(true)}
        />
      ) : (
        <Text
          style={{ color: colors.primary, fontWeight: "600", fontSize: dim * 0.36 }}
          accessibilityLabel={accessibilityLabel ?? name ?? "Avatar"}
        >
          {initials(name)}
        </Text>
      )}
    </View>
  );
};
