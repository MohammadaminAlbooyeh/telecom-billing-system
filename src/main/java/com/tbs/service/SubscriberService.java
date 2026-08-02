package com.tbs.service;

import com.tbs.exception.SubscriberNotFoundException;
import com.tbs.model.entity.Plan;
import com.tbs.model.entity.Subscriber;
import com.tbs.repository.PlanRepository;
import com.tbs.repository.SubscriberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final PlanRepository planRepository;
    private final BalanceService balanceService;

    public SubscriberService(SubscriberRepository subscriberRepository,
                             PlanRepository planRepository,
                             BalanceService balanceService) {
        this.subscriberRepository = subscriberRepository;
        this.planRepository = planRepository;
        this.balanceService = balanceService;
    }

    @Transactional
    public Subscriber register(String msisdn, String name, String email, Long planId) {
        if (subscriberRepository.existsByMsisdn(msisdn)) {
            throw new IllegalArgumentException("Subscriber already exists with msisdn: " + msisdn);
        }
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found with id: " + planId));
        Subscriber subscriber = new Subscriber();
        subscriber.setMsisdn(msisdn);
        subscriber.setName(name);
        subscriber.setEmail(email);
        subscriber.setPlan(plan);
        subscriber.setActive(true);
        subscriber = subscriberRepository.save(subscriber);
        balanceService.initializeBalance(subscriber);
        return subscriber;
    }

    public Subscriber getById(Long id) {
        return subscriberRepository.findById(id)
                .orElseThrow(() -> new SubscriberNotFoundException(id));
    }

    public Subscriber getByMsisdn(String msisdn) {
        return subscriberRepository.findByMsisdn(msisdn)
                .orElseThrow(() -> new SubscriberNotFoundException(msisdn));
    }

    public List<Subscriber> getAll() {
        return subscriberRepository.findAll();
    }

    public boolean existsByMsisdn(String msisdn) {
        return subscriberRepository.existsByMsisdn(msisdn);
    }

    @Transactional
    public Subscriber update(Long id, String name, String email, Long planId) {
        Subscriber subscriber = getById(id);
        if (name != null) {
            subscriber.setName(name);
        }
        if (email != null) {
            subscriber.setEmail(email);
        }
        if (planId != null) {
            Plan plan = planRepository.findById(planId)
                    .orElseThrow(() -> new IllegalArgumentException("Plan not found with id: " + planId));
            subscriber.setPlan(plan);
        }
        return subscriberRepository.save(subscriber);
    }

    @Transactional
    public Subscriber setActive(Long id, boolean active) {
        Subscriber subscriber = getById(id);
        subscriber.setActive(active);
        return subscriberRepository.save(subscriber);
    }
}
