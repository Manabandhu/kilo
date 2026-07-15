import type { UUID, ISODateTime } from "./common";

export type ReportTargetType =
  | "room"
  | "ride"
  | "post"
  | "comment"
  | "message"
  | "job"
  | "event"
  | "user"
  | "resource";

export type ReportReason =
  | "spam"
  | "scam"
  | "harassment"
  | "hate"
  | "inappropriate"
  | "misinformation"
  | "safety"
  | "other";

export type ReportStatus = "open" | "reviewing" | "resolved" | "dismissed";

export interface Report {
  id: UUID;
  reporterId: UUID;
  targetType: ReportTargetType;
  targetId: UUID;
  reason: ReportReason;
  detail?: string;
  status: ReportStatus;
  assignedToId?: UUID;
  resolution?: string;
  createdAt: ISODateTime;
  updatedAt: ISODateTime;
}

export interface CreateReportRequest {
  targetType: ReportTargetType;
  targetId: UUID;
  reason: ReportReason;
  detail?: string;
}

export type ModerationActionType =
  | "warning"
  | "content_removed"
  | "user_suspended"
  | "user_banned"
  | "user_reinstated"
  | "report_resolved"
  | "report_dismissed"
  | "note";

export interface ModerationAction {
  id: UUID;
  type: ModerationActionType;
  moderatorId: UUID;
  targetUserId?: UUID;
  reportId?: UUID;
  targetType?: ReportTargetType;
  targetId?: UUID;
  note?: string;
  createdAt: ISODateTime;
}

export interface ModerationNote {
  id: UUID;
  moderatorId: UUID;
  targetUserId: UUID;
  body: string;
  createdAt: ISODateTime;
}

export interface DashboardMetrics {
  totalUsers: number;
  activeUsers: number;
  openReports: number;
  newListingsToday: number;
  newRidesToday: number;
  newPostsToday: number;
  messagesToday: number;
  flaggedContent: number;
}
