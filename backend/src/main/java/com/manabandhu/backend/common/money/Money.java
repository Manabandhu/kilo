package com.manabandhu.backend.common.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Deterministic decimal money arithmetic. Never use floating point (double/float)
 * for currency. All values are stored as strings in API contracts and converted here.
 */
public final class Money {

    public static final int SCALE = 2;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private Money() {
    }

    public static BigDecimal of(String amount) {
        if (amount == null || amount.isBlank()) {
            return BigDecimal.ZERO.setScale(SCALE);
        }
        return new BigDecimal(amount).setScale(SCALE, ROUNDING);
    }

    public static BigDecimal of(long amount) {
        return BigDecimal.valueOf(amount).setScale(SCALE, ROUNDING);
    }

    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        return a.add(b).setScale(SCALE, ROUNDING);
    }

    public static BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return a.subtract(b).setScale(SCALE, ROUNDING);
    }

    public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return a.multiply(b).setScale(SCALE, ROUNDING);
    }

    public static BigDecimal divide(BigDecimal a, BigDecimal b) {
        return a.divide(b, SCALE, ROUNDING);
    }

    /** Sum a list of amounts. */
    public static BigDecimal sum(List<BigDecimal> amounts) {
        BigDecimal total = BigDecimal.ZERO.setScale(SCALE);
        for (BigDecimal a : amounts) {
            total = total.add(a);
        }
        return total;
    }

    /**
     * Validate that the parts of a split equal the total within rounding tolerance.
     * Returns the remainder (total - parts) which should be zero for valid splits.
     */
    public static BigDecimal remainder(BigDecimal total, List<BigDecimal> parts) {
        return total.subtract(sum(parts)).setScale(SCALE, ROUNDING);
    }
}
