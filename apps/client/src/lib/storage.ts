import * as SecureStore from "expo-secure-store";

const TOKEN_KEY = "manabandhu.session";
const PREFIX = "manabandhu.";

export const secureStore = {
  async getToken(): Promise<string | null> {
    try {
      return await SecureStore.getItemAsync(TOKEN_KEY);
    } catch {
      return null;
    }
  },
  async setToken(token: string): Promise<void> {
    try {
      await SecureStore.setItemAsync(TOKEN_KEY, token);
    } catch {
      /* ignore */
    }
  },
  async clearToken(): Promise<void> {
    try {
      await SecureStore.deleteItemAsync(TOKEN_KEY);
    } catch {
      /* ignore */
    }
  },
  async getItem(key: string): Promise<string | null> {
    try {
      return await SecureStore.getItemAsync(PREFIX + key);
    } catch {
      return null;
    }
  },
  async setItem(key: string, value: string): Promise<void> {
    try {
      await SecureStore.setItemAsync(PREFIX + key, value);
    } catch {
      /* ignore */
    }
  },
  async removeItem(key: string): Promise<void> {
    try {
      await SecureStore.deleteItemAsync(PREFIX + key);
    } catch {
      /* ignore */
    }
  },
};

// Zustand persist storage adapter backed by SecureStore.
export const zustandSecureStorage = {
  getItem: secureStore.getItem,
  setItem: secureStore.setItem,
  removeItem: secureStore.removeItem,
};
