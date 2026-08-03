package com.tbs.service;

import com.tbs.exception.SubscriberNotFoundException;
import com.tbs.model.entity.Invoice;
import com.tbs.model.entity.InvoiceLineItem;
import com.tbs.model.entity.Plan;
import com.tbs.model.entity.RatedUsage;
import com.tbs.model.entity.Subscriber;
import com.tbs.model.entity.UsageRecord;
import com.tbs.model.enums.InvoiceStatus;
import com.tbs.messaging.InvoiceEventProducer;
import com.tbs.repository.InvoiceRepository;
import com.tbs.repository.RatedUsageRepository;
import com.tbs.repository.SubscriberRepository;
import com.tbs.repository.UsageRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvoiceGenerationService {

    private static final int DEFAULT_PAYMENT_TERMS_DAYS = 14;

    private final InvoiceRepository invoiceRepository;
    private final SubscriberRepository subscriberRepository;
    private final UsageRecordRepository usageRecordRepository;
    private final RatedUsageRepository ratedUsageRepository;
    private final InvoiceEventProducer invoiceEventProducer;

    public InvoiceGenerationService(InvoiceRepository invoiceRepository,
                                    SubscriberRepository subscriberRepository,
                                    UsageRecordRepository usageRecordRepository,
                                    RatedUsageRepository ratedUsageRepository,
                                    InvoiceEventProducer invoiceEventProducer) {
        this.invoiceRepository = invoiceRepository;
        this.subscriberRepository = subscriberRepository;
        this.usageRecordRepository = usageRecordRepository;
        this.ratedUsageRepository = ratedUsageRepository;
        this.invoiceEventProducer = invoiceEventProducer;
    }

    @Transactional
    public Invoice generateInvoice(Long subscriberId, LocalDate periodStart, LocalDate periodEnd) {
        Subscriber subscriber = subscriberRepository.findById(subscriberId)
                .orElseThrow(() -> new SubscriberNotFoundException(subscriberId));
        if (invoiceRepository.existsBySubscriberIdAndBillingPeriodEnd(subscriberId, periodEnd)) {
            throw new IllegalStateException("Invoice already generated for subscriber " + subscriberId
                    + " for period ending " + periodEnd);
        }

        List<UsageRecord> ratedUsage = usageRecordRepository.findBySubscriberIdAndRatedTrueAndUsageStartTimeBetween(
                subscriberId, periodStart.atStartOfDay(), periodEnd.plusDays(1).atStartOfDay());

        Invoice invoice = new Invoice();
        invoice.setSubscriber(subscriber);
        invoice.setBillingPeriodStart(periodStart);
        invoice.setBillingPeriodEnd(periodEnd);
        invoice.setIssueDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(DEFAULT_PAYMENT_TERMS_DAYS));
        invoice.setInvoiceNumber(generateInvoiceNumber());

        BigDecimal total = BigDecimal.ZERO;
        for (UsageRecord usage : ratedUsage) {
            RatedUsage rated = ratedUsageRepository.findByUsageRecordId(usage.getId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Rated usage not found for usage record " + usage.getId()));
            InvoiceLineItem lineItem = new InvoiceLineItem();
            lineItem.setRatedUsage(rated);
            lineItem.setUsageType(usage.getUsageType());
            lineItem.setDescription(describe(usage));
            lineItem.setAmount(rated.getTotalCharge());
            invoice.addLineItem(lineItem);
            total = total.add(rated.getTotalCharge());
        }

        Plan plan = subscriber.getPlan();
        if (plan.getMonthlyFee() != null && plan.getMonthlyFee().signum() > 0) {
            InvoiceLineItem feeLine = new InvoiceLineItem();
            feeLine.setDescription("Monthly subscription fee - " + plan.getName());
            feeLine.setAmount(plan.getMonthlyFee());
            invoice.addLineItem(feeLine);
            total = total.add(plan.getMonthlyFee());
        }

        invoice.setTotalAmount(total);
        invoice.setStatus(InvoiceStatus.DRAFT);
        Invoice saved = invoiceRepository.save(invoice);
        invoiceEventProducer.publishInvoiceCreated(saved);
        return saved;
    }

    @Transactional
    public List<Invoice> generateInvoicesForAll(LocalDate periodStart, LocalDate periodEnd) {
        List<Subscriber> subscribers = subscriberRepository.findAll();
        List<Invoice> invoices = new ArrayList<>();
        for (Subscriber subscriber : subscribers) {
            if (!invoiceRepository.existsBySubscriberIdAndBillingPeriodEnd(subscriber.getId(), periodEnd)) {
                invoices.add(generateInvoice(subscriber.getId(), periodStart, periodEnd));
            }
        }
        return invoices;
    }

    public List<Invoice> getInvoicesForSubscriber(Long subscriberId) {
        return invoiceRepository.findBySubscriberIdOrderByBillingPeriodEndDesc(subscriberId);
    }

    public Optional<Invoice> getByInvoiceNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber);
    }

    private String describe(UsageRecord usage) {
        return switch (usage.getUsageType()) {
            case CALL -> usage.getDurationMinutes() + " minutes";
            case DATA -> usage.getDataUsedMb() + " MB";
            case SMS -> usage.getSmsCount() + " SMS";
        };
    }

    private String generateInvoiceNumber() {
        return "INV-" + LocalDate.now().getYear() + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
