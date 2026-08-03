package com.tbs.messaging;

import com.tbs.cdr.CdrIngestionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CdrEventConsumerTest {

    @Mock
    private CdrIngestionService cdrIngestionService;

    @Test
    void consumeDelegatesRawCdrToIngestionService() {
        CdrEventConsumer consumer = new CdrEventConsumer(cdrIngestionService);
        String raw = "cdr-1,989120000000,CALL,2026-03-10T10:00:00,5,,,";

        consumer.consume(raw);

        verify(cdrIngestionService).ingest(raw);
    }

    @Test
    void consumeDoesNotPropagateIngestionFailure() {
        CdrEventConsumer consumer = new CdrEventConsumer(cdrIngestionService);
        doThrow(new IllegalArgumentException("bad record")).when(cdrIngestionService).ingest("bad");

        // Should not throw - consumer logs and continues
        consumer.consume("bad");

        verify(cdrIngestionService).ingest("bad");
    }
}
