package com.manabandhu.backend.expenses.api;

import com.manabandhu.backend.expenses.api.ExpenseApi.BalanceResponse;
import com.manabandhu.backend.expenses.api.ExpenseApi.CreateExpenseRequest;
import com.manabandhu.backend.expenses.api.ExpenseApi.CreateGroupRequest;
import com.manabandhu.backend.expenses.api.ExpenseApi.CreateSettlementRequest;
import com.manabandhu.backend.expenses.api.ExpenseApi.ExpenseGroupResponse;
import com.manabandhu.backend.expenses.api.ExpenseApi.ExpenseResponse;
import com.manabandhu.backend.expenses.api.ExpenseApi.SettlementResponse;
import com.manabandhu.backend.expenses.application.ExpenseService;
import com.manabandhu.backend.security.AuthenticatedUser;
import com.manabandhu.backend.security.CurrentUser;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/expense-groups")
public class ExpenseController {

    private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    @PostMapping
    public ExpenseGroupResponse create(@AuthenticatedUser CurrentUser user, @Valid @RequestBody CreateGroupRequest req) {
        return service.createGroup(user, req);
    }

    @GetMapping
    public List<ExpenseGroupResponse> mine(@AuthenticatedUser CurrentUser user) {
        return service.myGroups(user);
    }

    @GetMapping("/{id}")
    public ExpenseGroupResponse get(@AuthenticatedUser CurrentUser user, @PathVariable UUID id) {
        return service.getGroup(user, id);
    }

    @PostMapping("/{id}/expenses")
    public ExpenseResponse add(@AuthenticatedUser CurrentUser user, @PathVariable UUID id, @Valid @RequestBody CreateExpenseRequest req) {
        return service.addExpense(user, id, req);
    }

    @PostMapping("/{id}/settlements")
    public SettlementResponse settle(@AuthenticatedUser CurrentUser user, @PathVariable UUID id, @Valid @RequestBody CreateSettlementRequest req) {
        return service.recordSettlement(user, id, req);
    }

    @GetMapping("/{id}/balances")
    public BalanceResponse balances(@AuthenticatedUser CurrentUser user, @PathVariable UUID id) {
        return service.balances(user, id);
    }
}
