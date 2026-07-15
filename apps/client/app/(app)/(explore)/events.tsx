import { View, FlatList } from "react-native";
import { useApiQuery } from "../../src/hooks/useApiQuery";
import { Screen } from "../../src/components/ui/Screen";
import { Card } from "../../src/components/ui/Card";
import { Badge } from "../../src/components/ui/Badge";
import { Skeleton } from "../../src/components/ui/Skeleton";
import { EmptyState } from "../../src/components/ui/EmptyState";
import { ErrorState } from "../../src/components/ui/ErrorState";
import { Text as T } from "../../src/components/ui/Text";
import type { Paginated, AppEvent } from "@manabandhu/contracts";

export default function EventsScreen() {
  const { data, isLoading, isError, refetch } = useApiQuery<Paginated<AppEvent>>(["events"], "/events?size=20");
  const items = data?.items ?? [];
  return (
    <Screen title="Events">
      {isLoading ? <Skeleton className="h-24 w-full" /> : isError ? <ErrorState onRetry={refetch} /> :
        items.length === 0 ? <EmptyState title="No events yet" message="Host or RSVP to an event." /> : (
          <FlatList data={items} keyExtractor={(i) => i.id} scrollEnabled={false}
            ItemSeparatorComponent={() => <View className="h-3" />}
            renderItem={({ item }) => (
              <Card>
                <T className="text-base font-semibold text-text">{item.title}</T>
                <T className="text-sm text-muted">{item.city ?? ""} {item.state ?? ""} · {item.venueType}</T>
                <View className="mt-2"><Badge tone="info">{item.attendeeCount} going</Badge></View>
              </Card>
            )} />
        )}
    </Screen>
  );
}
