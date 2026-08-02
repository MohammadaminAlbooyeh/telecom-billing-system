package com.tbs.service;

import com.tbs.model.entity.RatedUsage;
import com.tbs.model.entity.UsageRecord;
import com.tbs.rating.RatingEngine;
import com.tbs.repository.RatedUsageRepository;
import com.tbs.repository.UsageRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RatingService {

    private final RatingEngine ratingEngine;
    private final UsageRecordRepository usageRecordRepository;
    private final RatedUsageRepository ratedUsageRepository;
    private final BalanceService balanceService;

    public RatingService(RatingEngine ratingEngine,
                         UsageRecordRepository usageRecordRepository,
                         RatedUsageRepository ratedUsageRepository,
                         BalanceService balanceService) {
        this.ratingEngine = ratingEngine;
        this.usageRecordRepository = usageRecordRepository;
        this.ratedUsageRepository = ratedUsageRepository;
        this.balanceService = balanceService;
    }

    @Transactional
    public RatedUsage rateUsage(Long usageRecordId) {
        UsageRecord usage = usageRecordRepository.findById(usageRecordId)
                .orElseThrow(() -> new IllegalArgumentException("Usage record not found with id: " + usageRecordId));
        if (usage.isRated()) {
            throw new IllegalStateException("Usage record " + usageRecordId + " is already rated");
        }
        return rate(usage);
    }

    @Transactional
    public List<RatedUsage> ratePendingForSubscriber(Long subscriberId) {
        List<UsageRecord> pending = usageRecordRepository.findBySubscriberIdAndRatedFalse(subscriberId);
        List<RatedUsage> rated = new ArrayList<>();
        for (UsageRecord usage : pending) {
            rated.add(rate(usage));
        }
        return rated;
    }

    @Transactional
    public List<RatedUsage> rateAllPending() {
        List<UsageRecord> pending = usageRecordRepository.findByRatedFalse();
        List<RatedUsage> rated = new ArrayList<>();
        for (UsageRecord usage : pending) {
            rated.add(rate(usage));
        }
        return rated;
    }

    private RatedUsage rate(UsageRecord usage) {
        RatedUsage ratedUsage = ratingEngine.rate(usage);
        ratedUsageRepository.save(ratedUsage);
        usage.setRated(true);
        usageRecordRepository.save(usage);
        balanceService.applyCharge(usage.getSubscriber(), ratedUsage.getTotalCharge());
        return ratedUsage;
    }
}
