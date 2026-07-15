import { View, ScrollView } from "react-native";
import { useRouter } from "expo-router";
import { useAuth } from "../../src/hooks/useAuth";
import { useApiQuery } from "../../src/hooks/useApiQuery";
import { Screen } from "../../src/components/ui/Screen";
import { Text as T } from "../../src/components/ui/Text";
import { Card } from "../../src/components/ui/Card";
import { Button } from "../../src/components/ui/Button";
import { Skeleton } from "../../src/components/ui/Skeleton";
import { EmptyState } from "../../src/components/ui/EmptyState";
import { ErrorState } from "../../src/components/ui/ErrorState";
import type { UserProfile } from "@manabandhu/contracts";

const QUICK_ACTIONS = [
  { label: "Find housing", href: "/(explore)/rooms" },
  { label: "Offer a ride", href: "/(explore)/rides" },
  { label: "Browse jobs", href: "/(explore)/jobs" },
  { label: "Ask community", href: "/community" },
];

export default function HomeScreen() {
  const { user } = useAuth();
  const router = useRouter();
  const { data, isLoading, isError, refetch } = useApiQuery<UserProfile>(["profile", "me"], "/profiles/me");

  return (
    <Screen title={`Hi${user?.email ? "" : ""}, ${data?.displayName ?? (user ? "there" : "")}`}>
      <View className="gap-4">
        <Card>
          <T className="text-sm text-muted">Your trusted community super app</T>
          <View className="mt-3 flex-row flex-wrap gap-2">
            {QUICK_ACTIONS.map((a) => (
              <Button key={a.href} variant="secondary" size="sm" onPress={() => router.push(a.href as any)} accessibilityLabel={a.label}>
                {a.label}
              </Button>
            ))}
          </View>
        </Card>

        {isLoading ? (
          <Skeleton className="h-24 w-full" />
        ) : isError ? (
          <ErrorState onRetry={refetch} />
        ) : data ? (
          <Card>
            <T className="text-sm font-semibold text-text">Profile completion</T>
            <T className="mt-1 text-2xl font-bold text-primary">{data.profileCompletionScore}%</T>
          </Card>
        ) : (
          <EmptyState title="Complete your profile" message="Add details to get better matches." />
        )}

        <Card>
          <T className="text-sm font-semibold text-text">Safety tip</T>
          <T className="mt-1 text-sm text-muted">
            Never share passwords or send money outside ManaBandhu. Report anything suspicious.
          </T>
        </Card>

        <Button variant="outline" onPress={() => router.push("/(explore)" as any)} accessibilityLabel="Explore all features">
          Explore everything
        </Button>
      </View>
    </Screen>
  );
}
