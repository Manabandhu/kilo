import { View } from "react-native";
import { useRouter } from "expo-router";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useAuth } from "../../src/hooks/useAuth";
import { registerSchema, type RegisterInput } from "../../src/lib/validations";
import { Button } from "../../src/components/ui/Button";
import { Input } from "../../src/components/ui/Input";
import { Screen } from "../../src/components/ui/Screen";

export default function RegisterScreen() {
  const { signUp } = useAuth();
  const router = useRouter();
  const {
    setValue,
    watch,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<RegisterInput>({ resolver: zodResolver(registerSchema) });

  const values = watch();

  const onSubmit = async (data: RegisterInput) => {
    try {
      await signUp(data.email, data.password, data.displayName);
      router.replace("/(onboarding)");
    } catch {
      /* surface error */
    }
  };

  return (
    <Screen title="Create your account">
      <View className="gap-3">
        <Input label="Display name" value={values.displayName ?? ""}
          onChangeText={(v) => setValue("displayName", v)} error={errors.displayName?.message} accessibilityLabel="Display name" />
        <Input label="Email" autoCapitalize="none" keyboardType="email-address" value={values.email ?? ""}
          onChangeText={(v) => setValue("email", v)} error={errors.email?.message} accessibilityLabel="Email" />
        <Input label="Password" secureTextEntry value={values.password ?? ""}
          onChangeText={(v) => setValue("password", v)} error={errors.password?.message} accessibilityLabel="Password" />
        <Button loading={isSubmitting} onPress={handleSubmit(onSubmit)} fullWidth accessibilityLabel="Create account">
          Create account
        </Button>
      </View>
    </Screen>
  );
}
