import { View, Text } from "react-native";
import { useRouter } from "expo-router";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useAuth } from "../../src/hooks/useAuth";
import { loginSchema, type LoginInput } from "../../src/lib/validations";
import { Button } from "../../src/components/ui/Button";
import { Input } from "../../src/components/ui/Input";
import { Text as T } from "../../src/components/ui/Text";
import { Screen } from "../../src/components/ui/Screen";

export default function LoginScreen() {
  const { signIn } = useAuth();
  const router = useRouter();
  const {
    register,
    handleSubmit,
    setValue,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<LoginInput>({ resolver: zodResolver(loginSchema) });

  const email = watch("email") ?? "";
  const password = watch("password") ?? "";

  const onSubmit = async (data: LoginInput) => {
    try {
      await signIn(data.email, data.password);
      router.replace("/(app)/(tabs)/home");
    } catch (e) {
      // surface error via a banner in a real build
    }
  };

  return (
    <Screen title="Welcome back">
      <View className="gap-3">
        <Input label="Email" placeholder="you@example.com" autoCapitalize="none" keyboardType="email-address"
          value={email} onChangeText={(v) => setValue("email", v)} error={errors.email?.message} accessibilityLabel="Email" />
        <Input label="Password" placeholder="••••••••" secureTextEntry value={password}
          onChangeText={(v) => setValue("password", v)} error={errors.password?.message} accessibilityLabel="Password" />
        <Button loading={isSubmitting} onPress={handleSubmit(onSubmit)} fullWidth accessibilityLabel="Log in">
          Log in
        </Button>
        <View className="flex-row justify-between">
          <Button variant="ghost" size="sm" onPress={() => router.push("/(auth)/register")} accessibilityLabel="Create account">
            Create account
          </Button>
          <Button variant="ghost" size="sm" onPress={() => router.push("/(auth)/forgot-password")} accessibilityLabel="Forgot password">
            Forgot password?
          </Button>
        </View>
      </View>
    </Screen>
  );
}
