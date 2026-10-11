package com.tbs.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;

@Component
@EnableScheduling
public class BatchScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("monthlyInvoiceJob")
    private Job monthlyInvoiceJob;

    @Value("${batch.schedule.enabled:true}")
    private boolean scheduleEnabled;

    @Scheduled(cron = "${batch.schedule.cron:0 0 2 1 * ?}")
    public void runMonthlyInvoiceJob() {
        if (!scheduleEnabled) {
            return;
        }

        LocalDate periodEnd = YearMonth.now().minusMonths(1).atEndOfMonth();
        LocalDate periodStart = periodEnd.withDayOfMonth(1);

        JobParameters jobParameters = new JobParametersBuilder()
            .addLocalDate("periodStart", periodStart)
            .addLocalDate("periodEnd", periodEnd)
            .addLong("timestamp", System.currentTimeMillis())
            .toJobParameters();

        try {
            jobLauncher.run(monthlyInvoiceJob, jobParameters);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute monthly invoice job", e);
        }
    }
}