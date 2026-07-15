import { View } from "react-native";
import { useApiQuery } from "../../src/hooks/useApiQuery";
import { Screen } from "../../src/components/ui/Screen";
import { Card } from "../../src/components/ui/Card";
import { Button } from "../../src/components/ui/Button";
import { Skeleton } from "../../src/components/ui/Skeleton";
import { EmptyState } from "../../src/components/ui/EmptyState";
import { ErrorState } from "../../src/components/ui/ErrorState";
import { Text as T } from "../../src/components/ui/Text";
import type { ExpenseGroupResponse } from "@manabandhu/contracts";

export default function ExpensesScreen() {
  const { data, isLoading, isError, refetch } = useApiQuery<ExpenseGroupResponse[]>(["expense-groups"], "/expense-groups");
  const items = data ?? [];
  return (
    <Screen title="Shared Expenses">
      {isLoading ? <Skeleton className="h-24 w-full" /> : isError ? <ErrorState onRetry={refetch} /> :
        items.length === 0 ? (
          <EmptyState title="No expense groups" message="Create a group to split bills." actionLabel="Create group" onAction={() => {}} />
        ) : (
          <View className="gap-3">
            {items.map((g) => (
              <Card key={g.id}>
                <T className="text-base font-semibold text-text">{g.name}</T>
                <T className="text-sm text-muted">{g.memberIds.length} members · {g.currency}</T>
              </Card>
            ))}
            <Button variant="secondary" accessibilityLabel="New group">New group</Button>
          </View>
        )}
    </Screen>
  );
}
