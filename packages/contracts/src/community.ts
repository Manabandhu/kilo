import type { UUID, ISODateTime, UsState } from "./common";
import type { PublicProfileSummary } from "./rooms";

export type CommunityKind = "city" | "topic";
export type CommunityCategory =
  | "housing"
  | "jobs"
  | "immigration"
  | "students"
  | "families"
  | "city"
  | "transportation"
  | "marketplace"
  | "general";

export interface Community {
  id: UUID;
  name: string;
  slug: string;
  description: string;
  kind: CommunityKind;
  category: CommunityCategory;
  city?: string;
  state?: UsState;
  memberCount: number;
  coverImageUrl?: string;
  pinnedPostIds: UUID[];
  createdAt: ISODateTime;
}

export interface CommunityMembership {
  communityId: UUID;
  userId: UUID;
  joinedAt: ISODateTime;
  role: "member" | "moderator";
}

export type PostStatus = "published" | "deleted" | "moderated";
export type ReactionKind = "like" | "helpful" | "celebrate" | "support";

export interface Post {
  id: UUID;
  communityId: UUID;
  authorId: UUID;
  author?: PublicProfileSummary;
  title?: string;
  body: string;
  tags: string[];
  pinned: boolean;
  saved: boolean;
  reactions: Record<ReactionKind, number>;
  userReaction?: ReactionKind;
  commentCount: number;
  status: PostStatus;
  createdAt: ISODateTime;
  updatedAt: ISODateTime;
}
