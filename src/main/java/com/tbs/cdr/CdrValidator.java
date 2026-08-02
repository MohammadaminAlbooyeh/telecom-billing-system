package com.tbs.cdr;

import com.tbs.model.dto.CdrDTO;
import org.springframework.stereotype.Component;

@Component
public class CdrValidator {

    public void validate(CdrDTO cdr) {
        if (cdr == null) {
            throw new IllegalArgumentException("CDR cannot be null");
        }
        requireText(cdr.getCdrId(), "cdrId");
        requireText(cdr.getMsisdn(), "msisdn");
        if (cdr.getUsageType() == null) {
            throw new IllegalArgumentException("usageType is required");
        }
        if (cdr.getUsageStartTime() == null) {
            throw new IllegalArgumentException("usageStartTime is required");
        }
        validateUsageFields(cdr);
    }

    private void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }

    private void validateUsageFields(CdrDTO cdr) {
        switch (cdr.getUsageType()) {
            case CALL -> {
                if (cdr.getDurationMinutes() == null || cdr.getDurationMinutes() <= 0) {
                    throw new IllegalArgumentException("durationMinutes must be a positive number for CALL usage");
                }
            }
            case DATA -> {
                if (cdr.getDataUsedMb() == null || cdr.getDataUsedMb().signum() <= 0) {
                    throw new IllegalArgumentException("dataUsedMb must be a positive number for DATA usage");
                }
            }
            case SMS -> {
                if (cdr.getSmsCount() == null || cdr.getSmsCount() <= 0) {
                    throw new IllegalArgumentException("smsCount must be a positive number for SMS usage");
                }
            }
            default -> throw new IllegalArgumentException("Unsupported usage type: " + cdr.getUsageType());
        }
    }
}
