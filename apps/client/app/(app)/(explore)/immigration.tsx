import { View, FlatList } from "react-native";
import { useApiQuery } from "../../src/hooks/useApiQuery";
import { Screen } from "../../src/components/ui/Screen";
import { Card } from "../../src/components/ui/Card";
import { Badge } from "../../src/components/ui/Badge";
import { Skeleton } from "../../src/components/ui/Skeleton";
import { EmptyState } from "../../src/components/ui/EmptyState";
import { ErrorState } from "../../src/components/ui/ErrorState";
import { Text as T } from "../../src/components/ui/Text";
import type { Paginated, ImmigrationResource } from "@manabandhu/contracts";

export default function ImmigrationScreen() {
  const { data, isLoading, isError, refetch } = useApiQuery<Paginated<ImmigrationResource>>(["immigration"], "/immigration/resources?size=20");
  const items = data?.items ?? [];
  return (
    <Screen title="Immigration Resources">
      {isLoading ? <Skeleton className="h-24 w-full" /> : isError ? <ErrorState onRetry={refetch} /> :
        items.length === 0 ? <EmptyState title="No resources yet" message="Guides are being added." /> : (
          <FlatList data={items} keyExtractor={(i) => i.id} scrollEnabled={false}
            ItemSeparatorComponent={() => <View className="h-3" />}
            renderItem={({ item }) => (
              <Card>
                <T className="text-base font-semibold text-text">{item.title}</T>
                <T className="text-sm text-muted">Reviewed {item.lastReviewedAt.slice(0, 10)}</T>
                <View className="mt-2">{item.trustedContributor ? <Badge tone="success">Trusted contributor</Badge> : null}</View>
                <T className="mt-2 text-xs text-warning">General info, not legal advice.</T>
              </Card>
            )} />
        )}
    </Screen>
  );
}
