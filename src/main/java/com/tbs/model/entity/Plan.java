package com.tbs.model.entity;

import com.tbs.model.enums.PlanType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "plans")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false, length = 20)
    private PlanType planType;

    @Column(name = "monthly_fee", precision = 10, scale = 2)
    private BigDecimal monthlyFee;

    @Column(name = "included_minutes", nullable = false)
    private int includedMinutes;

    @Column(name = "included_data_mb", nullable = false)
    private int includedDataMb;

    @Column(name = "included_sms", nullable = false)
    private int includedSms;

    @Column(name = "call_rate_per_minute", precision = 10, scale = 4)
    private BigDecimal callRatePerMinute;

    @Column(name = "data_rate_per_mb", precision = 10, scale = 4)
    private BigDecimal dataRatePerMb;

    @Column(name = "sms_rate", precision = 10, scale = 4)
    private BigDecimal smsRate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }

    public BigDecimal getMonthlyFee() {
        return monthlyFee;
    }

    public void setMonthlyFee(BigDecimal monthlyFee) {
        this.monthlyFee = monthlyFee;
    }

    public int getIncludedMinutes() {
        return includedMinutes;
    }

    public void setIncludedMinutes(int includedMinutes) {
        this.includedMinutes = includedMinutes;
    }

    public int getIncludedDataMb() {
        return includedDataMb;
    }

    public void setIncludedDataMb(int includedDataMb) {
        this.includedDataMb = includedDataMb;
    }

    public int getIncludedSms() {
        return includedSms;
    }

    public void setIncludedSms(int includedSms) {
        this.includedSms = includedSms;
    }

    public BigDecimal getCallRatePerMinute() {
        return callRatePerMinute;
    }

    public void setCallRatePerMinute(BigDecimal callRatePerMinute) {
        this.callRatePerMinute = callRatePerMinute;
    }

    public BigDecimal getDataRatePerMb() {
        return dataRatePerMb;
    }

    public void setDataRatePerMb(BigDecimal dataRatePerMb) {
        this.dataRatePerMb = dataRatePerMb;
    }

    public BigDecimal getSmsRate() {
        return smsRate;
    }

    public void setSmsRate(BigDecimal smsRate) {
        this.smsRate = smsRate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
