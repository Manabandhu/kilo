import React, { useState } from "react";
import {
  Image,
  NativeSyntheticEvent,
  NativeScrollEvent,
  ScrollView,
  Text,
  View,
  useWindowDimensions,
} from "react-native";
import { useTheme } from "@/theme/ThemeProvider";

export interface ImageGalleryProps {
  images: string[];
  onImagePress?: (index: number) => void;
  height?: number;
  accessibilityLabel?: string;
}

/** Swipeable image carousel with dot indicators. */
export const ImageGallery: React.FC<ImageGalleryProps> = ({
  images,
  onImagePress,
  height = 220,
  accessibilityLabel = "Image gallery",
}) => {
  const { colors } = useTheme();
  const { width: screenWidth } = useWindowDimensions();
  const [index, setIndex] = useState(0);
  const [containerWidth, setContainerWidth] = useState(screenWidth);

  if (images.length === 0) {
    return (
      <View
        className="items-center justify-center rounded-lg border"
        style={{ height, borderColor: colors.border, backgroundColor: colors.surfaceAlt }}
      >
        <Text style={{ color: colors.textMuted }}>No images</Text>
      </View>
    );
  }

  const onScroll = (event: NativeSyntheticEvent<NativeScrollEvent>): void => {
    const pageWidth = event.nativeEvent.layoutMeasurement.width;
    if (pageWidth === 0) return;
    const next = Math.round(event.nativeEvent.contentOffset.x / pageWidth);
    if (next !== index) setIndex(next);
  };

  return (
    <View
      accessibilityLabel={accessibilityLabel}
      onLayout={(event) => setContainerWidth(event.nativeEvent.layout.width)}
    >
      <ScrollView
        horizontal
        pagingEnabled
        showsHorizontalScrollIndicator={false}
        onMomentumScrollEnd={onScroll}
        className="rounded-lg"
        style={{ height }}
      >
        {images.map((uri, i) => (
          <Image
            key={`${uri}-${i}`}
            source={{ uri }}
            style={{ width: containerWidth, height }}
            resizeMode="cover"
            accessibilityLabel={`Image ${i + 1} of ${images.length}`}
            onTouchEnd={() => onImagePress?.(i)}
          />
        ))}
      </ScrollView>
      {images.length > 1 ? (
        <View className="mt-2 flex-row items-center justify-center gap-1.5">
          {images.map((_, i) => (
            <View
              key={i}
              className="rounded-full"
              style={{
                width: i === index ? 18 : 7,
                height: 7,
                backgroundColor: i === index ? colors.primary : colors.border,
              }}
            />
          ))}
        </View>
      ) : null}
    </View>
  );
};
