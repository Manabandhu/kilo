import { View } from "react-native";
import { useLocalSearchParams, useRouter } from "expo-router";
import { useApiQuery } from "../../../../src/hooks/useApiQuery";
import { useApiMutation } from "../../../../src/hooks/useApiMutation";
import { Screen } from "../../../../src/components/ui/Screen";
import { Card } from "../../../../src/components/ui/Card";
import { Badge } from "../../../../src/components/ui/Badge";
import { Button } from "../../../../src/components/ui/Button";
import { Text as T } from "../../../../src/components/ui/Text";
import { Skeleton } from "../../../../src/components/ui/Skeleton";
import { ErrorState } from "../../../../src/components/ui/ErrorState";
import { formatMoney } from "../../../../src/lib/format";
import { apiClient } from "../../../../src/lib/apiClient";
import type { RoomListing } from "@manabandhu/contracts";

export default function RoomDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const router = useRouter();
  const { data, isLoading, isError, refetch } = useApiQuery<RoomListing>(["room", id], `/rooms/${id}`);

  const save = useApiMutation<void>("post", `/profiles/me/saved`, {
    onSuccess: () => refetch(),
  });
  const report = useApiMutation<void, { reason: string }>("post", `/rooms/${id}/report`, {});

  if (isLoading) return <Screen title="Listing"><Skeleton className="h-48 w-full" /></Screen>;
  if (isError || !data) return <Screen title="Listing"><ErrorState onRetry={refetch} /></Screen>;

  return (
    <Screen title={data.title}>
      <View className="gap-3">
        <Card>
          <T className="text-sm text-muted">{data.city}, {data.state}{data.zip ? ` ${data.zip}` : ""}</T>
          <T className="mt-1 text-2xl font-bold text-primary">{formatMoney(data.monthlyRent)}/mo</T>
          <View className="mt-2 flex-row flex-wrap gap-1">
            <Badge tone="neutral">{data.roomType}</Badge>
            <Badge tone="neutral">{data.propertyType}</Badge>
            <Badge tone="neutral">{data.furnished}</Badge>
          </View>
          <T className="mt-3 text-sm text-text">{data.description}</T>
        </Card>
        <Card>
          <T className="text-sm font-semibold text-text">Details</T>
          <T className="text-sm text-muted">Occupancy: {data.occupancy} · Parking: {data.parking ? "Yes" : "No"}</T>
          <T className="text-sm text-muted">Pets: {data.petPolicy} · Smoking: {data.smokingPolicy}</T>
          <T className="text-sm text-muted">Utilities included: {data.utilitiesIncluded ? "Yes" : "No"}</T>
        </Card>
        <Button onPress={() => save.mutate({ itemType: "room", itemId: id } as any)} loading={save.isPending} accessibilityLabel="Save listing">
          Save listing
        </Button>
        <Button variant="ghost" onPress={() => report.mutate({ reason: "other" })} accessibilityLabel="Report listing">
          Report
        </Button>
      </View>
    </Screen>
  );
}
