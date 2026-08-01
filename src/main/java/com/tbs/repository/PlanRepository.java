package com.tbs.repository;

import com.tbs.model.entity.Plan;
import com.tbs.model.enums.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    Optional<Plan> findByName(String name);

    List<Plan> findByPlanType(PlanType planType);
}
