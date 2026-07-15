import React, { useEffect, useState } from "react";
import { Modal, Pressable, Text, View, useWindowDimensions, useReducedMotion } from "react-native";
import { Gesture, GestureDetector } from "react-native-gesture-handler";
import Animated, {
  runOnJS,
  useAnimatedStyle,
  useSharedValue,
  withSpring,
  withTiming,
} from "react-native-reanimated";
import { useTheme } from "@/theme/ThemeProvider";

export interface BottomSheetProps {
  visible: boolean;
  onClose: () => void;
  title?: string;
  children: React.ReactNode;
}

const SPRING = { damping: 30, stiffness: 320, mass: 0.8 } as const;

/**
 * Accessible bottom sheet built on React Native Reanimated + Gesture Handler.
 * Drag down past 30% (or with velocity) to dismiss. Respects reduced motion.
 */
export const BottomSheet: React.FC<BottomSheetProps> = ({
  visible,
  onClose,
  title,
  children,
}) => {
  const { colors } = useTheme();
  const reduced = useReducedMotion();
  const { height: screenHeight } = useWindowDimensions();
  const translateY = useSharedValue(screenHeight);
  const [sheetHeight, setSheetHeight] = useState(screenHeight);

  const close = (): void => {
    const target = sheetHeight;
    if (reduced) {
      translateY.value = withTiming(target, { duration: 0 }, (finished) => {
        if (finished) runOnJS(onClose)();
      });
    } else {
      translateY.value = withSpring(target, SPRING, (finished) => {
        if (finished) runOnJS(onClose)();
      });
    }
  };

  useEffect(() => {
    if (!visible) return;
    if (reduced) {
      translateY.value = withTiming(0, { duration: 0 });
    } else {
      translateY.value = withSpring(0, SPRING);
    }
  }, [visible, reduced, translateY]);

  const pan = Gesture.Pan()
    .onUpdate((event) => {
      translateY.value = Math.max(0, event.translationY);
    })
    .onEnd((event) => {
      if (event.translationY > sheetHeight * 0.3 || event.velocityY > 800) {
        close();
      } else {
        translateY.value = reduced ? withTiming(0, { duration: 0 }) : withSpring(0, SPRING);
      }
    });

  const animatedStyle = useAnimatedStyle(() => ({
    transform: [{ translateY: translateY.value }],
  }));

  if (!visible) return null;

  return (
    <Modal visible={visible} transparent statusBarTranslucent animationType="none">
      <Pressable
        className="flex-1"
        style={{ backgroundColor: "rgba(0,0,0,0.5)" }}
        accessibilityRole="button"
        accessibilityLabel="Close sheet"
        onPress={close}
      >
        <View className="flex-1 justify-end">
          <GestureDetector gesture={pan}>
            <Animated.View
              className="w-full rounded-t-2xl border-t px-4 pb-6 pt-2"
              style={[
                animatedStyle,
                { backgroundColor: colors.surface, borderColor: colors.border },
              ]}
              onLayout={(event) => setSheetHeight(event.nativeEvent.layout.height)}
              accessibilityViewIsModal
              accessibilityRole="dialog"
              accessibilityLabel={title}
            >
              <View className="mb-2 items-center" pointerEvents="none">
                <View
                  className="h-1 w-10 rounded-full"
                  style={{ backgroundColor: colors.border }}
                />
              </View>
              {title ? (
                <Text className="mb-3 text-lg font-bold" style={{ color: colors.text }}>
                  {title}
                </Text>
              ) : null}
              {children}
            </Animated.View>
          </GestureDetector>
        </View>
      </Pressable>
    </Modal>
  );
};
