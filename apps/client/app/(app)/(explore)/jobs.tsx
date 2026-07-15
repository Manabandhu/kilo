import { View, FlatList } from "react-native";
import { useApiQuery } from "../../src/hooks/useApiQuery";
import { Screen } from "../../src/components/ui/Screen";
import { Card } from "../../src/components/ui/Card";
import { Badge } from "../../src/components/ui/Badge";
import { Skeleton } from "../../src/components/ui/Skeleton";
import { EmptyState } from "../../src/components/ui/EmptyState";
import { ErrorState } from "../../src/components/ui/ErrorState";
import { Text as T } from "../../src/components/ui/Text";
import type { Paginated, Job } from "@manabandhu/contracts";

export default function JobsScreen() {
  const { data, isLoading, isError, refetch } = useApiQuery<Paginated<Job>>(["jobs"], "/jobs?size=20");
  const items = data?.items ?? [];
  return (
    <Screen title="Jobs & Referrals">
      {isLoading ? <Skeleton className="h-24 w-full" /> : isError ? <ErrorState onRetry={refetch} /> :
        items.length === 0 ? <EmptyState title="No jobs yet" message="Check back soon." /> : (
          <FlatList data={items} keyExtractor={(i) => i.id} scrollEnabled={false}
            ItemSeparatorComponent={() => <View className="h-3" />}
            renderItem={({ item }) => (
              <Card>
                <T className="text-base font-semibold text-text">{item.title}</T>
                <T className="text-sm text-muted">{item.company} · {item.location}</T>
                <View className="mt-2 flex-row gap-1">
                  <Badge tone="neutral">{item.workMode}</Badge>
                  {item.sponsorshipOffered ? <Badge tone="success">Sponsorship</Badge> : null}
                </View>
              </Card>
            )} />
        )}
    </Screen>
  );
}
