package com.tbs.model.dto;

import com.tbs.model.enums.UsageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CdrDTO {

    @NotBlank
    private String cdrId;

    @NotBlank
    private String msisdn;

    @NotNull
    private UsageType usageType;

    @NotNull
    private LocalDateTime usageStartTime;

    private Integer durationMinutes;

    private BigDecimal dataUsedMb;

    private Integer smsCount;

    private String peerNumber;

    public String getCdrId() {
        return cdrId;
    }

    public void setCdrId(String cdrId) {
        this.cdrId = cdrId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public UsageType getUsageType() {
        return usageType;
    }

    public void setUsageType(UsageType usageType) {
        this.usageType = usageType;
    }

    public LocalDateTime getUsageStartTime() {
        return usageStartTime;
    }

    public void setUsageStartTime(LocalDateTime usageStartTime) {
        this.usageStartTime = usageStartTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getDataUsedMb() {
        return dataUsedMb;
    }

    public void setDataUsedMb(BigDecimal dataUsedMb) {
        this.dataUsedMb = dataUsedMb;
    }

    public Integer getSmsCount() {
        return smsCount;
    }

    public void setSmsCount(Integer smsCount) {
        this.smsCount = smsCount;
    }

    public String getPeerNumber() {
        return peerNumber;
    }

    public void setPeerNumber(String peerNumber) {
        this.peerNumber = peerNumber;
    }
}
