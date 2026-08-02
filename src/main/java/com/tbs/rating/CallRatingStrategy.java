package com.tbs.rating;

import com.tbs.model.entity.Plan;
import com.tbs.model.entity.UsageRecord;

import java.math.BigDecimal;

public class CallRatingStrategy implements RatingStrategy {

    @Override
    public RateResult rate(Plan plan, UsageRecord usage, BigDecimal usedInCycle) {
        BigDecimal total = BigDecimal.valueOf(usage.getDurationMinutes())
                .multiply(plan.getCallRatePerMinute());
        return RateResult.flat(total);
    }
}
