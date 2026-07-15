import { useWindowDimensions } from "react-native";

export interface Responsive {
  width: number;
  isMobile: boolean;
  isTablet: boolean;
  isDesktop: boolean;
  isLargeDesktop: boolean;
}

export function useResponsive(): Responsive {
  const { width } = useWindowDimensions();
  return {
    width,
    isMobile: width < 640,
    isTablet: width >= 640 && width < 1024,
    isDesktop: width >= 1024 && width < 1440,
    isLargeDesktop: width >= 1440,
  };
}
