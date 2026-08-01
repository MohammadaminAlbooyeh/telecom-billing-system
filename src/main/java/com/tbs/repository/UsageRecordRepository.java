package com.tbs.repository;

import com.tbs.model.entity.UsageRecord;
import com.tbs.model.enums.UsageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UsageRecordRepository extends JpaRepository<UsageRecord, Long> {

    boolean existsByCdrId(String cdrId);

    List<UsageRecord> findBySubscriberIdAndRatedTrueAndUsageStartTimeBetween(
            Long subscriberId, LocalDateTime start, LocalDateTime end);

    long countBySubscriberIdAndRatedFalse(Long subscriberId);
}
