package com.tbs.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Kafka configuration: enables listener support and declares the topics used
 * by the messaging layer. Spring Boot's KafkaAutoConfiguration wires the
 * consumer/producer factories from the {@code spring.kafka.*} properties in
 * application.yml. Declared {@link NewTopic} beans are created automatically
 * on startup by the KafkaAdmin when a broker is reachable.
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    public static final String CDR_EVENTS_TOPIC = "cdr-events";
    public static final String INVOICE_EVENTS_TOPIC = "invoice-events";

    private static final int PARTITIONS = 1;
    private static final short REPLICATION_FACTOR = 1;

    @Bean
    public NewTopic cdrEventsTopic() {
        return new NewTopic(CDR_EVENTS_TOPIC, PARTITIONS, REPLICATION_FACTOR);
    }

    @Bean
    public NewTopic invoiceEventsTopic() {
        return new NewTopic(INVOICE_EVENTS_TOPIC, PARTITIONS, REPLICATION_FACTOR);
    }
}

