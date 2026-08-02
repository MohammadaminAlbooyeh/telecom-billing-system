package com.tbs.rating;

import com.tbs.model.entity.Plan;
import com.tbs.model.entity.UsageRecord;
import com.tbs.model.enums.UsageType;

import java.math.BigDecimal;

public class TieredRatingStrategy implements RatingStrategy {

    private final UsageType usageType;

    public TieredRatingStrategy(UsageType usageType) {
        this.usageType = usageType;
    }

    @Override
    public RateResult rate(Plan plan, UsageRecord usage, BigDecimal usedInCycle) {
        BigDecimal units = extractUnits(usage);
        BigDecimal allowance = BigDecimal.valueOf(extractAllowance(plan));
        BigDecimal remaining = allowance.subtract(usedInCycle).max(BigDecimal.ZERO);
        BigDecimal inAllowance = units.min(remaining);
        BigDecimal overageUnits = units.subtract(inAllowance);
        BigDecimal unitPrice = extractUnitPrice(plan);
        BigDecimal baseCharge = unitPrice.multiply(inAllowance);
        BigDecimal overageCharge = unitPrice.multiply(overageUnits);
        return RateResult.of(baseCharge, overageCharge);
    }

    private BigDecimal extractUnits(UsageRecord usage) {
        return switch (usageType) {
            case CALL -> BigDecimal.valueOf(usage.getDurationMinutes());
            case DATA -> usage.getDataUsedMb();
            case SMS -> BigDecimal.valueOf(usage.getSmsCount());
        };
    }

    private int extractAllowance(Plan plan) {
        return switch (usageType) {
            case CALL -> plan.getIncludedMinutes();
            case DATA -> plan.getIncludedDataMb();
            case SMS -> plan.getIncludedSms();
        };
    }

    private BigDecimal extractUnitPrice(Plan plan) {
        return switch (usageType) {
            case CALL -> plan.getCallRatePerMinute();
            case DATA -> plan.getDataRatePerMb();
            case SMS -> plan.getSmsRate();
        };
    }
}
