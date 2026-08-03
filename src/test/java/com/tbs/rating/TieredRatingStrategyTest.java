package com.tbs.rating;

import com.tbs.model.entity.Plan;
import com.tbs.model.entity.UsageRecord;
import com.tbs.model.enums.UsageType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TieredRatingStrategyTest {

    @Test
    void callWithinAllowanceHasNoOverage() {
        TieredRatingStrategy strategy = new TieredRatingStrategy(UsageType.CALL);
        UsageRecord usage = usage();
        usage.setDurationMinutes(40);

        RateResult result = strategy.rate(plan(), usage, BigDecimal.ZERO);

        assertThat(result.baseCharge()).isEqualByComparingTo("20");   // 40 * 0.5
        assertThat(result.overageCharge()).isEqualByComparingTo("0");
        assertThat(result.totalCharge()).isEqualByComparingTo("20");
    }

    @Test
    void callPartialOverageUsesRemainingAllowance() {
        TieredRatingStrategy strategy = new TieredRatingStrategy(UsageType.CALL);
        UsageRecord usage = usage();
        usage.setDurationMinutes(40);

        // 70 of the 100-minute allowance already used -> only 30 in allowance, 10 over
        RateResult result = strategy.rate(plan(), usage, BigDecimal.valueOf(70));

        assertThat(result.baseCharge()).isEqualByComparingTo("15");   // 30 * 0.5
        assertThat(result.overageCharge()).isEqualByComparingTo("5"); // 10 * 0.5
        assertThat(result.totalCharge()).isEqualByComparingTo("20");
    }

    @Test
    void dataFullyOverAllowanceIsAllOverage() {
        TieredRatingStrategy strategy = new TieredRatingStrategy(UsageType.DATA);
        UsageRecord usage = usage();
        usage.setDataUsedMb(new BigDecimal("250.00"));

        // allowance 100 MB fully consumed by prior use -> 0 in allowance
        RateResult result = strategy.rate(plan(), usage, BigDecimal.valueOf(200));

        assertThat(result.baseCharge()).isEqualByComparingTo("0");
        assertThat(result.overageCharge()).isEqualByComparingTo("312.50"); // 250 * 1.25
        assertThat(result.totalCharge()).isEqualByComparingTo("312.50");
    }

    @Test
    void dataWithinAllowanceHasNoOverage() {
        TieredRatingStrategy strategy = new TieredRatingStrategy(UsageType.DATA);
        UsageRecord usage = usage();
        usage.setDataUsedMb(new BigDecimal("40.00"));

        RateResult result = strategy.rate(plan(), usage, BigDecimal.ZERO);

        assertThat(result.baseCharge()).isEqualByComparingTo("50.00");  // 40 * 1.25
        assertThat(result.overageCharge()).isEqualByComparingTo("0");
    }

    @Test
    void smsWithinAllowance() {
        TieredRatingStrategy strategy = new TieredRatingStrategy(UsageType.SMS);
        UsageRecord usage = usage();
        usage.setSmsCount(5);

        RateResult result = strategy.rate(plan(), usage, BigDecimal.ZERO);

        assertThat(result.totalCharge()).isEqualByComparingTo("0.50"); // 5 * 0.10
        assertThat(result.overageCharge()).isEqualByComparingTo("0");
    }

    private Plan plan() {
        Plan plan = new Plan();
        plan.setIncludedMinutes(100);
        plan.setIncludedDataMb(100);
        plan.setIncludedSms(10);
        plan.setCallRatePerMinute(new BigDecimal("0.5"));
        plan.setDataRatePerMb(new BigDecimal("1.25"));
        plan.setSmsRate(new BigDecimal("0.10"));
        return plan;
    }

    private UsageRecord usage() {
        return new UsageRecord();
    }
}

