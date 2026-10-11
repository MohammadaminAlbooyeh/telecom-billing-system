package com.tbs.integration;

import com.tbs.batch.MonthlyInvoiceJob;
import com.tbs.batch.UsageAggregationTasklet;
import com.tbs.model.entity.Subscriber;
import com.tbs.model.entity.Plan;
import com.tbs.model.entity.UsageRecord;
import com.tbs.model.enums.PlanType;
import com.tbs.model.enums.UsageType;
import com.tbs.repository.SubscriberRepository;
import com.tbs.repository.PlanRepository;
import com.tbs.repository.UsageRecordRepository;
import com.tbs.service.InvoiceGenerationService;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SpringBatchTest
@ActiveProfiles("postgres,test")
class MonthlyInvoiceJobIntegrationTest extends IntegrationTestBase {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private UsageRecordRepository usageRecordRepository;

    @Autowired
    private InvoiceGenerationService invoiceGenerationService;

    @Test
    void shouldRunMonthlyInvoiceJobSuccessfully() throws Exception {
        Plan plan = new Plan();
        plan.setName("Integration Test Plan");
        plan.setPlanType(PlanType.POSTPAID);
        plan.setMonthlyFee(BigDecimal.valueOf(25.00));
        plan.setCallRatePerMinute(BigDecimal.valueOf(0.12));
        plan.setDataRatePerMb(BigDecimal.valueOf(0.06));
        plan.setSmsRate(BigDecimal.valueOf(0.04));
        plan = planRepository.save(plan);

        Subscriber subscriber = new Subscriber();
        subscriber.setMsisdn("989555666777");
        subscriber.setFirstName("Integration");
        subscriber.setLastName("Test");
        subscriber.setEmail("integration.test@example.com");
        subscriber.setPassword("password");
        subscriber.setPlan(plan);
        subscriber = subscriberRepository.save(subscriber);

        UsageRecord usageRecord = new UsageRecord();
        usageRecord.setSubscriber(subscriber);
        usageRecord.setCdrId("cdr-integration-001");
        usageRecord.setUsageType(UsageType.CALL);
        usageRecord.setUsageStartTime(LocalDateTime.now().withHour(10).withMinute(0));
        usageRecord.setDurationMinutes(3);
        usageRecord.setRated(false);
        usageRecordRepository.save(usageRecord);

        JobParameters jobParameters = new JobParametersBuilder()
            .addLocalDate("periodStart", LocalDate.now().withDayOfMonth(1))
            .addLocalDate("periodEnd", LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()))
            .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");
    }

    @Test
    void shouldRunUsageAggregationTasklet() throws Exception {
        Plan plan = new Plan();
        plan.setName("Aggregation Test Plan");
        plan.setPlanType(PlanType.POSTPAID);
        plan.setMonthlyFee(BigDecimal.valueOf(30.00));
        plan.setCallRatePerMinute(BigDecimal.valueOf(0.10));
        plan.setDataRatePerMb(BigDecimal.valueOf(0.05));
        plan.setSmsRate(BigDecimal.valueOf(0.02));
        plan = planRepository.save(plan);

        Subscriber subscriber = new Subscriber();
        subscriber.setMsisdn("989888999000");
        subscriber.setFirstName("Aggregation");
        subscriber.setLastName("Test");
        subscriber.setEmail("aggregation.test@example.com");
        subscriber.setPassword("password");
        subscriber.setPlan(plan);
        subscriber = subscriberRepository.save(subscriber);

        UsageRecord usageRecord1 = new UsageRecord();
        usageRecord1.setSubscriber(subscriber);
        usageRecord1.setCdrId("cdr-agg-001");
        usageRecord1.setUsageType(UsageType.CALL);
        usageRecord1.setUsageStartTime(LocalDateTime.now().withHour(10).withMinute(0));
        usageRecord1.setDurationMinutes(1);
        usageRecord1.setRated(false);
        usageRecordRepository.save(usageRecord1);

        UsageRecord usageRecord2 = new UsageRecord();
        usageRecord2.setSubscriber(subscriber);
        usageRecord2.setCdrId("cdr-agg-002");
        usageRecord2.setUsageType(UsageType.DATA);
        usageRecord2.setUsageStartTime(LocalDateTime.now().withHour(11).withMinute(0));
        usageRecord2.setDataUsedMb(BigDecimal.valueOf(50));
        usageRecord2.setRated(false);
        usageRecordRepository.save(usageRecord2);

        JobParameters jobParameters = new JobParametersBuilder()
            .addLocalDate("periodStart", LocalDate.now().withDayOfMonth(1))
            .addLocalDate("periodEnd", LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()))
            .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchStep("usageAggregationStep", jobParameters);

        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");
    }
}