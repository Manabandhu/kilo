import { View } from "react-native";
import { useRouter } from "expo-router";
import { Screen } from "../../src/components/ui/Screen";
import { Card } from "../../src/components/ui/Card";
import { Text as T } from "../../src/components/ui/Text";

const MODULES = [
  { title: "Rooms & Housing", href: "/(explore)/rooms", desc: "Find or list a room" },
  { title: "Rides", href: "/(explore)/rides", desc: "Carpool & travel" },
  { title: "Jobs & Referrals", href: "/(explore)/jobs", desc: "Discover opportunities" },
  { title: "Immigration", href: "/(explore)/immigration", desc: "Guides & checklists" },
  { title: "Expenses", href: "/(explore)/expenses", desc: "Split bills fairly" },
  { title: "Events", href: "/(explore)/events", desc: "Meet your community" },
];

export default function ExploreScreen() {
  const router = useRouter();
  return (
    <Screen title="Explore">
      <View className="gap-3">
        {MODULES.map((m) => (
          <Card key={m.href} onPress={() => router.push(m.href as any)}>
            <T className="text-base font-semibold text-text">{m.title}</T>
            <T className="text-sm text-muted">{m.desc}</T>
          </Card>
        ))}
      </View>
    </Screen>
  );
}
