import { useMutation, type UseMutationOptions } from "@tanstack/react-query";
import { apiClient, ApiClientError } from "../lib/apiClient";

export function useApiMutation<TData, TBody = unknown>(
  method: "post" | "put" | "patch" | "del",
  path: string | ((body: TBody) => string),
  options?: Omit<UseMutationOptions<TData, ApiClientError, TBody>, "mutationFn">,
) {
  return useMutation<TData, ApiClientError, TBody>({
    mutationFn: async (body: TBody) => {
      const url = typeof path === "function" ? path(body) : path;
      return apiClient[method]<TData>(url, body);
    },
    ...options,
  });
}
