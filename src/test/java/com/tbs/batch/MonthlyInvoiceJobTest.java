package com.tbs.batch;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MonthlyInvoiceJobTest {

    @Test
    void buildsMonthlyInvoiceJobWithAggregationThenInvoicingSteps() {
        JobRepository jobRepository = mock(JobRepository.class);
        Step aggregation = mock(Step.class);
        Step invoicing = mock(Step.class);
        when(aggregation.getName()).thenReturn("usageAggregationStep");
        when(invoicing.getName()).thenReturn("monthlyInvoiceStep");

        MonthlyInvoiceJob config = new MonthlyInvoiceJob();
        Job job = config.invoiceJob(jobRepository, aggregation, invoicing);

        assertThat(job.getName()).isEqualTo("monthlyInvoiceJob");
    }
}
