import React from "react";
import { View, Text } from "react-native";
import { cn } from "../../lib/cn";

export function Avatar({ url, name, size = 40 }: { url?: string; name?: string; size?: number }) {
  const initials = (name ?? "?")
    .split(" ")
    .map((p) => p[0])
    .slice(0, 2)
    .join("")
    .toUpperCase();
  const dimension = { width: size, height: size, borderRadius: size / 2 };
  if (url) {
    return (
      <View
        className="items-center justify-center overflow-hidden bg-primary-soft"
        style={dimension}
        accessibilityRole="image"
      >
        {/* eslint-disable-next-line react-native/no-raw-image */}
        <Text style={{ display: "none" }}>{name}</Text>
        <AvatarImage uri={url} dimension={dimension} />
      </View>
    );
  }
  return (
    <View
      className="items-center justify-center bg-primary-soft"
      style={dimension}
      accessibilityRole="image"
      accessibilityLabel={name}
    >
      <Text className="text-sm font-semibold text-primary">{initials}</Text>
    </View>
  );
}

function AvatarImage({ uri, dimension }: { uri: string; dimension: { width: number; height: number; borderRadius: number } }) {
  const { Image } = require("react-native");
  return <Image source={{ uri }} style={dimension} className="bg-primary-soft" />;
}
