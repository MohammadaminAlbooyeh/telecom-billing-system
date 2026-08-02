package com.tbs.controller;

import com.tbs.model.entity.Plan;
import com.tbs.model.enums.PlanType;
import com.tbs.service.PlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @PostMapping
    public ResponseEntity<Plan> create(@RequestBody Plan plan) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planService.createPlan(plan));
    }

    @GetMapping
    public List<Plan> getAll() {
        return planService.getAll();
    }

    @GetMapping("/{id}")
    public Plan getById(@PathVariable Long id) {
        return planService.getById(id);
    }

    @GetMapping("/name/{name}")
    public Plan getByName(@PathVariable String name) {
        return planService.getByName(name);
    }

    @GetMapping("/type/{planType}")
    public List<Plan> getByType(@PathVariable PlanType planType) {
        return planService.getByType(planType);
    }

    @PutMapping("/{id}")
    public Plan update(@PathVariable Long id, @RequestBody Plan plan) {
        return planService.update(id, plan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        planService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
