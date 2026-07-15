import { useState } from "react";
import { View, FlatList, Pressable } from "react-native";
import { useRouter } from "expo-router";
import { useApiQuery } from "../../src/hooks/useApiQuery";
import { useDebounce } from "../../src/hooks/useDebounce";
import { Screen } from "../../src/components/ui/Screen";
import { Card } from "../../src/components/ui/Card";
import { Badge } from "../../src/components/ui/Badge";
import { SearchBar } from "../../src/components/ui/SearchBar";
import { Skeleton } from "../../src/components/ui/Skeleton";
import { EmptyState } from "../../src/components/ui/EmptyState";
import { ErrorState } from "../../src/components/ui/ErrorState";
import { Text as T } from "../../src/components/ui/Text";
import { formatMoney } from "../../src/lib/format";
import type { Paginated, RoomListing } from "@manabandhu/contracts";

export default function RoomsScreen() {
  const router = useRouter();
  const [query, setQuery] = useState("");
  const debounced = useDebounce(query, 350);
  const { data, isLoading, isError, refetch } = useApiQuery<Paginated<RoomListing>>(
    ["rooms", debounced],
    `/rooms?query=${encodeURIComponent(debounced)}&size=20`,
  );

  const items = data?.items ?? [];

  return (
    <Screen title="Rooms & Housing">
      <View className="gap-3">
        <SearchBar value={query} onChangeText={setQuery} placeholder="Search city, title…" />
        {isLoading ? (
          <Skeleton className="h-24 w-full" />
        ) : isError ? (
          <ErrorState onRetry={refetch} />
        ) : items.length === 0 ? (
          <EmptyState title="No listings yet" message="Be the first to post a room." />
        ) : (
          <FlatList
            data={items}
            keyExtractor={(i) => i.id}
            scrollEnabled={false}
            ItemSeparatorComponent={() => <View className="h-3" />}
            renderItem={({ item }) => (
              <Pressable onPress={() => router.push(`/(explore)/rooms/${item.id}` as any)} accessibilityRole="button" accessibilityLabel={item.title}>
                <Card>
                  <T className="text-base font-semibold text-text">{item.title}</T>
                  <T className="text-sm text-muted">{item.city}, {item.state}</T>
                  <View className="mt-2 flex-row items-center justify-between">
                    <T className="text-lg font-bold text-primary">{formatMoney(item.monthlyRent)}/mo</T>
                    <Badge tone="neutral">{item.roomType}</Badge>
                  </View>
                </Card>
              </Pressable>
            )}
          />
        )}
      </View>
    </Screen>
  );
}
