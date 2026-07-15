import React from "react";
import { Text, View } from "react-native";
import { Dialog } from "./Dialog";
import { Button } from "./Button";

export interface ConfirmDialogProps {
  visible: boolean;
  title: string;
  message?: string;
  confirmLabel?: string;
  cancelLabel?: string;
  tone?: "primary" | "danger";
  loading?: boolean;
  onConfirm: () => void;
  onCancel: () => void;
}

export const ConfirmDialog: React.FC<ConfirmDialogProps> = ({
  visible,
  title,
  message,
  confirmLabel = "Confirm",
  cancelLabel = "Cancel",
  tone = "primary",
  loading = false,
  onConfirm,
  onCancel,
}) => {
  return (
    <Dialog
      visible={visible}
      onClose={onCancel}
      title={title}
      description={message}
      footer={
        <>
          <Button
            title={cancelLabel}
            variant="ghost"
            onPress={onCancel}
            accessibilityLabel={cancelLabel}
          />
          <Button
            title={confirmLabel}
            variant={tone === "danger" ? "danger" : "primary"}
            onPress={onConfirm}
            loading={loading}
            accessibilityLabel={confirmLabel}
          />
        </>
      }
    />
  );
};
