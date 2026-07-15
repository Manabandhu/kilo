// Core API contracts shared across ManaBandhu clients and backend.

export type UUID = string;
export type ISODateTime = string;
export type ISODate = string;

export type SortOrder = "asc" | "desc";

export interface PageRequest {
  page?: number;
  size?: number;
  sort?: string; // "field,asc" | "field,desc"
}

export interface Paginated<T> {
  items: T[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
  hasNext: boolean;
}

export type ErrorCode =
  | "VALIDATION_ERROR"
  | "UNAUTHORIZED"
  | "FORBIDDEN"
  | "NOT_FOUND"
  | "CONFLICT"
  | "RATE_LIMITED"
  | "IDEMPOTENT_REPLAY"
  | "BUSINESS_RULE_VIOLATION"
  | "INTERNAL_ERROR";

export interface ApiError {
  timestamp: ISODateTime;
  status: number;
  code: ErrorCode;
  message: string;
  path: string;
  fieldErrors?: Record<string, string>;
  traceId?: string;
}

export interface ApiSuccess<T> {
  data: T;
}

export type ApiResponse<T> = ApiSuccess<T> | ApiError;

export type UserRole = "user" | "moderator" | "admin";

export type AccountStatus = "active" | "warned" | "suspended" | "banned" | "deleted";

export type ThemePreference = "light" | "dark" | "system";

export interface Coordinates {
  lat: number;
  lng: number;
}

export interface Address {
  city: string;
  state: string;
  zip?: string;
  country: string;
}

export const US_STATES = [
  "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
  "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
  "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
  "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
  "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY", "DC",
] as const;

export type UsState = (typeof US_STATES)[number];
