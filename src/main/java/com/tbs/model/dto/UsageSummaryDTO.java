package com.tbs.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UsageSummaryDTO {

    private Long subscriberId;
    private String msisdn;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private long callCount;
    private long totalMinutes;
    private long smsCount;
    private BigDecimal totalDataMb;

    public Long getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(Long subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public long getCallCount() {
        return callCount;
    }

    public void setCallCount(long callCount) {
        this.callCount = callCount;
    }

    public long getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(long totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public long getSmsCount() {
        return smsCount;
    }

    public void setSmsCount(long smsCount) {
        this.smsCount = smsCount;
    }

    public BigDecimal getTotalDataMb() {
        return totalDataMb;
    }

    public void setTotalDataMb(BigDecimal totalDataMb) {
        this.totalDataMb = totalDataMb;
    }
}
