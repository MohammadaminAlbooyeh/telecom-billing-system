package com.tbs.service;

import com.tbs.exception.SubscriberNotFoundException;
import com.tbs.model.entity.Invoice;
import com.tbs.model.entity.Plan;
import com.tbs.model.entity.RatedUsage;
import com.tbs.model.entity.Subscriber;
import com.tbs.model.entity.UsageRecord;
import com.tbs.model.enums.InvoiceStatus;
import com.tbs.model.enums.UsageType;
import com.tbs.repository.InvoiceRepository;
import com.tbs.repository.RatedUsageRepository;
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
class InvoiceGenerationServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private SubscriberRepository subscriberRepository;

    @Mock
    private UsageRecordRepository usageRecordRepository;

    @Mock
    private RatedUsageRepository ratedUsageRepository;

    @InjectMocks
    private InvoiceGenerationService invoiceGenerationService;

    private final LocalDate periodStart = LocalDate.of(2026, 3, 1);
    private final LocalDate periodEnd = LocalDate.of(2026, 3, 31);

    @Test
    void generateInvoiceAggregatesUsageAndMonthlyFee() {
        Plan plan = plan("Basic", "50.00");
        Subscriber subscriber = subscriber(1L, plan);
        UsageRecord usage = usage(9L, UsageType.CALL);
        RatedUsage rated = rated(usage, "15.00");

        when(subscriberRepository.findById(1L)).thenReturn(Optional.of(subscriber));
        when(invoiceRepository.existsBySubscriberIdAndBillingPeriodEnd(eq(1L), eq(periodEnd))).thenReturn(false);
        when(usageRecordRepository.findBySubscriberIdAndRatedTrueAndUsageStartTimeBetween(
                eq(1L), any(), any())).thenReturn(List.of(usage));
        when(ratedUsageRepository.findByUsageRecordId(9L)).thenReturn(Optional.of(rated));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> inv.getArgument(0));

        Invoice invoice = invoiceGenerationService.generateInvoice(1L, periodStart, periodEnd);

        assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.DRAFT);
        assertThat(invoice.getTotalAmount()).isEqualByComparingTo("65.00"); // 15 usage + 50 fee
        assertThat(invoice.getLineItems()).hasSize(2);
        assertThat(invoice.getIssueDate()).isNotNull();
        assertThat(invoice.getDueDate()).isNotNull();
        assertThat(invoice.getInvoiceNumber()).startsWith("INV-");
    }

    @Test
    void generateInvoiceSkipsMonthlyFeeWhenZero() {
        Plan plan = plan("Prepaid", "0.00");
        Subscriber subscriber = subscriber(1L, plan);

        when(subscriberRepository.findById(1L)).thenReturn(Optional.of(subscriber));
        when(invoiceRepository.existsBySubscriberIdAndBillingPeriodEnd(eq(1L), eq(periodEnd))).thenReturn(false);
        when(usageRecordRepository.findBySubscriberIdAndRatedTrueAndUsageStartTimeBetween(
                eq(1L), any(), any())).thenReturn(List.of());
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> inv.getArgument(0));

        Invoice invoice = invoiceGenerationService.generateInvoice(1L, periodStart, periodEnd);

        assertThat(invoice.getTotalAmount()).isEqualByComparingTo("0");
        assertThat(invoice.getLineItems()).isEmpty();
    }

    @Test
    void generateInvoiceThrowsWhenAlreadyGenerated() {
        Subscriber subscriber = subscriber(1L, plan("Basic", "50.00"));
        when(subscriberRepository.findById(1L)).thenReturn(Optional.of(subscriber));
        when(invoiceRepository.existsBySubscriberIdAndBillingPeriodEnd(eq(1L), eq(periodEnd))).thenReturn(true);

        assertThatThrownBy(() -> invoiceGenerationService.generateInvoice(1L, periodStart, periodEnd))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already generated");
    }

    @Test
    void generateInvoiceThrowsWhenSubscriberNotFound() {
        when(subscriberRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceGenerationService.generateInvoice(999L, periodStart, periodEnd))
                .isInstanceOf(SubscriberNotFoundException.class);
    }

    @Test
    void generateInvoicesForAllSkipsExistingInvoices() {
        Subscriber subscribed = subscriber(1L, plan("Basic", "50.00"));
        Subscriber skipped = subscriber(2L, plan("Basic", "50.00"));

        when(subscriberRepository.findAll()).thenReturn(List.of(subscribed, skipped));
        when(subscriberRepository.findById(1L)).thenReturn(Optional.of(subscribed));
        when(invoiceRepository.existsBySubscriberIdAndBillingPeriodEnd(eq(1L), eq(periodEnd))).thenReturn(false);
        when(invoiceRepository.existsBySubscriberIdAndBillingPeriodEnd(eq(2L), eq(periodEnd))).thenReturn(true);
        when(usageRecordRepository.findBySubscriberIdAndRatedTrueAndUsageStartTimeBetween(
                eq(1L), any(), any())).thenReturn(List.of());
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> inv.getArgument(0));

        List<Invoice> invoices = invoiceGenerationService.generateInvoicesForAll(periodStart, periodEnd);

        assertThat(invoices).hasSize(1);
    }

    private Plan plan(String name, String fee) {
        Plan plan = new Plan();
        plan.setName(name);
        plan.setMonthlyFee(new BigDecimal(fee));
        return plan;
    }

    private Subscriber subscriber(Long id, Plan plan) {
        Subscriber subscriber = new Subscriber();
        subscriber.setId(id);
        subscriber.setMsisdn("989120000000");
        subscriber.setPlan(plan);
        return subscriber;
    }

    private UsageRecord usage(Long id, UsageType type) {
        UsageRecord usage = new UsageRecord();
        usage.setId(id);
        usage.setUsageType(type);
        usage.setDurationMinutes(30);
        return usage;
    }

    private RatedUsage rated(UsageRecord usage, String total) {
        RatedUsage rated = new RatedUsage();
        rated.setUsageRecord(usage);
        rated.setTotalCharge(new BigDecimal(total));
        return rated;
    }
}


