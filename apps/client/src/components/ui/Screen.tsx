import React from "react";
import { View, ScrollView, SafeAreaView, Text } from "react-native";
import { cn } from "../../lib/cn";

export function Screen({
  title,
  children,
  headerRight,
  scroll = true,
  className,
}: {
  title?: string;
  children: React.ReactNode;
  headerRight?: React.ReactNode;
  scroll?: boolean;
  className?: string;
}) {
  const content = (
    <View className={cn("flex-1 bg-background px-4 pt-3", className)}>
      {title ? (
        <View className="mb-3 flex-row items-center justify-between">
          <Text className="text-2xl font-bold text-text">{title}</Text>
          {headerRight}
        </View>
      ) : null}
      {children}
    </View>
  );

  return (
    <SafeAreaView className="flex-1 bg-background" edges={["top", "left", "right"]}>
      {scroll ? <ScrollView className="flex-1" keyboardShouldPersistTaps="handled">{content}</ScrollView> : content}
    </SafeAreaView>
  );
}
