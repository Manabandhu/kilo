import type { UUID, ISODateTime } from "./common";

export type NotificationType =
  | "message"
  | "ride_request"
  | "ride_decision"
  | "ride_reminder"
  | "listing_inquiry"
  | "post_reply"
  | "comment_reply"
  | "event_reminder"
  | "expense_added"
  | "settlement_recorded"
  | "moderation_action"
  | "security_alert";

export interface AppNotification {
  id: UUID;
  userId: UUID;
  type: NotificationType;
  title: string;
  body: string;
  deepLink?: string; // e.g. /chat/:conversationId
  read: boolean;
  createdAt: ISODateTime;
}

export interface NotificationPreferences {
  pushEnabled: boolean;
  emailEnabled: boolean;
  messages: boolean;
  rides: boolean;
  listings: boolean;
  posts: boolean;
  events: boolean;
  expenses: boolean;
  moderation: boolean;
  marketing: boolean;
}

export interface PushTokenRegistration {
  token: string;
  platform: "ios" | "android" | "web";
  deviceId?: string;
}
