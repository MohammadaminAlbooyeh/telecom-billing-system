package com.tbs.rating;

import com.tbs.model.entity.Plan;
import com.tbs.model.entity.UsageRecord;

import java.math.BigDecimal;

public interface RatingStrategy {

    RateResult rate(Plan plan, UsageRecord usage, BigDecimal usedInCycle);
}
