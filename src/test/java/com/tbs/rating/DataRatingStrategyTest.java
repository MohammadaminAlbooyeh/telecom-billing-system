package com.tbs.rating;

import com.tbs.model.entity.Plan;
import com.tbs.model.entity.UsageRecord;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class DataRatingStrategyTest {

    private final DataRatingStrategy strategy = new DataRatingStrategy();

    @Test
    void rateComputesTotalFromDataAndRate() {
        Plan plan = planWithRate("1.2500");
        UsageRecord usage = usageWithData("10.00");

        RateResult result = strategy.rate(plan, usage, BigDecimal.ZERO);

        assertThat(result.totalCharge()).isEqualByComparingTo("12.50");
        assertThat(result.baseCharge()).isEqualByComparingTo("12.50");
        assertThat(result.overageCharge()).isEqualByComparingTo("0");
    }

    @Test
    void rateIgnoresUsedInCycleForFlatPricing() {
        Plan plan = planWithRate("1.2500");
        UsageRecord usage = usageWithData("10.00");

        RateResult result = strategy.rate(plan, usage, BigDecimal.valueOf(500));

        assertThat(result.totalCharge()).isEqualByComparingTo("12.50");
    }

    private Plan planWithRate(String rate) {
        Plan plan = new Plan();
        plan.setDataRatePerMb(new BigDecimal(rate));
        return plan;
    }

    private UsageRecord usageWithData(String mb) {
        UsageRecord usage = new UsageRecord();
        usage.setDataUsedMb(new BigDecimal(mb));
        return usage;
    }
}
