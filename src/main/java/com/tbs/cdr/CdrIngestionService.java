package com.tbs.cdr;

import com.tbs.exception.SubscriberNotFoundException;
import com.tbs.model.dto.CdrDTO;
import com.tbs.model.entity.Subscriber;
import com.tbs.model.entity.UsageRecord;
import com.tbs.repository.SubscriberRepository;
import com.tbs.repository.UsageRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CdrIngestionService {

    private final CdrParser cdrParser;
    private final CdrValidator cdrValidator;
    private final CdrDeduplicationService cdrDeduplicationService;
    private final SubscriberRepository subscriberRepository;
    private final UsageRecordRepository usageRecordRepository;

    public CdrIngestionService(CdrParser cdrParser,
                               CdrValidator cdrValidator,
                               CdrDeduplicationService cdrDeduplicationService,
                               SubscriberRepository subscriberRepository,
                               UsageRecordRepository usageRecordRepository) {
        this.cdrParser = cdrParser;
        this.cdrValidator = cdrValidator;
        this.cdrDeduplicationService = cdrDeduplicationService;
        this.subscriberRepository = subscriberRepository;
        this.usageRecordRepository = usageRecordRepository;
    }

    @Transactional
    public UsageRecord ingest(String rawCdr) {
        return ingest(cdrParser.parse(rawCdr));
    }

    @Transactional
    public UsageRecord ingest(CdrDTO cdr) {
        cdrValidator.validate(cdr);
        cdrDeduplicationService.checkDuplicate(cdr.getCdrId());
        Subscriber subscriber = subscriberRepository.findByMsisdn(cdr.getMsisdn())
                .orElseThrow(() -> new SubscriberNotFoundException(cdr.getMsisdn()));
        if (!subscriber.isActive()) {
            throw new IllegalArgumentException("Subscriber is not active: " + cdr.getMsisdn());
        }
        return usageRecordRepository.save(toUsageRecord(cdr, subscriber));
    }

    @Transactional
    public List<UsageRecord> ingestBatch(List<String> rawCdrs) {
        List<UsageRecord> records = new ArrayList<>();
        for (String raw : rawCdrs) {
            records.add(ingest(raw));
        }
        return records;
    }

    @Transactional
    public List<UsageRecord> ingestAll(List<CdrDTO> cdrs) {
        List<UsageRecord> records = new ArrayList<>();
        for (CdrDTO cdr : cdrs) {
            records.add(ingest(cdr));
        }
        return records;
    }

    private UsageRecord toUsageRecord(CdrDTO cdr, Subscriber subscriber) {
        UsageRecord record = new UsageRecord();
        record.setCdrId(cdr.getCdrId());
        record.setSubscriber(subscriber);
        record.setUsageType(cdr.getUsageType());
        record.setUsageStartTime(cdr.getUsageStartTime());
        record.setDurationMinutes(cdr.getDurationMinutes());
        record.setDataUsedMb(cdr.getDataUsedMb());
        record.setSmsCount(cdr.getSmsCount());
        record.setRated(false);
        return record;
    }
}
