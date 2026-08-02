package com.tbs.rating;

import java.math.BigDecimal;

public record RateResult(BigDecimal baseCharge, BigDecimal overageCharge, BigDecimal totalCharge) {

    public static RateResult of(BigDecimal baseCharge, BigDecimal overageCharge) {
        return new RateResult(baseCharge, overageCharge, baseCharge.add(overageCharge));
    }

    public static RateResult flat(BigDecimal totalCharge) {
        return new RateResult(totalCharge, BigDecimal.ZERO, totalCharge);
    }
}
