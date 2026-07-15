package com.manabandhu.backend.expenses.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.common.exception.BusinessException;
import com.manabandhu.backend.common.money.Money;
import com.manabandhu.backend.expenses.api.ExpenseApi;
import com.manabandhu.backend.expenses.api.ExpenseApi.BalanceEntry;
import com.manabandhu.backend.expenses.api.ExpenseApi.BalanceResponse;
import com.manabandhu.backend.expenses.api.ExpenseApi.CreateExpenseRequest;
import com.manabandhu.backend.expenses.api.ExpenseApi.CreateGroupRequest;
import com.manabandhu.backend.expenses.api.ExpenseApi.CreateSettlementRequest;
import com.manabandhu.backend.expenses.api.ExpenseApi.ExpenseGroupResponse;
import com.manabandhu.backend.expenses.api.ExpenseApi.ExpenseResponse;
import com.manabandhu.backend.expenses.api.ExpenseApi.SettlementResponse;
import com.manabandhu.backend.expenses.api.ExpenseApi.SplitRequest;
import com.manabandhu.backend.expenses.infrastructure.ExpenseEntity;
import com.manabandhu.backend.expenses.infrastructure.ExpenseGroupEntity;
import com.manabandhu.backend.expenses.infrastructure.ExpenseGroupMemberEntity;
import com.manabandhu.backend.expenses.infrastructure.ExpenseGroupMemberRepository;
import com.manabandhu.backend.expenses.infrastructure.ExpenseGroupRepository;
import com.manabandhu.backend.expenses.infrastructure.ExpenseRepository;
import com.manabandhu.backend.expenses.infrastructure.SettlementEntity;
import com.manabandhu.backend.expenses.infrastructure.SettlementRepository;
import com.manabandhu.backend.security.Authorization;
import com.manabandhu.backend.security.CurrentUser;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseService {

    private final ExpenseGroupRepository groupRepository;
    private final ExpenseGroupMemberRepository memberRepository;
    private final ExpenseRepository expenseRepository;
    private final SettlementRepository settlementRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExpenseService(ExpenseGroupRepository groupRepository, ExpenseGroupMemberRepository memberRepository,
                          ExpenseRepository expenseRepository, SettlementRepository settlementRepository) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.expenseRepository = expenseRepository;
        this.settlementRepository = settlementRepository;
    }

    @Transactional
    public ExpenseGroupResponse createGroup(CurrentUser user, CreateGroupRequest req) {
        ExpenseGroupEntity g = new ExpenseGroupEntity();
        g.setName(req.name());
        g.setCurrency(req.currency() == null ? "USD" : req.currency());
        g.setCreatedById(user.userId());
        groupRepository.save(g);
        addMember(g.getId(), user.userId());
        if (req.memberIds() != null) {
            for (UUID m : req.memberIds()) addMember(g.getId(), m);
        }
        return ExpenseApi.toResponse(g, memberIds(g.getId()));
    }

    private void addMember(UUID groupId, UUID userId) {
        if (!memberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            ExpenseGroupMemberEntity m = new ExpenseGroupMemberEntity();
            m.setGroupId(groupId);
            m.setUserId(userId);
            memberRepository.save(m);
        }
    }

    public List<ExpenseGroupResponse> myGroups(CurrentUser user) {
        return memberRepository.findAll().stream()
                .filter(m -> m.getUserId().equals(user.userId()))
                .map(m -> groupRepository.findById(m.getGroupId()).orElse(null))
                .filter(java.util.Objects::nonNull)
                .map(g -> ExpenseApi.toResponse(g, memberIds(g.getId())))
                .toList();
    }

    public ExpenseGroupResponse getGroup(CurrentUser user, UUID groupId) {
        requireMember(groupId, user.userId());
        return ExpenseApi.toResponse(requireGroup(groupId), memberIds(groupId));
    }

    @Transactional
    public ExpenseResponse addExpense(CurrentUser user, UUID groupId, CreateExpenseRequest req) {
        requireMember(groupId, user.userId());
        BigDecimal total = Money.of(req.totalAmount());
        List<SplitRequest> splits = req.splits() == null ? List.of() : List.of(req.splits());
        if (splits.isEmpty()) {
            throw BusinessException.validation("At least one split is required", null);
        }
        validateSplits(req.splitStrategy(), total, splits);
        ExpenseEntity e = new ExpenseEntity();
        e.setGroupId(groupId);
        e.setTitle(req.title());
        e.setDescription(req.description());
        e.setTotalAmount(total);
        e.setCurrency(req.currency() == null ? requireGroup(groupId).getCurrency() : req.currency());
        e.setPaidByIds(req.paidByIds());
        e.setSplitStrategy(req.splitStrategy());
        try {
            e.setSplits(objectMapper.writeValueAsString(splits));
        } catch (Exception ex) {
            throw BusinessException.businessRule("Invalid split payload");
        }
        e.setDate(req.date());
        e.setCategory(req.category());
        e.setReceiptUrl(req.receiptUrl());
        e.setCreatedById(user.userId());
        return ExpenseApi.toResponse(expenseRepository.save(e));
    }

    @Transactional
    public SettlementResponse recordSettlement(CurrentUser user, UUID groupId, CreateSettlementRequest req) {
        requireMember(groupId, user.userId());
        BigDecimal amount = Money.of(req.amount());
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw BusinessException.validation("Settlement amount must be positive", null);
        }
        SettlementEntity s = new SettlementEntity();
        s.setGroupId(groupId);
        s.setFromUserId(req.fromUserId());
        s.setToUserId(req.toUserId());
        s.setAmount(amount);
        s.setCurrency(requireGroup(groupId).getCurrency());
        s.setStatus("pending");
        s.setNote(req.note());
        s.setRecordedById(user.userId());
        return ExpenseApi.toResponse(settlementRepository.save(s));
    }

    public BalanceResponse balances(CurrentUser user, UUID groupId) {
        requireMember(groupId, user.userId());
        Map<UUID, BigDecimal> net = new LinkedHashMap<>();
        for (UUID m : memberIds(groupId)) net.put(m, BigDecimal.ZERO.setScale(2));
        for (ExpenseEntity e : expenseRepository.findByGroupId(groupId)) {
            List<SplitRequest> splits = parseSplits(e.getSplits());
            BigDecimal total = e.getTotalAmount();
            for (SplitRequest s : splits) {
                BigDecimal owed = owedFor(e.getSplitStrategy(), total, s, splits);
                net.merge(s.userId(), owed.negate(), BigDecimal::add);
            }
            if (e.getPaidByIds() != null) {
                BigDecimal perPayer = total.divide(BigDecimal.valueOf(e.getPaidByIds().length), 2, java.math.RoundingMode.HALF_UP);
                for (UUID p : e.getPaidByIds()) net.merge(p, perPayer, BigDecimal::add);
            }
        }
        for (SettlementEntity s : settlementRepository.findByGroupId(groupId)) {
            if ("completed".equals(s.getStatus())) {
                net.merge(s.getFromUserId(), s.getAmount(), BigDecimal::add);
                net.merge(s.getToUserId(), s.getAmount().negate(), BigDecimal::add);
            }
        }
        BalanceEntry[] entries = net.entrySet().stream()
                .map(en -> new BalanceEntry(en.getKey(), en.getValue().setScale(2).toPlainString()))
                .toArray(BalanceEntry[]::new);
        return new BalanceResponse(groupId, requireGroup(groupId).getCurrency(), entries);
    }

    private void validateSplits(String strategy, BigDecimal total, List<SplitRequest> splits) {
        List<BigDecimal> parts = new ArrayList<>();
        if ("exact".equals(strategy) || "equal".equals(strategy)) {
            for (SplitRequest s : splits) parts.add(Money.of(s.amount()));
            if (!"equal".equals(strategy) && Money.remainder(total, parts).abs().compareTo(new BigDecimal("0.01")) > 0) {
                throw BusinessException.validation("Split amounts must equal the total", null);
            }
        } else if ("percentage".equals(strategy)) {
            int sum = splits.stream().mapToInt(SplitRequest::percent).sum();
            if (sum != 100) throw BusinessException.validation("Percentages must sum to 100", null);
        } else if ("shares".equals(strategy)) {
            int sum = splits.stream().mapToInt(SplitRequest::shares).sum();
            if (sum <= 0) throw BusinessException.validation("Shares must be positive", null);
        } else {
            throw BusinessException.validation("Unknown split strategy", null);
        }
    }

    private BigDecimal owedFor(String strategy, BigDecimal total, SplitRequest s, List<SplitRequest> splits) {
        return switch (strategy) {
            case "exact", "equal" -> Money.of(s.amount());
            case "percentage" -> total.multiply(BigDecimal.valueOf(s.percent()))
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            case "shares" -> {
                int totalShares = splits.stream().mapToInt(SplitRequest::shares).sum();
                yield total.multiply(BigDecimal.valueOf(s.shares()))
                        .divide(BigDecimal.valueOf(totalShares), 2, java.math.RoundingMode.HALF_UP);
            }
            default -> BigDecimal.ZERO.setScale(2);
        };
    }

    private List<SplitRequest> parseSplits(String json) {
        try {
            List<SplitRequest> out = new ArrayList<>();
            JsonNode node = objectMapper.readTree(json);
            for (JsonNode n : node) {
                out.add(new SplitRequest(
                        UUID.fromString(n.get("userId").asText()),
                        n.has("amount") ? n.get("amount").asText() : null,
                        n.has("percent") ? n.get("percent").asInt() : null,
                        n.has("shares") ? n.get("shares").asInt() : null));
            }
            return out;
        } catch (Exception ex) {
            throw BusinessException.businessRule("Corrupt split data");
        }
    }

    private UUID[] memberIds(UUID groupId) {
        return memberRepository.findAll().stream()
                .filter(m -> m.getGroupId().equals(groupId))
                .map(ExpenseGroupMemberEntity::getUserId).toArray(UUID[]::new);
    }

    private void requireMember(UUID groupId, UUID userId) {
        if (!memberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw BusinessException.forbidden("You are not a member of this group");
        }
    }

    private ExpenseGroupEntity requireGroup(UUID id) {
        return groupRepository.findById(id).orElseThrow(() -> BusinessException.notFound("Group not found"));
    }
}
