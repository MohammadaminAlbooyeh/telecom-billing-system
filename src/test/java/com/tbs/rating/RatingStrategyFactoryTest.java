package com.tbs.rating;

import com.tbs.model.entity.Plan;
import com.tbs.model.enums.UsageType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RatingStrategyFactoryTest {

    private final RatingStrategyFactory factory = new RatingStrategyFactory();

    @Test
    void returnsTieredStrategyWhenCallAllowanceExists() {
        Plan plan = plan();
        plan.setIncludedMinutes(100);

        RatingStrategy strategy = factory.getStrategy(plan, UsageType.CALL);

        assertThat(strategy).isInstanceOf(TieredRatingStrategy.class);
    }

    @Test
    void returnsFlatCallStrategyWhenNoCallAllowance() {
        Plan plan = plan();
        plan.setIncludedMinutes(0);

        RatingStrategy strategy = factory.getStrategy(plan, UsageType.CALL);

        assertThat(strategy).isInstanceOf(CallRatingStrategy.class);
    }

    @Test
    void returnsTieredStrategyWhenDataAllowanceExists() {
        Plan plan = plan();
        plan.setIncludedDataMb(500);

        assertThat(factory.getStrategy(plan, UsageType.DATA)).isInstanceOf(TieredRatingStrategy.class);
    }

    @Test
    void returnsFlatDataStrategyWhenNoDataAllowance() {
        Plan plan = plan();
        plan.setIncludedDataMb(0);

        assertThat(factory.getStrategy(plan, UsageType.DATA)).isInstanceOf(DataRatingStrategy.class);
    }

    @Test
    void returnsTieredStrategyWhenSmsAllowanceExists() {
        Plan plan = plan();
        plan.setIncludedSms(50);

        assertThat(factory.getStrategy(plan, UsageType.SMS)).isInstanceOf(TieredRatingStrategy.class);
    }

    @Test
    void returnsFlatSmsStrategyWhenNoSmsAllowance() {
        Plan plan = plan();
        plan.setIncludedSms(0);

        assertThat(factory.getStrategy(plan, UsageType.SMS)).isInstanceOf(SmsRatingStrategy.class);
    }

    private Plan plan() {
        return new Plan();
    }
}
