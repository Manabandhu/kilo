import React, { useEffect } from "react";
import {
  Animated,
  Easing,
  View,
  useReducedMotion,
  type StyleProp,
  type ViewStyle,
} from "react-native";
import { cn } from "@/lib/cn";
import { useTheme } from "@/theme/ThemeProvider";

export interface SkeletonProps {
  width?: number | string;
  height?: number | string;
  radius?: number;
  style?: StyleProp<ViewStyle>;
  className?: string;
}

/** Shimmering placeholder. Animations are disabled when reduced motion is on. */
export const Skeleton: React.FC<SkeletonProps> = ({
  width = "100%",
  height = 16,
  radius = 8,
  style,
  className,
}) => {
  const { colors } = useTheme();
  const reduced = useReducedMotion();
  const opacity = React.useRef(new Animated.Value(0.4)).current;

  useEffect(() => {
    if (reduced) {
      opacity.setValue(0.5);
      return;
    }
    const loop = Animated.loop(
      Animated.sequence([
        Animated.timing(opacity, {
          toValue: 1,
          duration: 800,
          easing: Easing.inOut(Easing.ease),
          useNativeDriver: true,
        }),
        Animated.timing(opacity, {
          toValue: 0.4,
          duration: 800,
          easing: Easing.inOut(Easing.ease),
          useNativeDriver: true,
        }),
      ]),
    );
    loop.start();
    return () => loop.stop();
  }, [opacity, reduced]);

  return (
    <Animated.View
      className={cn(className)}
      style={[
        {
          width,
          height,
          borderRadius: radius,
          backgroundColor: colors.surfaceAlt,
          opacity,
        },
        style,
      ]}
    />
  );
};
