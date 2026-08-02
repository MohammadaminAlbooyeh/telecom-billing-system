package com.tbs.rating;

import com.tbs.model.entity.Plan;
import com.tbs.model.entity.UsageRecord;

import java.math.BigDecimal;

public class DataRatingStrategy implements RatingStrategy {

    @Override
    public RateResult rate(Plan plan, UsageRecord usage, BigDecimal usedInCycle) {
        BigDecimal total = usage.getDataUsedMb().multiply(plan.getDataRatePerMb());
        return RateResult.flat(total);
    }
}
