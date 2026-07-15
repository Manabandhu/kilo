package com.manabandhu.backend.common.money;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void ofString_parsesCorrectly() {
        assertEquals(new BigDecimal("10.00"), Money.of("10"));
        assertEquals(new BigDecimal("10.50"), Money.of("10.5"));
        assertEquals(new BigDecimal("10.55"), Money.of("10.55"));
        assertEquals(BigDecimal.ZERO.setScale(2), Money.of(null));
        assertEquals(BigDecimal.ZERO.setScale(2), Money.of(""));
    }

    @Test
    void ofLong_createsWithScale() {
        assertEquals(new BigDecimal("10.00"), Money.of(10L));
    }

    @Test
    void add_subtract_multiply_divide() {
        BigDecimal a = new BigDecimal("10.50");
        BigDecimal b = new BigDecimal("2.25");

        assertEquals(new BigDecimal("12.75"), Money.add(a, b));
        assertEquals(new BigDecimal("8.25"), Money.subtract(a, b));
        assertEquals(new BigDecimal("23.63"), Money.multiply(a, b)); // 10.50 * 2.25 = 23.625 -> 23.63
        assertEquals(new BigDecimal("4.67"), Money.divide(a, b));   // 10.50 / 2.25 = 4.666... -> 4.67
    }

    @Test
    void sum_sumsList() {
        List<BigDecimal> amounts = List.of(
                new BigDecimal("10.00"),
                new BigDecimal("20.50"),
                new BigDecimal("5.25")
        );
        assertEquals(new BigDecimal("35.75"), Money.sum(amounts));
    }

    @Test
    void remainder_validatesSplits() {
        BigDecimal total = new BigDecimal("100.00");
        List<BigDecimal> exact = List.of(new BigDecimal("33.33"), new BigDecimal("33.33"), new BigDecimal("33.34"));
        assertEquals(BigDecimal.ZERO.setScale(2), Money.remainder(total, exact));

        List<BigDecimal> offByOne = List.of(new BigDecimal("33.33"), new BigDecimal("33.33"), new BigDecimal("33.33"));
        assertEquals(new BigDecimal("0.01"), Money.remainder(total, offByOne));
    }
}