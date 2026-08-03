package com.tbs.rating;

import com.tbs.model.entity.Plan;
import com.tbs.model.entity.RatedUsage;
import com.tbs.model.entity.Subscriber;
import com.tbs.model.entity.UsageRecord;
import com.tbs.model.enums.UsageType;
import com.tbs.repository.UsageRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingEngineTest {

    @Mock
    private RatingStrategyFactory strategyFactory;

    @Mock
    private UsageRecordRepository usageRecordRepository;

    @InjectMocks
    private RatingEngine engine;

    @Test
    void rateComputesChargesWithFlatStrategy() {
        Plan plan = plan();
        Subscriber subscriber = subscriberWithPlan(1L, plan);
        UsageRecord usage = usage(subscriber, UsageType.CALL);
        usage.setDurationMinutes(30);

        when(usageRecordRepository.findBySubscriberIdAndRatedTrueAndUsageStartTimeBetween(
                eq(1L), any(), any())).thenReturn(List.of());
        when(strategyFactory.getStrategy(plan, UsageType.CALL)).thenReturn(new CallRatingStrategy());

        RatedUsage rated = engine.rate(usage);

        assertThat(rated.getTotalCharge()).isEqualByComparingTo("15"); // 30 * 0.5
        assertThat(rated.getBaseCharge()).isEqualByComparingTo("15");
        assertThat(rated.getOverageCharge()).isEqualByComparingTo("0");
        assertThat(rated.getUsageRecord()).isSameAs(usage);
        assertThat(rated.getSubscriber()).isSameAs(subscriber);
        assertThat(rated.getPlan()).isSameAs(plan);
    }

    @Test
    void rateAccountsForPriorRatedUnitsInSameCycleForTiering() {
        Plan plan = plan();
        Subscriber subscriber = subscriberWithPlan(1L, plan);
        UsageRecord current = usage(subscriber, UsageType.CALL);
        current.setDurationMinutes(40);

        UsageRecord prior = usage(subscriber, UsageType.CALL);
        prior.setDurationMinutes(80);
        prior.setRated(true);

        when(usageRecordRepository.findBySubscriberIdAndRatedTrueAndUsageStartTimeBetween(
                eq(1L), any(), any())).thenReturn(List.of(prior));
        when(strategyFactory.getStrategy(plan, UsageType.CALL)).thenReturn(new TieredRatingStrategy(UsageType.CALL));

        RatedUsage rated = engine.rate(current);

        // 100 min allowance; 80 already used -> only 20 in allowance, 20 over
        assertThat(rated.getBaseCharge()).isEqualByComparingTo("10");    // 20 * 0.5
        assertThat(rated.getOverageCharge()).isEqualByComparingTo("10"); // 20 * 0.5
        assertThat(rated.getTotalCharge()).isEqualByComparingTo("20");
    }

    private Plan plan() {
        Plan plan = new Plan();
        plan.setIncludedMinutes(100);
        plan.setCallRatePerMinute(new BigDecimal("0.5"));
        return plan;
    }

    private Subscriber subscriberWithPlan(Long id, Plan plan) {
        Subscriber subscriber = new Subscriber();
        subscriber.setId(id);
        subscriber.setPlan(plan);
        return subscriber;
    }

    private UsageRecord usage(Subscriber subscriber, UsageType type) {
        UsageRecord usage = new UsageRecord();
        usage.setSubscriber(subscriber);
        usage.setUsageType(type);
        usage.setUsageStartTime(LocalDateTime.of(2026, 3, 10, 10, 0));
        return usage;
    }
}
