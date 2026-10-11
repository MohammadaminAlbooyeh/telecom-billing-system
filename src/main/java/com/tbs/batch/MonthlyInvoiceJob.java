package com.tbs.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Defines the monthly invoice job: rate pending usage (aggregation), then
 * generate an invoice for every subscriber. The job is triggered manually via
 * {@code JobLauncher} with optional {@code periodStart}/{@code periodEnd} params.
 */
@Configuration
public class MonthlyInvoiceJob {

    public static final String JOB_NAME = "monthlyInvoiceJob";

    @Bean
    public Job invoiceJob(JobRepository jobRepository,
                          @Qualifier("usageAggregationStep") Step usageAggregationStep,
                          @Qualifier("monthlyInvoiceStep") Step monthlyInvoiceStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(usageAggregationStep)
                .next(monthlyInvoiceStep)
                .build();
    }
}

