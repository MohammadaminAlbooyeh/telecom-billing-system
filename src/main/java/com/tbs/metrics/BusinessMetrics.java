package com.tbs.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class BusinessMetrics {

    private final Counter cdrIngestedCounter;
    private final Counter cdrFailedCounter;
    private final Counter invoicesGeneratedCounter;
    private final Counter invoicesFailedCounter;
    private final Counter usageRatedCounter;
    private final Timer cdrIngestionTimer;
    private final Timer ratingTimer;
    private final Timer invoiceGenerationTimer;

    public BusinessMetrics(MeterRegistry meterRegistry) {
        this.cdrIngestedCounter = Counter.builder("tbs.cdr.ingested")
            .description("Total number of CDRs successfully ingested")
            .register(meterRegistry);

        this.cdrFailedCounter = Counter.builder("tbs.cdr.failed")
            .description("Total number of CDRs failed to ingest")
            .register(meterRegistry);

        this.invoicesGeneratedCounter = Counter.builder("tbs.invoices.generated")
            .description("Total number of invoices generated")
            .register(meterRegistry);

        this.invoicesFailedCounter = Counter.builder("tbs.invoices.failed")
            .description("Total number of invoices failed to generate")
            .register(meterRegistry);

        this.usageRatedCounter = Counter.builder("tbs.usage.rated")
            .description("Total number of usage records rated")
            .register(meterRegistry);

        this.cdrIngestionTimer = Timer.builder("tbs.cdr.ingestion.duration")
            .description("Time taken to ingest a CDR")
            .publishPercentileHistogram()
            .register(meterRegistry);

        this.ratingTimer = Timer.builder("tbs.rating.duration")
            .description("Time taken to rate usage")
            .publishPercentileHistogram()
            .register(meterRegistry);

        this.invoiceGenerationTimer = Timer.builder("tbs.invoice.generation.duration")
            .description("Time taken to generate an invoice")
            .publishPercentileHistogram()
            .register(meterRegistry);
    }

    public void incrementCdrIngested() {
        cdrIngestedCounter.increment();
    }

    public void incrementCdrFailed() {
        cdrFailedCounter.increment();
    }

    public void incrementInvoicesGenerated() {
        invoicesGeneratedCounter.increment();
    }

    public void incrementInvoicesFailed() {
        invoicesFailedCounter.increment();
    }

    public void incrementUsageRated() {
        usageRatedCounter.increment();
    }

    public Timer.Sample startCdrIngestionTimer() {
        return Timer.start();
    }

    public void stopCdrIngestionTimer(Timer.Sample sample) {
        sample.stop(cdrIngestionTimer);
    }

    public Timer.Sample startRatingTimer() {
        return Timer.start();
    }

    public void stopRatingTimer(Timer.Sample sample) {
        sample.stop(ratingTimer);
    }

    public Timer.Sample startInvoiceGenerationTimer() {
        return Timer.start();
    }

    public void stopInvoiceGenerationTimer(Timer.Sample sample) {
        sample.stop(invoiceGenerationTimer);
    }
}