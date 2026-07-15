package com.manabandhu.backend.expenses.api;

import com.manabandhu.backend.expenses.infrastructure.ExpenseEntity;
import com.manabandhu.backend.expenses.infrastructure.ExpenseGroupEntity;
import com.manabandhu.backend.expenses.infrastructure.SettlementEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class ExpenseApi {

    private ExpenseApi() {
    }

    public record ExpenseGroupResponse(UUID id, String name, String currency, UUID[] memberIds, UUID createdById, String createdAt) {
    }

    public record CreateGroupRequest(String name, String currency, UUID[] memberIds) {
    }

    public record ExpenseResponse(
            UUID id, UUID groupId, String title, String description, String totalAmount, String currency,
            UUID[] paidByIds, String splitStrategy, String splits, LocalDate date, String category,
            String receiptUrl, UUID createdById) {
    }

    public record SplitRequest(UUID userId, String amount, Integer percent, Integer shares) {
    }

    public record CreateExpenseRequest(
            String title, String description, String totalAmount, String currency, UUID[] paidByIds,
            String splitStrategy, SplitRequest[] splits, LocalDate date, String category, String receiptUrl) {
    }

    public record SettlementResponse(
            UUID id, UUID groupId, UUID fromUserId, UUID toUserId, String amount, String currency, String status, String note) {
    }

    public record CreateSettlementRequest(UUID fromUserId, UUID toUserId, String amount, String note) {
    }

    public record BalanceResponse(UUID groupId, String currency, BalanceEntry[] balances) {
    }

    public record BalanceEntry(UUID userId, String net) {
    }

    public static ExpenseGroupResponse toResponse(ExpenseGroupEntity e, UUID[] memberIds) {
        return new ExpenseGroupResponse(e.getId(), e.getName(), e.getCurrency(), memberIds, e.getCreatedById(), e.getCreatedAt().toString());
    }

    public static ExpenseResponse toResponse(ExpenseEntity e) {
        return new ExpenseResponse(e.getId(), e.getGroupId(), e.getTitle(), e.getDescription(),
                e.getTotalAmount().toPlainString(), e.getCurrency(), e.getPaidByIds(), e.getSplitStrategy(),
                e.getSplits(), e.getDate(), e.getCategory(), e.getReceiptUrl(), e.getCreatedById());
    }

    public static SettlementResponse toResponse(SettlementEntity e) {
        return new SettlementResponse(e.getId(), e.getGroupId(), e.getFromUserId(), e.getToUserId(),
                e.getAmount().toPlainString(), e.getCurrency(), e.getStatus(), e.getNote());
    }
}
