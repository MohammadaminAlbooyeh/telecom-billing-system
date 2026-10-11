package com.tbs.config;

import com.tbs.batch.InvoiceItemWriter;
import com.tbs.batch.UsageAggregationTasklet;
import com.tbs.model.entity.Invoice;
import com.tbs.model.entity.Subscriber;
import com.tbs.repository.SubscriberRepository;
import com.tbs.service.InvoiceGenerationService;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.Map;

/**
 * Wiring for the monthly invoice batch job: a tasklet step that rates pending
 * usage followed by a chunk step that generates an invoice per subscriber.
 *
 * <p>The billing window is read from the job parameters {@code periodStart} and
 * {@code periodEnd} (ISO dates). When omitted, the current calendar month is
 * used. The job is not run automatically at startup ({@code spring.batch.job.
 * enabled=false}); it is intended to be triggered via {@code JobLauncher}.
 */
@Configuration
public class BatchConfig {

    @Bean
    @StepScope
    public ItemProcessor<Subscriber, Invoice> invoiceProcessor(
            @Value("#{jobParameters['periodStart']}") String periodStart,
            @Value("#{jobParameters['periodEnd']}") String periodEnd,
            InvoiceGenerationService invoiceGenerationService) {
        LocalDate start = periodStart == null ? LocalDate.now().withDayOfMonth(1) : LocalDate.parse(periodStart);
        LocalDate end = periodEnd == null ? start.plusMonths(1).minusDays(1) : LocalDate.parse(periodEnd);
        return subscriber -> {
            try {
                return invoiceGenerationService.generateInvoice(subscriber.getId(), start, end);
            } catch (IllegalStateException ex) {
                // Invoice already generated for this period - filter the item out.
                return null;
            }
        };
    }

    @Bean
    public ItemReader<Subscriber> subscriberReader(SubscriberRepository subscriberRepository) {
        return new RepositoryItemReaderBuilder<Subscriber>()
                .name("subscriberReader")
                .repository(subscriberRepository)
                .methodName("findAll")
                .pageSize(100)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public Step usageAggregationStep(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager,
                                     UsageAggregationTasklet usageAggregationTasklet) {
        return new StepBuilder("usageAggregationStep", jobRepository)
                .tasklet(usageAggregationTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step monthlyInvoiceStep(JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager,
                                   ItemReader<Subscriber> subscriberReader,
                                   ItemProcessor<Subscriber, Invoice> invoiceProcessor,
                                   InvoiceItemWriter invoiceItemWriter) {
        return new StepBuilder("monthlyInvoiceStep", jobRepository)
                .<Subscriber, Invoice>chunk(50, transactionManager)
                .reader(subscriberReader)
                .processor(invoiceProcessor)
                .writer(invoiceItemWriter)
                .build();
    }
}

