import { useQuery, type UseQueryOptions } from "@tanstack/react-query";
import { apiClient, ApiClientError } from "../lib/apiClient";

export function useApiQuery<T>(key: unknown[], path: string, options?: Omit<UseQueryOptions<T, ApiClientError>, "queryKey" | "queryFn">) {
  return useQuery<T, ApiClientError>({
    queryKey: key,
    queryFn: () => apiClient.get<T>(path),
    ...options,
  });
}
