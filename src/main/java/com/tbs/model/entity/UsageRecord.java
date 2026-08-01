package com.tbs.model.entity;

import com.tbs.model.enums.UsageType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "usage_records")
public class UsageRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cdr_id", nullable = false, unique = true, length = 64)
    private String cdrId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriber_id", nullable = false)
    private Subscriber subscriber;

    @Enumerated(EnumType.STRING)
    @Column(name = "usage_type", nullable = false, length = 10)
    private UsageType usageType;

    @Column(name = "usage_start_time", nullable = false)
    private LocalDateTime usageStartTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "data_used_mb", precision = 10, scale = 2)
    private BigDecimal dataUsedMb;

    @Column(name = "sms_count")
    private Integer smsCount;

    @Column(nullable = false)
    private boolean rated;

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

    public String getCdrId() {
        return cdrId;
    }

    public void setCdrId(String cdrId) {
        this.cdrId = cdrId;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
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

    public boolean isRated() {
        return rated;
    }

    public void setRated(boolean rated) {
        this.rated = rated;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
