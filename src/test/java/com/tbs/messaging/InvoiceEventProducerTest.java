package com.tbs.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbs.config.KafkaConfig;
import com.tbs.model.entity.Invoice;
import com.tbs.model.entity.Subscriber;
import com.tbs.model.enums.InvoiceStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InvoiceEventProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void publishInvoiceCreatedSendsJsonToInvoiceTopic() throws Exception {
        InvoiceEventProducer producer = new InvoiceEventProducer(kafkaTemplate, objectMapper);

        Subscriber subscriber = new Subscriber();
        subscriber.setId(7L);
        subscriber.setMsisdn("989120000000");

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-2026-ABC123");
        invoice.setSubscriber(subscriber);
        invoice.setTotalAmount(new BigDecimal("65.00"));
        invoice.setStatus(InvoiceStatus.DRAFT);

        producer.publishInvoiceCreated(invoice);

        ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq(KafkaConfig.INVOICE_EVENTS_TOPIC),
                eq("INV-2026-ABC123"), payload.capture());

        assertThat(payload.getValue()).contains("\"invoiceNumber\":\"INV-2026-ABC123\"");
        assertThat(payload.getValue()).contains("\"totalAmount\":65.00");
        assertThat(payload.getValue()).contains("\"status\":\"DRAFT\"");
        assertThat(payload.getValue()).contains("\"subscriberId\":7");
    }
}
