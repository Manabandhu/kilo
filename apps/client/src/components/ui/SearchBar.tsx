import React, { useEffect, useRef, useState } from "react";
import { TextInput, View } from "react-native";
import { Search, X } from "lucide-react-native";
import { cn } from "@/lib/cn";
import { useTheme } from "@/theme/ThemeProvider";

export interface SearchBarProps {
  placeholder?: string;
  /** Called with the debounced query text. */
  onChangeText?: (text: string) => void;
  /** Called immediately when the user submits (e.g. enter / search icon). */
  onSearch?: (text: string) => void;
  delay?: number;
  initialValue?: string;
  accessibilityLabel?: string;
  className?: string;
}

export const SearchBar: React.FC<SearchBarProps> = ({
  placeholder = "Search",
  onChangeText,
  onSearch,
  delay = 300,
  initialValue = "",
  accessibilityLabel,
  className,
}) => {
  const { colors } = useTheme();
  const [text, setText] = useState(initialValue);
  const timer = useRef<ReturnType<typeof setTimeout> | null>(null);

  useEffect(() => {
    return () => {
      if (timer.current) clearTimeout(timer.current);
    };
  }, []);

  const handleChange = (next: string): void => {
    setText(next);
    if (timer.current) clearTimeout(timer.current);
    if (onChangeText) {
      timer.current = setTimeout(() => onChangeText(next), delay);
    }
  };

  const clear = (): void => {
    setText("");
    if (timer.current) clearTimeout(timer.current);
    onChangeText?.("");
  };

  return (
    <View
      className={cn("flex-row items-center rounded-lg border bg-surface px-3", className)}
      style={{ minHeight: 44, borderColor: colors.border, backgroundColor: colors.surface }}
    >
      <Search size={18} color={colors.textMuted} accessibilityLabel="Search" />
      <TextInput
        className="mx-2 flex-1 py-2 text-base"
        style={{ color: colors.text }}
        placeholder={placeholder}
        placeholderTextColor={colors.textMuted}
        value={text}
        onChangeText={handleChange}
        onSubmitEditing={() => onSearch?.(text)}
        returnKeyType="search"
        accessibilityLabel={accessibilityLabel ?? placeholder}
      />
      {text.length > 0 ? (
        <View
          accessibilityRole="button"
          accessibilityLabel="Clear search"
          hitSlop={8}
          onTouchEnd={clear}
        >
          <X size={18} color={colors.textMuted} />
        </View>
      ) : null}
    </View>
  );
};
