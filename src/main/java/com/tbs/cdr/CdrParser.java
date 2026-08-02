package com.tbs.cdr;

import com.tbs.model.dto.CdrDTO;
import com.tbs.model.enums.UsageType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Component
public class CdrParser {

    private static final int MIN_FIELDS = 4;
    private static final int IDX_CDR_ID = 0;
    private static final int IDX_MSISDN = 1;
    private static final int IDX_USAGE_TYPE = 2;
    private static final int IDX_START_TIME = 3;
    private static final int IDX_DURATION = 4;
    private static final int IDX_DATA_MB = 5;
    private static final int IDX_SMS_COUNT = 6;
    private static final int IDX_PEER = 7;

    public CdrDTO parse(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("CDR line cannot be blank");
        }
        String[] fields = line.split(",");
        if (fields.length < MIN_FIELDS) {
            throw new IllegalArgumentException("Invalid CDR format, expected at least " + MIN_FIELDS
                    + " fields but got " + fields.length + ": " + line);
        }

        CdrDTO dto = new CdrDTO();
        dto.setCdrId(trim(fields[IDX_CDR_ID]));
        dto.setMsisdn(trim(fields[IDX_MSISDN]));
        dto.setUsageType(parseUsageType(fields[IDX_USAGE_TYPE], line));
        dto.setUsageStartTime(parseStartTime(fields[IDX_START_TIME], line));
        dto.setDurationMinutes(parseIntField(fields, IDX_DURATION, line));
        dto.setDataUsedMb(parseDecimalField(fields, IDX_DATA_MB, line));
        dto.setSmsCount(parseIntField(fields, IDX_SMS_COUNT, line));
        dto.setPeerNumber(fields.length > IDX_PEER ? trim(fields[IDX_PEER]) : null);
        return dto;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private UsageType parseUsageType(String value, String line) {
        try {
            return UsageType.valueOf(trim(value).toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid usage type '" + value + "' in CDR: " + line, e);
        }
    }

    private LocalDateTime parseStartTime(String value, String line) {
        try {
            return LocalDateTime.parse(trim(value));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid start time '" + value + "' in CDR: " + line, e);
        }
    }

    private Integer parseIntField(String[] fields, int index, String line) {
        if (fields.length <= index || fields[index].isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(trim(fields[index]));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid integer value '" + fields[index]
                    + "' in CDR: " + line, e);
        }
    }

    private BigDecimal parseDecimalField(String[] fields, int index, String line) {
        if (fields.length <= index || fields[index].isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(trim(fields[index]));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid decimal value '" + fields[index]
                    + "' in CDR: " + line, e);
        }
    }
}
