package com.tbs.messaging;

import com.tbs.cdr.CdrIngestionService;
import com.tbs.config.KafkaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumes raw CDR messages from the {@code cdr-events} topic and delegates
 * each one to {@link CdrIngestionService}. Messages are raw comma-separated
 * CDR strings (matching the configured String value deserializer); parsing is
 * performed by the ingestion service via {@code CdrParser}.
 */
@Component
public class CdrEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(CdrEventConsumer.class);

    private final CdrIngestionService cdrIngestionService;

    public CdrEventConsumer(CdrIngestionService cdrIngestionService) {
        this.cdrIngestionService = cdrIngestionService;
    }

    @KafkaListener(topics = KafkaConfig.CDR_EVENTS_TOPIC)
    public void consume(String rawCdr) {
        log.info("Received CDR event from Kafka: {}", rawCdr);
        try {
            cdrIngestionService.ingest(rawCdr);
            log.info("Successfully ingested CDR from Kafka: {}", rawCdr);
        } catch (Exception e) {
            // A single bad record must not kill the consumer; log and continue.
            log.error("Failed to ingest CDR from Kafka: {}. Reason: {}", rawCdr, e.getMessage(), e);
        }
    }
}

