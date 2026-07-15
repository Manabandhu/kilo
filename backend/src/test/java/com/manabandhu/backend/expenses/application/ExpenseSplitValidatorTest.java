package com.manabandhu.backend.expenses.application;

import com.manabandhu.backend.common.exception.BusinessException;
import com.manabandhu.backend.common.money.Money;
import com.manabandhu.backend.expenses.api.ExpenseApi.SplitRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseSplitValidatorTest {

    @Test
    void equalStrategy_validatesTotal() {
        BigDecimal total = new BigDecimal("100.00");
        List<SplitRequest> splits = List.of(
                new SplitRequest("u1", "33.33", null, null),
                new SplitRequest("u2", "33.33", null, null),
                new SplitRequest("u3", "33.34", null, null)
        );
        assertDoesNotThrow(() -> ExpenseSplitValidator.validateSplits("equal", total, splits));
    }

    @Test
    void equalStrategy_rejectsMismatch() {
        BigDecimal total = new BigDecimal("100.00");
        List<SplitRequest> splits = List.of(
                new SplitRequest("u1", "33.33", null, null),
                new SplitRequest("u2", "33.33", null, null),
                new SplitRequest("u3", "33.33", null, null)
        );
        assertThrows(BusinessException.class, () -> ExpenseSplitValidator.validateSplits("equal", total, splits));
    }

    @Test
    void exactStrategy_validatesSum() {
        BigDecimal total = new BigDecimal("100.00");
        List<SplitRequest> splits = List.of(
                new SplitRequest("u1", "40.00", null, null),
                new SplitRequest("u2", "60.00", null, null)
        );
        assertDoesNotThrow(() -> ExpenseSplitValidator.validateSplits("exact", total, splits));
    }

    @Test
    void exactStrategy_rejectsMismatch() {
        BigDecimal total = new BigDecimal("100.00");
        List<SplitRequest> splits = List.of(
                new SplitRequest("u1", "40.00", null, null),
                new SplitRequest("u2", "50.00", null, null)
        );
        assertThrows(BusinessException.class, () -> ExpenseSplitValidator.validateSplits("exact", total, splits));
    }

    @Test
    void percentageStrategy_validatesSum100() {
        BigDecimal total = new BigDecimal("100.00");
        List<SplitRequest> splits = List.of(
                new SplitRequest("u1", null, 50, null),
                new SplitRequest("u2", null, 50, null)
        );
        assertDoesNotThrow(() -> ExpenseSplitValidator.validateSplits("percentage", total, splits));
    }

    @Test
    void percentageStrategy_rejectsNon100() {
        BigDecimal total = new BigDecimal("100.00");
        List<SplitRequest> splits = List.of(
                new SplitRequest("u1", null, 60, null),
                new SplitRequest("u2", null, 50, null)
        );
        assertThrows(BusinessException.class, () -> ExpenseSplitValidator.validateSplits("percentage", total, splits));
    }

    @Test
    void sharesStrategy_validatesPositiveShares() {
        BigDecimal total = new BigDecimal("100.00");
        List<SplitRequest> splits = List.of(
                new SplitRequest("u1", null, null, 2),
                new SplitRequest("u2", null, null, 3)
        );
        assertDoesNotThrow(() -> ExpenseSplitValidator.validateSplits("shares", total, splits));
    }

    @Test
    void sharesStrategy_rejectsZeroTotalShares() {
        BigDecimal total = new BigDecimal("100.00");
        List<SplitRequest> splits = List.of(
                new SplitRequest("u1", null, null, 0),
                new SplitRequest("u2", null, null, 0)
        );
        assertThrows(BusinessException.class, () -> ExpenseSplitValidator.validateSplits("shares", total, splits));
    }

    @Test
    void unknownStrategy_rejected() {
        BigDecimal total = new BigDecimal("100.00");
        List<SplitRequest> splits = List.of(
                new SplitRequest("u1", "100.00", null, null)
        );
        assertThrows(BusinessException.class, () -> ExpenseSplitValidator.validateSplits("unknown", total, splits));
    }
}