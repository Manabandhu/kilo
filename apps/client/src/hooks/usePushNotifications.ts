import { useEffect, useState } from "react";
import * as Notifications from "expo-notifications";
import { Platform } from "react-native";
import { apiClient } from "../lib/apiClient";

Notifications.setNotificationHandler({
  handleNotification: async () => ({
    shouldShowAlert: true,
    shouldPlaySound: true,
    shouldSetBadge: true,
  }),
});

export function usePushNotifications(enabled = true) {
  const [token, setToken] = useState<string | null>(null);
  const [permission, setPermission] = useState<boolean>(false);

  useEffect(() => {
    if (!enabled) return;
    let active = true;
    (async () => {
      const { status } = await Notifications.getPermissionsAsync();
      let finalStatus = status;
      if (status !== "granted") {
        const req = await Notifications.requestPermissionsAsync();
        finalStatus = req.status;
      }
      setPermission(finalStatus === "granted");
      if (finalStatus !== "granted") return;

      const expoToken = (await Notifications.getExpoPushTokenAsync()).data;
      if (!active) return;
      setToken(expoToken);
      const platform = Platform.OS === "ios" ? "ios" : Platform.OS === "android" ? "android" : "web";
      await apiClient.post("/notifications/tokens", { token: expoToken, platform });
    })().catch(() => {
      /* ignore notification setup failures */
    });
    return () => {
      active = false;
    };
  }, [enabled]);

  return { token, permission };
}
