import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useRef,
  useState,
} from "react";
import {
  Animated,
  Easing,
  Pressable,
  Text,
  View,
  useReducedMotion,
} from "react-native";
import { cn } from "@/lib/cn";
import { useTheme } from "@/theme/ThemeProvider";

export type ToastTone = "success" | "error" | "info" | "warning";

export interface ToastOptions {
  title: string;
  description?: string;
  tone?: ToastTone;
  duration?: number;
  actionLabel?: string;
  onAction?: () => void;
}

interface ToastItem extends ToastOptions {
  id: string;
  tone: ToastTone;
  duration: number;
}

interface ToastContextValue {
  show: (options: ToastOptions) => string;
  success: (title: string, description?: string) => string;
  error: (title: string, description?: string) => string;
  dismiss: (id: string) => void;
}

const ToastContext = createContext<ToastContextValue | null>(null);

let counter = 0;
const nextId = (): string => `toast-${counter++}`;

export const ToastProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [toasts, setToasts] = useState<ToastItem[]>([]);
  const timers = useRef<Map<string, ReturnType<typeof setTimeout>>>(new Map());

  const dismiss = useCallback((id: string) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
    const timer = timers.current.get(id);
    if (timer) {
      clearTimeout(timer);
      timers.current.delete(id);
    }
  }, []);

  const show = useCallback(
    (options: ToastOptions): string => {
      const id = nextId();
      const item: ToastItem = {
        id,
        tone: options.tone ?? "info",
        duration: options.duration ?? 3500,
        ...options,
      };
      setToasts((prev) => [...prev, item]);
      const timer = setTimeout(() => dismiss(id), item.duration);
      timers.current.set(id, timer);
      return id;
    },
    [dismiss],
  );

  const success = useCallback(
    (title: string, description?: string) => show({ title, description, tone: "success" }),
    [show],
  );
  const error = useCallback(
    (title: string, description?: string) => show({ title, description, tone: "error" }),
    [show],
  );

  useEffect(() => {
    const map = timers.current;
    return () => {
      map.forEach((t) => clearTimeout(t));
      map.clear();
    };
  }, []);

  return (
    <ToastContext.Provider value={{ show, success, error, dismiss }}>
      {children}
      <ToastViewport toasts={toasts} onDismiss={dismiss} />
    </ToastContext.Provider>
  );
};

const ToastViewport: React.FC<{
  toasts: ToastItem[];
  onDismiss: (id: string) => void;
}> = ({ toasts, onDismiss }) => {
  return (
    <View
      className="absolute inset-x-0 bottom-0 z-[1400] px-4 pb-6"
      style={{ pointerEvents: "box-none" }}
      accessibilityLiveRegion="polite"
    >
      {toasts.map((toast) => (
        <ToastCard key={toast.id} toast={toast} onDismiss={onDismiss} />
      ))}
    </View>
  );
};

const ToastCard: React.FC<{ toast: ToastItem; onDismiss: (id: string) => void }> = ({
  toast,
  onDismiss,
}) => {
  const { colors } = useTheme();
  const reduced = useReducedMotion();
  const translate = useRef(new Animated.Value(reduced ? 0 : 24)).current;
  const opacity = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    Animated.parallel([
      Animated.timing(opacity, {
        toValue: 1,
        duration: reduced ? 0 : 200,
        easing: Easing.out(Easing.ease),
        useNativeDriver: true,
      }),
      Animated.timing(translate, {
        toValue: 0,
        duration: reduced ? 0 : 240,
        easing: Easing.out(Easing.ease),
        useNativeDriver: true,
      }),
    ]).start();
  }, [opacity, translate, reduced]);

  const accent =
    toast.tone === "success"
      ? colors.success
      : toast.tone === "error"
        ? colors.danger
        : toast.tone === "warning"
          ? colors.warning
          : colors.primary;

  return (
    <Animated.View
      className="mb-2 overflow-hidden rounded-lg border p-3 shadow-sm"
      style={{
        opacity,
        transform: [{ translateY: translate }],
        backgroundColor: colors.surface,
        borderColor: colors.border,
      }}
      accessibilityRole="alert"
    >
      <View className="flex-row items-start gap-3">
        <View className="mt-1 h-2 w-2 rounded-full" style={{ backgroundColor: accent }} />
        <View className="flex-1">
          <Text className="text-sm font-semibold" style={{ color: colors.text }}>
            {toast.title}
          </Text>
          {toast.description ? (
            <Text className="mt-0.5 text-xs" style={{ color: colors.textMuted }}>
              {toast.description}
            </Text>
          ) : null}
          {toast.actionLabel ? (
            <Pressable
              accessibilityRole="button"
              accessibilityLabel={toast.actionLabel}
              className="mt-2"
              onPress={() => {
                toast.onAction?.();
                onDismiss(toast.id);
              }}
            >
              <Text className="text-xs font-semibold" style={{ color: colors.primary }}>
                {toast.actionLabel}
              </Text>
            </Pressable>
          ) : null}
        </View>
        <Pressable
          accessibilityRole="button"
          accessibilityLabel="Dismiss notification"
          hitSlop={8}
          onPress={() => onDismiss(toast.id)}
        >
          <Text className="text-xs" style={{ color: colors.textMuted }}>
            ✕
          </Text>
        </Pressable>
      </View>
    </Animated.View>
  );
};

export function useToast(): ToastContextValue {
  const ctx = useContext(ToastContext);
  if (!ctx) {
    throw new Error("useToast must be used within a ToastProvider");
  }
  return ctx;
}
