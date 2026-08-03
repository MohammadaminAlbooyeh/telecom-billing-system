package com.tbs.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbs.config.KafkaConfig;
import com.tbs.model.entity.Invoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Publishes invoice events to the {@code invoice-events} topic. The payload is
 * encoded as JSON using the configured {@link ObjectMapper} and sent with a
 * String key (the invoice number), matching the String producer serializers.
 */
@Component
public class InvoiceEventProducer {

    private static final Logger log = LoggerFactory.getLogger(InvoiceEventProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public InvoiceEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishInvoiceCreated(Invoice invoice) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("invoiceNumber", invoice.getInvoiceNumber());
        payload.put("subscriberId", invoice.getSubscriber().getId());
        payload.put("msisdn", invoice.getSubscriber().getMsisdn());
        payload.put("totalAmount", invoice.getTotalAmount());
        payload.put("status", invoice.getStatus().name());

        String json;
        try {
            json = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize invoice event for {}: {}", invoice.getInvoiceNumber(), e.getMessage(), e);
            return;
        }

        kafkaTemplate.send(KafkaConfig.INVOICE_EVENTS_TOPIC, invoice.getInvoiceNumber(), json);
        log.info("Published invoice event for {} to {}", invoice.getInvoiceNumber(), KafkaConfig.INVOICE_EVENTS_TOPIC);
    }
}

