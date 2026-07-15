import React from "react";
import { View, Pressable, Text } from "react-native";
import { Slot, useRouter, usePathname } from "expo-router";
import { Home, Compass, MessageCircle, Users, User } from "lucide-react-native";
import { useResponsive } from "../../src/hooks/useResponsive";
import { useAuth } from "../../src/hooks/useAuth";
import { cn } from "../../src/lib/cn";

const NAV = [
  { href: "/home", label: "Home", Icon: Home },
  { href: "/explore", label: "Explore", Icon: Compass },
  { href: "/chat", label: "Chat", Icon: MessageCircle },
  { href: "/community", label: "Community", Icon: Users },
  { href: "/profile", label: "Profile", Icon: User },
];

function isActive(pathname: string, href: string) {
  if (href === "/home") return pathname === "/home" || pathname === "/";
  return pathname.startsWith(href);
}

function NavItem({ href, label, Icon, active }: { href: string; label: string; Icon: any; active: boolean }) {
  const router = useRouter();
  return (
    <Pressable
      onPress={() => router.push(href as any)}
      className={cn("flex-1 items-center justify-center py-2", active ? "text-primary" : "text-muted")}
      accessibilityRole="button"
      accessibilityLabel={label}
      accessibilityState={{ selected: active }}
    >
      <Icon size={22} color={active ? "#4f46e5" : "#64748b"} />
      <Text className={cn("mt-0.5 text-xs", active ? "font-semibold text-primary" : "text-muted")}>{label}</Text>
    </Pressable>
  );
}

export default function AppLayout() {
  const { isDesktop } = useResponsive();
  const pathname = usePathname();
  const { isAdmin } = useAuth();

  const items = isAdmin ? [...NAV, { href: "/admin", label: "Admin", Icon: User }] : NAV;

  if (isDesktop) {
    return (
      <View className="flex-1 flex-row bg-background">
        <View className="w-60 border-r border-border bg-surface px-3 py-4">
          <Text className="px-2 pb-4 text-xl font-bold text-primary">ManaBandhu</Text>
          {items.map((it) => (
            <Pressable
              key={it.href}
              onPress={() => useRouter().push(it.href as any)}
              className={cn("mb-1 flex-row items-center gap-3 rounded-md px-3 py-2.5", isActive(pathname, it.href) ? "bg-primary-soft" : "")}
              accessibilityRole="button"
              accessibilityLabel={it.label}
            >
              <it.Icon size={20} color={isActive(pathname, it.href) ? "#4f46e5" : "#64748b"} />
              <Text className={cn("text-base", isActive(pathname, it.href) ? "font-semibold text-primary" : "text-text")}>{it.label}</Text>
            </Pressable>
          ))}
        </View>
        <View className="flex-1">
          <Slot />
        </View>
      </View>
    );
  }

  return (
    <View className="flex-1 bg-background">
      <View className="flex-1">
        <Slot />
      </View>
      <View className="flex-row border-t border-border bg-surface" accessibilityRole="tablist">
        {items.map((it) => (
          <NavItem key={it.href} href={it.href} label={it.label} Icon={it.Icon} active={isActive(pathname, it.href)} />
        ))}
      </View>
    </View>
  );
}
