package com.tbs.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rated_usage")
public class RatedUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usage_record_id", nullable = false, unique = true)
    private UsageRecord usageRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriber_id", nullable = false)
    private Subscriber subscriber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(name = "base_charge", precision = 12, scale = 2)
    private BigDecimal baseCharge;

    @Column(name = "overage_charge", precision = 12, scale = 2)
    private BigDecimal overageCharge;

    @Column(name = "total_charge", precision = 12, scale = 2)
    private BigDecimal totalCharge;

    @Column(name = "rated_at", nullable = false)
    private LocalDateTime ratedAt;

    @PrePersist
    protected void onCreate() {
        ratedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsageRecord getUsageRecord() {
        return usageRecord;
    }

    public void setUsageRecord(UsageRecord usageRecord) {
        this.usageRecord = usageRecord;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public BigDecimal getBaseCharge() {
        return baseCharge;
    }

    public void setBaseCharge(BigDecimal baseCharge) {
        this.baseCharge = baseCharge;
    }

    public BigDecimal getOverageCharge() {
        return overageCharge;
    }

    public void setOverageCharge(BigDecimal overageCharge) {
        this.overageCharge = overageCharge;
    }

    public BigDecimal getTotalCharge() {
        return totalCharge;
    }

    public void setTotalCharge(BigDecimal totalCharge) {
        this.totalCharge = totalCharge;
    }

    public LocalDateTime getRatedAt() {
        return ratedAt;
    }
}
