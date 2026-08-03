package com.tbs.service;

import com.tbs.exception.SubscriberNotFoundException;
import com.tbs.model.dto.UsageSummaryDTO;
import com.tbs.model.entity.Subscriber;
import com.tbs.model.entity.UsageRecord;
import com.tbs.model.enums.UsageType;
import com.tbs.repository.SubscriberRepository;
import com.tbs.repository.UsageRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsageMeteringServiceTest {

    @Mock
    private UsageRecordRepository usageRecordRepository;

    @Mock
    private SubscriberRepository subscriberRepository;

    @InjectMocks
    private UsageMeteringService usageMeteringService;

    @Test
    void getUsageRecordsDelegatesWithDateRange() {
        LocalDate from = LocalDate.of(2026, 3, 1);
        LocalDate to = LocalDate.of(2026, 3, 31);
        when(usageRecordRepository.findBySubscriberIdAndUsageStartTimeBetween(eq(1L), any(), any()))
                .thenReturn(List.of(new UsageRecord()));

        List<UsageRecord> result = usageMeteringService.getUsageRecords(1L, from, to);

        assertThat(result).hasSize(1);
    }

    @Test
    void getUsageSummaryComputesTotals() {
        Subscriber subscriber = new Subscriber();
        subscriber.setId(1L);
        subscriber.setMsisdn("989120000000");

        UsageRecord call1 = record(UsageType.CALL);
        call1.setDurationMinutes(10);
        UsageRecord call2 = record(UsageType.CALL);
        call2.setDurationMinutes(5);
        UsageRecord sms = record(UsageType.SMS);
        sms.setSmsCount(3);
        UsageRecord data1 = record(UsageType.DATA);
        data1.setDataUsedMb(new BigDecimal("2.50"));
        UsageRecord data2 = record(UsageType.DATA);
        data2.setDataUsedMb(new BigDecimal("1.50"));

        when(subscriberRepository.findById(1L)).thenReturn(Optional.of(subscriber));
        when(usageRecordRepository.findBySubscriberIdAndUsageStartTimeBetween(eq(1L), any(), any()))
                .thenReturn(List.of(call1, call2, sms, data1, data2));

        UsageSummaryDTO summary = usageMeteringService.getUsageSummary(
                1L, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31));

        assertThat(summary.getMsisdn()).isEqualTo("989120000000");
        assertThat(summary.getCallCount()).isEqualTo(2);
        assertThat(summary.getTotalMinutes()).isEqualTo(15);
        assertThat(summary.getSmsCount()).isEqualTo(3);
        assertThat(summary.getTotalDataMb()).isEqualByComparingTo("4.00");
    }

    @Test
    void getUsageSummaryThrowsWhenSubscriberNotFound() {
        when(subscriberRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usageMeteringService.getUsageSummary(
                999L, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31)))
                .isInstanceOf(SubscriberNotFoundException.class);
    }

    @Test
    void countUnratedDelegates() {
        when(usageRecordRepository.countBySubscriberIdAndRatedFalse(1L)).thenReturn(7L);

        assertThat(usageMeteringService.countUnrated(1L)).isEqualTo(7L);
    }

    private UsageRecord record(UsageType type) {
        UsageRecord record = new UsageRecord();
        record.setUsageType(type);
        return record;
    }
}

