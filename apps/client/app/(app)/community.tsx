import { View, FlatList } from "react-native";
import { useApiQuery } from "../../src/hooks/useApiQuery";
import { Screen } from "../../src/components/ui/Screen";
import { Card } from "../../src/components/ui/Card";
import { Badge } from "../../src/components/ui/Badge";
import { Skeleton } from "../../src/components/ui/Skeleton";
import { EmptyState } from "../../src/components/ui/EmptyState";
import { ErrorState } from "../../src/components/ui/ErrorState";
import { Text as T } from "../../src/components/ui/Text";
import type { Paginated, Community } from "@manabandhu/contracts";

export default function CommunityScreen() {
  const { data, isLoading, isError, refetch } = useApiQuery<Paginated<Community>>(["communities"], "/communities?size=20");
  const items = data?.items ?? [];
  return (
    <Screen title="Community">
      {isLoading ? <Skeleton className="h-24 w-full" /> : isError ? <ErrorState onRetry={refetch} /> :
        items.length === 0 ? <EmptyState title="No communities" message="Discover groups near you." /> : (
          <FlatList data={items} keyExtractor={(i) => i.id} scrollEnabled={false}
            ItemSeparatorComponent={() => <View className="h-3" />}
            renderItem={({ item }) => (
              <Card>
                <T className="text-base font-semibold text-text">{item.name}</T>
                <T className="text-sm text-muted">{item.description}</T>
                <View className="mt-2 flex-row gap-1">
                  <Badge tone="neutral">{item.category}</Badge>
                  <Badge tone="info">{item.memberCount} members</Badge>
                </View>
              </Card>
            )} />
        )}
    </Screen>
  );
}
