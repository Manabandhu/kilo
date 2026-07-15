import { create } from "zustand";

interface UiState {
  sidebarOpen: boolean;
  toggleSidebar: () => void;
  setSidebar: (open: boolean) => void;
  bottomSheet: string | null;
  openBottomSheet: (id: string) => void;
  closeBottomSheet: () => void;
}

export const useUiStore = create<UiState>((set) => ({
  sidebarOpen: false,
  toggleSidebar: () => set((s) => ({ sidebarOpen: !s.sidebarOpen })),
  setSidebar: (open) => set({ sidebarOpen: open }),
  bottomSheet: null,
  openBottomSheet: (id) => set({ bottomSheet: id }),
  closeBottomSheet: () => set({ bottomSheet: null }),
}));
