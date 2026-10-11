package com.tbs.batch;

import com.tbs.model.entity.Invoice;
import com.tbs.model.entity.Subscriber;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;

class InvoiceItemWriterTest {

    @Test
    void writeProcessesAllInvoicesWithoutError() {
        InvoiceItemWriter writer = new InvoiceItemWriter();

        Subscriber subscriber = new Subscriber();
        subscriber.setMsisdn("989120000000");

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-2026-ABC123");
        invoice.setSubscriber(subscriber);
        invoice.setTotalAmount(new BigDecimal("65.00"));

        assertThatCode(() -> writer.write(new Chunk<>(List.of(invoice, invoice))))
                .doesNotThrowAnyException();
    }

    @Test
    void writeHandlesEmptyChunk() {
        InvoiceItemWriter writer = new InvoiceItemWriter();

        assertThatCode(() -> writer.write(new Chunk<>(List.of())))
                .doesNotThrowAnyException();
    }
}
