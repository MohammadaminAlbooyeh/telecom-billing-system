package com.tbs.rating;

import com.tbs.model.entity.Plan;
import com.tbs.model.entity.UsageRecord;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CallRatingStrategyTest {

    private final CallRatingStrategy strategy = new CallRatingStrategy();

    @Test
    void rateComputesTotalFromDurationAndRate() {
        Plan plan = planWithRate("0.5000");
        UsageRecord usage = usageWithDuration(10);

        RateResult result = strategy.rate(plan, usage, BigDecimal.ZERO);

        assertThat(result.totalCharge()).isEqualByComparingTo("5.0000");
        assertThat(result.baseCharge()).isEqualByComparingTo("5.0000");
        assertThat(result.overageCharge()).isEqualByComparingTo("0");
    }

    @Test
    void rateOfZeroDurationIsZero() {
        Plan plan = planWithRate("0.5000");
        UsageRecord usage = usageWithDuration(0);

        RateResult result = strategy.rate(plan, usage, BigDecimal.ZERO);

        assertThat(result.totalCharge()).isEqualByComparingTo("0");
        assertThat(result.overageCharge()).isEqualByComparingTo("0");
    }

    private Plan planWithRate(String rate) {
        Plan plan = new Plan();
        plan.setCallRatePerMinute(new BigDecimal(rate));
        return plan;
    }

    private UsageRecord usageWithDuration(int minutes) {
        UsageRecord usage = new UsageRecord();
        usage.setDurationMinutes(minutes);
        return usage;
    }
}

