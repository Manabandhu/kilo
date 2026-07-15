import React from "react";
import { Text, View } from "react-native";
import { ChevronLeft, ChevronRight } from "lucide-react-native";
import { cn } from "@/lib/cn";
import { useTheme } from "@/theme/ThemeProvider";
import { IconButton } from "./IconButton";

export interface PaginationProps {
  page: number;
  totalPages: number;
  onChange: (page: number) => void;
  siblingCount?: number;
  className?: string;
}

type PageToken = number | "ellipsis";

function buildRange(current: number, total: number, sibling: number): PageToken[] {
  const totalNumbers = sibling * 2 + 5; // first, last, current, 2 ellipses
  if (total <= totalNumbers) {
    return Array.from({ length: total }, (_, i) => i + 1);
  }

  const leftSibling = Math.max(current - sibling, 1);
  const rightSibling = Math.min(current + sibling, total);
  const showLeftEllipsis = leftSibling > 2;
  const showRightEllipsis = rightSibling < total - 1;

  const tokens: PageToken[] = [1];
  if (showLeftEllipsis) tokens.push("ellipsis");
  else if (leftSibling === 2) tokens.push(2);

  for (let p = leftSibling; p <= rightSibling; p++) {
    if (p !== 1 && p !== total) tokens.push(p);
  }

  if (showRightEllipsis) tokens.push("ellipsis");
  else if (rightSibling === total - 1) tokens.push(total - 1);

  tokens.push(total);
  return tokens;
}

export const Pagination: React.FC<PaginationProps> = ({
  page,
  totalPages,
  onChange,
  siblingCount = 1,
  className,
}) => {
  const { colors } = useTheme();
  if (totalPages <= 1) return null;

  const tokens = buildRange(page, totalPages, siblingCount);

  return (
    <View className={cn("flex-row items-center justify-center gap-1 py-2", className)}>
      <IconButton
        accessibilityLabel="Previous page"
        icon={<ChevronLeft size={20} color={colors.text} />}
        variant="secondary"
        size="sm"
        disabled={page <= 1}
        onPress={() => onChange(page - 1)}
      />
      {tokens.map((token, idx) =>
        token === "ellipsis" ? (
          <View key={`e-${idx}`} className="items-center justify-center px-2" style={{ minWidth: 36, minHeight: 44 }}>
            <Text style={{ color: colors.textMuted }}>…</Text>
          </View>
        ) : (
          <PaginationButton
            key={token}
            page={token}
            active={token === page}
            onPress={() => onChange(token)}
            colors={colors}
          />
        ),
      )}
      <IconButton
        accessibilityLabel="Next page"
        icon={<ChevronRight size={20} color={colors.text} />}
        variant="secondary"
        size="sm"
        disabled={page >= totalPages}
        onPress={() => onChange(page + 1)}
      />
    </View>
  );
};

const PaginationButton: React.FC<{
  page: number;
  active: boolean;
  onPress: () => void;
  colors: ReturnType<typeof useTheme>["colors"];
}> = ({ page, active, onPress, colors }) => {
  return (
    <Text
      accessibilityRole="button"
      accessibilityLabel={`Page ${page}`}
      accessibilityState={{ selected: active }}
      onPress={onPress}
      className="items-center justify-center rounded-lg px-3 text-base font-semibold"
      style={{
        minWidth: 36,
        minHeight: 44,
        textAlign: "center",
        textAlignVertical: "center",
        color: active ? colors.primary : colors.text,
        backgroundColor: active ? colors.primarySoft : "transparent",
        overflow: "hidden",
        paddingVertical: 12,
      }}
    >
      {page}
    </Text>
  );
};
