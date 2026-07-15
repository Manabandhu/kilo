import { View, FlatList } from "react-native";
import { useApiQuery } from "../../src/hooks/useApiQuery";
import { Screen } from "../../src/components/ui/Screen";
import { Card } from "../../src/components/ui/Card";
import { Skeleton } from "../../src/components/ui/Skeleton";
import { EmptyState } from "../../src/components/ui/EmptyState";
import { ErrorState } from "../../src/components/ui/ErrorState";
import { Text as T } from "../../src/components/ui/Text";
import type { Paginated, Conversation } from "@manabandhu/contracts";

export default function ChatScreen() {
  const { data, isLoading, isError, refetch } = useApiQuery<Paginated<Conversation>>(["conversations"], "/conversations?size=20");
  const items = data?.items ?? [];
  return (
    <Screen title="Messages">
      {isLoading ? <Skeleton className="h-24 w-full" /> : isError ? <ErrorState onRetry={refetch} /> :
        items.length === 0 ? <EmptyState title="No conversations" message="Start a chat from a listing or profile." /> : (
          <FlatList data={items} keyExtractor={(i) => i.id} scrollEnabled={false}
            ItemSeparatorComponent={() => <View className="h-3" />}
            renderItem={({ item }) => (
              <Card>
                <T className="text-base font-semibold text-text">{item.kind === "group" ? item.title : "Direct message"}</T>
                <T className="text-sm text-muted">{item.memberIds.length} members · {item.unreadCount} unread</T>
              </Card>
            )} />
        )}
    </Screen>
  );
}
