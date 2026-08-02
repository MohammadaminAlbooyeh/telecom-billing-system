package com.tbs.cdr;

import com.tbs.exception.DuplicateCdrException;
import com.tbs.repository.UsageRecordRepository;
import org.springframework.stereotype.Service;

@Service
public class CdrDeduplicationService {

    private final UsageRecordRepository usageRecordRepository;

    public CdrDeduplicationService(UsageRecordRepository usageRecordRepository) {
        this.usageRecordRepository = usageRecordRepository;
    }

    public boolean isDuplicate(String cdrId) {
        return cdrId != null && usageRecordRepository.existsByCdrId(cdrId);
    }

    public void checkDuplicate(String cdrId) {
        if (isDuplicate(cdrId)) {
            throw new DuplicateCdrException(cdrId);
        }
    }
}
