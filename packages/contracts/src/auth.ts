import type { UUID, ISODateTime, UserRole, AccountStatus } from "./common";

export interface AuthUser {
  id: UUID;
  email: string;
  emailVerified: boolean;
  phoneVerified: boolean;
  role: UserRole;
  accountStatus: AccountStatus;
  createdAt: ISODateTime;
}

export interface Session {
  accessToken: string;
  refreshToken: string;
  expiresAt: ISODateTime;
  user: AuthUser;
}

export interface RegisterRequest {
  email: string;
  password: string;
  displayName: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface PasswordResetRequest {
  email: string;
}

export interface PasswordUpdateRequest {
  token: string;
  newPassword: string;
}

export interface AuthResponse {
  session: Session;
  needsOnboarding: boolean;
}

export type OnboardingStep =
  | "profile"
  | "location"
  | "languages"
  | "occupation"
  | "status"
  | "interests"
  | "modules"
  | "notifications"
  | "privacy";

export const ONBOARDING_STEPS: OnboardingStep[] = [
  "profile",
  "location",
  "languages",
  "occupation",
  "status",
  "interests",
  "modules",
  "notifications",
  "privacy",
];
