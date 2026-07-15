import { useColorScheme as useRNColorScheme } from "react-native";
import { useTheme } from "./ThemeProvider";

export const useColorScheme = (): "light" | "dark" => {
  const { isDark } = useTheme();
  return isDark ? "dark" : "light";
};

export { useRNColorScheme };
