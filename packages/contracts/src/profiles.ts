import type { UUID, ISODateTime, ISODate, UsState } from "./common";

export type UserArchetype = "student" | "professional" | "family" | "newcomer";

export type PrivacyLevel = "public" | "members" | "private";

export interface UserProfile {
  id: UUID;
  displayName: string;
  avatarUrl?: string;
  bio?: string;
  city?: string;
  state?: UsState;
  languages: string[];
  occupation?: string;
  archetype?: UserArchetype;
  university?: string;
  employer?: string;
  interests: string[];
  joinedAt: ISODateTime;
  profileCompletionScore: number; // 0-100
  ratingAverage: number; // 0-5
  ratingCount: number;
  verification: VerificationFlags;
  contributionLevel: number;
  privacy: PrivacyPreferences;
}

export interface VerificationFlags {
  emailVerified: boolean;
  phoneVerified: boolean;
  profileCompleted: boolean;
  trustedContributor: boolean;
  moderator: boolean;
}

export interface PrivacyPreferences {
  showCity: boolean;
  showEmployer: boolean;
  showUniversity: boolean;
  allowDirectMessages: boolean;
  showOnlineStatus: boolean;
}

export interface ProfileUpdateRequest {
  displayName?: string;
  bio?: string;
  city?: string;
  state?: UsState;
  languages?: string[];
  occupation?: string;
  archetype?: UserArchetype;
  university?: string;
  employer?: string;
  interests?: string[];
  privacy?: Partial<PrivacyPreferences>;
}

export interface UserRating {
  id: UUID;
  toUserId: UUID;
  fromUserId: UUID;
  score: number; // 1-5
  context: RatingContext;
  comment?: string;
  createdAt: ISODateTime;
}

export type RatingContext = "ride" | "room" | "community" | "general";

export interface BlockedUser {
  id: UUID;
  blockedUserId: UUID;
  createdAt: ISODateTime;
}

export interface NotificationPreferencesRequest {
  pushEnabled?: boolean;
  emailEnabled?: boolean;
  messages?: boolean;
  rides?: boolean;
  listings?: boolean;
  posts?: boolean;
  events?: boolean;
  expenses?: boolean;
  moderation?: boolean;
  marketing?: boolean;
}

export interface AccountDeletionRequest {
  reason?: string;
  confirmation: boolean;
}

export interface DataExportRequest {
  confirmation: boolean;
}
