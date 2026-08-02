package com.tbs.rating;

import com.tbs.model.entity.Plan;
import com.tbs.model.entity.RatedUsage;
import com.tbs.model.entity.Subscriber;
import com.tbs.model.entity.UsageRecord;
import com.tbs.model.enums.UsageType;
import com.tbs.repository.UsageRecordRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class RatingEngine {

    private final RatingStrategyFactory strategyFactory;
    private final UsageRecordRepository usageRecordRepository;

    public RatingEngine(RatingStrategyFactory strategyFactory, UsageRecordRepository usageRecordRepository) {
        this.strategyFactory = strategyFactory;
        this.usageRecordRepository = usageRecordRepository;
    }

    public RatedUsage rate(UsageRecord usageRecord) {
        Subscriber subscriber = usageRecord.getSubscriber();
        Plan plan = subscriber.getPlan();
        UsageType usageType = usageRecord.getUsageType();

        LocalDateTime cycleStart = startOfBillingCycle(usageRecord.getUsageStartTime());
        LocalDateTime cycleEnd = cycleStart.plusMonths(1);

        List<UsageRecord> priorRated = usageRecordRepository
                .findBySubscriberIdAndRatedTrueAndUsageStartTimeBetween(subscriber.getId(), cycleStart, cycleEnd);
        BigDecimal usedInCycle = priorRated.stream()
                .map(record -> unitsFor(record, usageType))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        RatingStrategy strategy = strategyFactory.getStrategy(plan, usageType);
        RateResult result = strategy.rate(plan, usageRecord, usedInCycle);

        RatedUsage ratedUsage = new RatedUsage();
        ratedUsage.setUsageRecord(usageRecord);
        ratedUsage.setSubscriber(subscriber);
        ratedUsage.setPlan(plan);
        ratedUsage.setBaseCharge(result.baseCharge());
        ratedUsage.setOverageCharge(result.overageCharge());
        ratedUsage.setTotalCharge(result.totalCharge());
        return ratedUsage;
    }

    private LocalDateTime startOfBillingCycle(LocalDateTime usageStartTime) {
        return usageStartTime.toLocalDate().withDayOfMonth(1).atStartOfDay();
    }

    private BigDecimal unitsFor(UsageRecord record, UsageType usageType) {
        return switch (usageType) {
            case CALL -> record.getDurationMinutes() != null
                    ? BigDecimal.valueOf(record.getDurationMinutes()) : BigDecimal.ZERO;
            case DATA -> record.getDataUsedMb() != null
                    ? record.getDataUsedMb() : BigDecimal.ZERO;
            case SMS -> record.getSmsCount() != null
                    ? BigDecimal.valueOf(record.getSmsCount()) : BigDecimal.ZERO;
        };
    }
}
