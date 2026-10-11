package com.tbs.controller;

import com.tbs.model.entity.Balance;
import com.tbs.model.entity.Subscriber;
import com.tbs.service.BalanceService;
import com.tbs.service.SubscriberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/subscribers")
public class SubscriberController {

    private final SubscriberService subscriberService;
    private final BalanceService balanceService;

    public SubscriberController(SubscriberService subscriberService, BalanceService balanceService) {
        this.subscriberService = subscriberService;
        this.balanceService = balanceService;
    }

    @PostMapping
    public ResponseEntity<Subscriber> register(@RequestBody RegisterSubscriberRequest request) {
        Subscriber subscriber = subscriberService.register(
                request.msisdn(), request.firstName(), request.lastName(), request.email(), request.planId(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriber);
    }

    @GetMapping
    public List<Subscriber> getAll() {
        return subscriberService.getAll();
    }

    @GetMapping("/{id}")
    public Subscriber getById(@PathVariable Long id) {
        return subscriberService.getById(id);
    }

    @GetMapping("/msisdn/{msisdn}")
    public Subscriber getByMsisdn(@PathVariable String msisdn) {
        return subscriberService.getByMsisdn(msisdn);
    }

    @PutMapping("/{id}")
    public Subscriber update(@PathVariable Long id, @RequestBody UpdateSubscriberRequest request) {
        return subscriberService.update(id, request.firstName(), request.lastName(), request.email(), request.planId());
    }

    @PatchMapping("/{id}/active")
    public Subscriber setActive(@PathVariable Long id, @RequestParam boolean active) {
        return subscriberService.setActive(id, active);
    }

    @PostMapping("/{id}/topup")
    public Balance topUp(@PathVariable Long id, @RequestBody TopUpRequest request) {
        return balanceService.topUp(id, request.amount());
    }

    @GetMapping("/{id}/balance")
    public Balance getBalance(@PathVariable Long id) {
        return balanceService.getBalance(id);
    }

    public record RegisterSubscriberRequest(String msisdn, String firstName, String lastName, String email, Long planId, String password) {
    }

    public record UpdateSubscriberRequest(String firstName, String lastName, String email, Long planId) {
    }

    public record TopUpRequest(BigDecimal amount) {
    }
}