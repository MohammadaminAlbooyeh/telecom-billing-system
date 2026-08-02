package com.tbs.service;

import com.tbs.exception.SubscriberNotFoundException;
import com.tbs.model.dto.UsageSummaryDTO;
import com.tbs.model.entity.Subscriber;
import com.tbs.model.entity.UsageRecord;
import com.tbs.model.enums.UsageType;
import com.tbs.repository.SubscriberRepository;
import com.tbs.repository.UsageRecordRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class UsageMeteringService {

    private final UsageRecordRepository usageRecordRepository;
    private final SubscriberRepository subscriberRepository;

    public UsageMeteringService(UsageRecordRepository usageRecordRepository,
                                SubscriberRepository subscriberRepository) {
        this.usageRecordRepository = usageRecordRepository;
        this.subscriberRepository = subscriberRepository;
    }

    public List<UsageRecord> getUsageRecords(Long subscriberId, LocalDate from, LocalDate to) {
        return usageRecordRepository.findBySubscriberIdAndUsageStartTimeBetween(
                subscriberId, from.atStartOfDay(), to.plusDays(1).atStartOfDay());
    }

    public List<UsageRecord> getRatedUsageRecords(Long subscriberId, LocalDate from, LocalDate to) {
        return usageRecordRepository.findBySubscriberIdAndRatedTrueAndUsageStartTimeBetween(
                subscriberId, from.atStartOfDay(), to.plusDays(1).atStartOfDay());
    }

    public UsageSummaryDTO getUsageSummary(Long subscriberId, LocalDate from, LocalDate to) {
        Subscriber subscriber = subscriberRepository.findById(subscriberId)
                .orElseThrow(() -> new SubscriberNotFoundException(subscriberId));
        List<UsageRecord> records = getUsageRecords(subscriberId, from, to);

        UsageSummaryDTO summary = new UsageSummaryDTO();
        summary.setSubscriberId(subscriberId);
        summary.setMsisdn(subscriber.getMsisdn());
        summary.setPeriodStart(from);
        summary.setPeriodEnd(to);
        summary.setCallCount(records.stream()
                .filter(r -> r.getUsageType() == UsageType.CALL).count());
        summary.setTotalMinutes(records.stream()
                .filter(r -> r.getUsageType() == UsageType.CALL)
                .mapToLong(r -> r.getDurationMinutes() == null ? 0L : r.getDurationMinutes())
                .sum());
        summary.setSmsCount(records.stream()
                .filter(r -> r.getUsageType() == UsageType.SMS)
                .mapToLong(r -> r.getSmsCount() == null ? 0L : r.getSmsCount())
                .sum());
        summary.setTotalDataMb(records.stream()
                .filter(r -> r.getUsageType() == UsageType.DATA)
                .map(UsageRecord::getDataUsedMb)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return summary;
    }

    public long countUnrated(Long subscriberId) {
        return usageRecordRepository.countBySubscriberIdAndRatedFalse(subscriberId);
    }
}
