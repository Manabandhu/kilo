import type { UUID, ISODateTime } from "./common";

export type ConversationKind = "direct" | "group";
export type MessageStatus = "sent" | "delivered" | "read" | "failed";

export interface Conversation {
  id: UUID;
  kind: ConversationKind;
  title?: string; // for groups
  memberIds: UUID[];
  lastMessage?: MessageSummary;
  unreadCount: number;
  muted: boolean;
  createdAt: ISODateTime;
  updatedAt: ISODateTime;
}

export interface MessageSummary {
  id: UUID;
  body: string;
  senderId: UUID;
  createdAt: ISODateTime;
}

export interface Message {
  id: UUID;
  conversationId: UUID;
  senderId: UUID;
  body: string;
  attachments: MessageAttachment[];
  replyToId?: UUID;
  reaction?: string;
  edited: boolean;
  status: MessageStatus;
  createdAt: ISODateTime;
  updatedAt: ISODateTime;
}

export interface MessageAttachment {
  id: UUID;
  type: "image" | "file";
  url: string;
  name?: string;
  sizeBytes?: number;
}

export interface SendMessageRequest {
  conversationId: UUID;
  body: string;
  replyToId?: UUID;
  attachments?: { type: "image" | "file"; url: string; name?: string }[];
}

export interface ConversationMember {
  conversationId: UUID;
  userId: UUID;
  role: "member" | "admin";
  joinedAt: ISODateTime;
  muted: boolean;
}
