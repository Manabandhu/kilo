import { View, useState } from "react-native";
import { useRouter } from "expo-router";
import { useAuth } from "../../src/hooks/useAuth";
import { Button } from "../../src/components/ui/Button";
import { Input } from "../../src/components/ui/Input";
import { Screen } from "../../src/components/ui/Screen";
import { Text as T } from "../../src/components/ui/Text";

export default function ForgotPasswordScreen() {
  const { resetPassword } = useAuth();
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [sent, setSent] = useState(false);

  const onSubmit = async () => {
    await resetPassword(email);
    setSent(true);
  };

  return (
    <Screen title="Reset password">
      <View className="gap-3">
        <T className="text-sm text-muted">We’ll send a reset link to your email.</T>
        <Input label="Email" autoCapitalize="none" keyboardType="email-address" value={email}
          onChangeText={setEmail} accessibilityLabel="Email" />
        <Button onPress={onSubmit} fullWidth accessibilityLabel="Send reset link">
          Send reset link
        </Button>
        {sent ? <T className="text-center text-sm text-success">Check your inbox.</T> : null}
        <Button variant="ghost" size="sm" onPress={() => router.replace("/(auth)/login")} accessibilityLabel="Back to login">
          Back to login
        </Button>
      </View>
    </Screen>
  );
}
