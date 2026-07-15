import React from "react";
import { View, Text, Pressable, AccessibilityInfo } from "react-native";

interface Props {
  children: React.ReactNode;
}
interface State {
  hasError: boolean;
  message?: string;
}

export class ErrorBoundary extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, message: error.message };
  }

  componentDidCatch(error: Error, info: React.ErrorInfo) {
    // Do not log PII; forward to error reporting in production.
    if (__DEV__) {
      // eslint-disable-next-line no-console
      console.error("Unhandled UI error", error, info);
    }
  }

  reset = () => this.setState({ hasError: false, message: undefined });

  render() {
    if (this.state.hasError) {
      return (
        <View
          className="flex-1 items-center justify-center bg-background px-6"
          accessibilityRole="alert"
        >
          <Text className="text-lg font-bold text-text">Something went wrong</Text>
          <Text className="mt-2 text-center text-sm text-muted">
            We’re sorry — an unexpected error occurred. You can try again.
          </Text>
          <Pressable
            onPress={this.reset}
            className="mt-4 rounded-md bg-primary px-5 py-2.5"
            accessibilityRole="button"
            accessibilityLabel="Retry"
          >
            <Text className="font-semibold text-text-inverse">Retry</Text>
          </Pressable>
        </View>
      );
    }
    return this.props.children;
  }
}
