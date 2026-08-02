package com.tbs.service;

import com.tbs.model.entity.Plan;
import com.tbs.model.enums.PlanType;
import com.tbs.repository.PlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlanService {

    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Transactional
    public Plan createPlan(Plan plan) {
        if (plan.getName() == null || plan.getName().isBlank()) {
            throw new IllegalArgumentException("Plan name is required");
        }
        if (planRepository.findByName(plan.getName()).isPresent()) {
            throw new IllegalArgumentException("Plan already exists with name: " + plan.getName());
        }
        return planRepository.save(plan);
    }

    public Plan getById(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found with id: " + id));
    }

    public Plan getByName(String name) {
        return planRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found with name: " + name));
    }

    public List<Plan> getAll() {
        return planRepository.findAll();
    }

    public List<Plan> getByType(PlanType planType) {
        return planRepository.findByPlanType(planType);
    }

    @Transactional
    public Plan update(Long id, Plan updates) {
        Plan plan = getById(id);
        if (updates.getName() != null && !updates.getName().isBlank()) {
            plan.setName(updates.getName());
        }
        if (updates.getPlanType() != null) {
            plan.setPlanType(updates.getPlanType());
        }
        if (updates.getMonthlyFee() != null) {
            plan.setMonthlyFee(updates.getMonthlyFee());
        }
        if (updates.getCallRatePerMinute() != null) {
            plan.setCallRatePerMinute(updates.getCallRatePerMinute());
        }
        if (updates.getDataRatePerMb() != null) {
            plan.setDataRatePerMb(updates.getDataRatePerMb());
        }
        if (updates.getSmsRate() != null) {
            plan.setSmsRate(updates.getSmsRate());
        }
        plan.setIncludedMinutes(updates.getIncludedMinutes());
        plan.setIncludedDataMb(updates.getIncludedDataMb());
        plan.setIncludedSms(updates.getIncludedSms());
        return planRepository.save(plan);
    }

    @Transactional
    public void delete(Long id) {
        if (!planRepository.existsById(id)) {
            throw new IllegalArgumentException("Plan not found with id: " + id);
        }
        planRepository.deleteById(id);
    }
}
