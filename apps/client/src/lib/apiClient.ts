import type { ApiError, Paginated } from "@manabandhu/contracts";
import { env } from "./env";

type TokenProvider = () => string | null | undefined;

let tokenProvider: TokenProvider = () => null;

export function setApiTokenProvider(provider: TokenProvider): void {
  tokenProvider = provider;
}

export class ApiClientError extends Error {
  status: number;
  code: string;
  fieldErrors?: Record<string, string>;
  traceId?: string;

  constructor(err: ApiError) {
    super(err.message);
    this.name = "ApiClientError";
    this.status = err.status;
    this.code = err.code;
    this.fieldErrors = err.fieldErrors;
    this.traceId = err.traceId;
  }
}

async function request<T>(method: string, path: string, body?: unknown): Promise<T> {
  const headers: Record<string, string> = { "Content-Type": "application/json" };
  const token = tokenProvider();
  if (token) headers.Authorization = `Bearer ${token}`;

  const res = await fetch(`${env.apiBaseUrl}${path}`, {
    method,
    headers,
    body: body === undefined ? undefined : JSON.stringify(body),
  });

  if (res.status === 204) return undefined as T;

  const text = await res.text();
  const json = text ? JSON.parse(text) : null;

  if (!res.ok) {
    const err = (json ?? {
      status: res.status,
      code: "INTERNAL_ERROR",
      message: res.statusText,
      path,
    }) as ApiError;
    if (res.status === 401) {
      // signal auth failure so callers can redirect to login
      throw new ApiClientError({ ...err, code: "UNAUTHORIZED" });
    }
    throw new ApiClientError(err);
  }

  // Backend returns { data: T } for single items; lists return Paginated directly.
  if (json && typeof json === "object" && "data" in json) {
    return (json as { data: T }).data;
  }
  return json as T;
}

export const apiClient = {
  get: <T>(path: string) => request<T>("GET", path),
  post: <T>(path: string, body?: unknown) => request<T>("POST", path, body),
  put: <T>(path: string, body?: unknown) => request<T>("PUT", path, body),
  patch: <T>(path: string, body?: unknown) => request<T>("PATCH", path, body),
  del: <T>(path: string) => request<T>("DELETE", path),
};

export type { Paginated };
