package com.tbs.batch;

import com.tbs.model.entity.Invoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

/**
 * Writes the invoice items produced by the monthly invoice step. Invoices are
 * already persisted by {@code InvoiceGenerationService}; this writer records
 * the generated invoices (e.g. for audit/logging) and completes the chunk.
 */
@Component
public class InvoiceItemWriter implements ItemWriter<Invoice> {

    private static final Logger log = LoggerFactory.getLogger(InvoiceItemWriter.class);

    @Override
    public void write(Chunk<? extends Invoice> chunk) {
        for (Invoice invoice : chunk.getItems()) {
            log.info("Generated invoice {} for subscriber {}, total {}",
                    invoice.getInvoiceNumber(),
                    invoice.getSubscriber() != null && invoice.getSubscriber().getMsisdn() != null
                            ? invoice.getSubscriber().getMsisdn() : "n/a",
                    invoice.getTotalAmount());
        }
    }
}

