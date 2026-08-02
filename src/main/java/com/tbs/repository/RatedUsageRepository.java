package com.tbs.repository;

import com.tbs.model.entity.RatedUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatedUsageRepository extends JpaRepository<RatedUsage, Long> {

    Optional<RatedUsage> findByUsageRecordId(Long usageRecordId);
}
