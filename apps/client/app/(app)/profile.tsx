import { View } from "react-native";
import { useRouter } from "expo-router";
import { useAuth } from "../../src/hooks/useAuth";
import { useApiQuery } from "../../src/hooks/useApiQuery";
import { Screen } from "../../src/components/ui/Screen";
import { Card } from "../../src/components/ui/Card";
import { Avatar } from "../../src/components/ui/Avatar";
import { Badge } from "../../src/components/ui/Badge";
import { Button } from "../../src/components/ui/Button";
import { Text as T } from "../../src/components/ui/Text";
import { Skeleton } from "../../src/components/ui/Skeleton";
import type { UserProfile } from "@manabandhu/contracts";

export default function ProfileScreen() {
  const { signOut } = useAuth();
  const router = useRouter();
  const { data, isLoading } = useApiQuery<UserProfile>(["profile", "me"], "/profiles/me");

  return (
    <Screen title="Profile">
      {isLoading ? (
        <Skeleton className="h-40 w-full" />
      ) : data ? (
        <View className="gap-3">
          <Card>
            <View className="flex-row items-center gap-3">
              <Avatar url={data.avatarUrl} name={data.displayName} size={56} />
              <View>
                <T className="text-lg font-bold text-text">{data.displayName}</T>
                <T className="text-sm text-muted">{[data.city, data.state].filter(Boolean).join(", ")}</T>
                <View className="mt-1 flex-row gap-1">
                  {data.verification.emailVerified ? <Badge tone="success">Email</Badge> : null}
                  {data.verification.profileCompleted ? <Badge tone="info">Verified</Badge> : null}
                </View>
              </View>
            </View>
          </Card>
          <Card>
            <T className="text-sm font-semibold text-text">Profile completion</T>
            <T className="text-2xl font-bold text-primary">{data.profileCompletionScore}%</T>
            <T className="text-sm text-muted">Rating: {data.ratingAverage.toFixed(1)} ({data.ratingCount})</T>
          </Card>
          <Button variant="outline" onPress={() => router.push("/(explore)/expenses" as any)} accessibilityLabel="Settings">
            Settings
          </Button>
          <Button variant="danger" onPress={signOut} accessibilityLabel="Log out">
            Log out
          </Button>
        </View>
      ) : null}
    </Screen>
  );
}
