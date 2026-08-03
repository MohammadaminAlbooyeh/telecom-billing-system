package com.tbs.rating;

import com.tbs.model.entity.Plan;
import com.tbs.model.entity.UsageRecord;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class SmsRatingStrategyTest {

    private final SmsRatingStrategy strategy = new SmsRatingStrategy();

    @Test
    void rateComputesTotalFromSmsCountAndRate() {
        Plan plan = planWithRate("0.1000");
        UsageRecord usage = usageWithSms(3);

        RateResult result = strategy.rate(plan, usage, BigDecimal.ZERO);

        assertThat(result.totalCharge()).isEqualByComparingTo("0.30");
        assertThat(result.baseCharge()).isEqualByComparingTo("0.30");
        assertThat(result.overageCharge()).isEqualByComparingTo("0");
    }

    private Plan planWithRate(String rate) {
        Plan plan = new Plan();
        plan.setSmsRate(new BigDecimal(rate));
        return plan;
    }

    private UsageRecord usageWithSms(int count) {
        UsageRecord usage = new UsageRecord();
        usage.setSmsCount(count);
        return usage;
    }
}
