package com.tbs.rating;

import com.tbs.model.entity.Plan;
import com.tbs.model.entity.UsageRecord;

import java.math.BigDecimal;

public class SmsRatingStrategy implements RatingStrategy {

    @Override
    public RateResult rate(Plan plan, UsageRecord usage, BigDecimal usedInCycle) {
        BigDecimal total = BigDecimal.valueOf(usage.getSmsCount())
                .multiply(plan.getSmsRate());
        return RateResult.flat(total);
    }
}
