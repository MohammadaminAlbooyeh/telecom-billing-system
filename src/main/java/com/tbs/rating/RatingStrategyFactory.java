package com.tbs.rating;

import com.tbs.model.entity.Plan;
import com.tbs.model.enums.UsageType;
import org.springframework.stereotype.Component;

@Component
public class RatingStrategyFactory {

    public RatingStrategy getStrategy(Plan plan, UsageType usageType) {
        if (hasIncludedAllowance(plan, usageType)) {
            return new TieredRatingStrategy(usageType);
        }
        return switch (usageType) {
            case CALL -> new CallRatingStrategy();
            case DATA -> new DataRatingStrategy();
            case SMS -> new SmsRatingStrategy();
        };
    }

    private boolean hasIncludedAllowance(Plan plan, UsageType usageType) {
        return switch (usageType) {
            case CALL -> plan.getIncludedMinutes() > 0;
            case DATA -> plan.getIncludedDataMb() > 0;
            case SMS -> plan.getIncludedSms() > 0;
        };
    }
}
