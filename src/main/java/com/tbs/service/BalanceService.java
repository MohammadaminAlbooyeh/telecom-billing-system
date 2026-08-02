package com.tbs.service;

import com.tbs.exception.InsufficientBalanceException;
import com.tbs.model.entity.Balance;
import com.tbs.model.entity.Subscriber;
import com.tbs.model.enums.PlanType;
import com.tbs.repository.BalanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BalanceService {

    private final BalanceRepository balanceRepository;

    public BalanceService(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    public Balance getBalance(Long subscriberId) {
        return balanceRepository.findBySubscriberId(subscriberId)
                .orElseThrow(() -> new IllegalArgumentException("Balance not found for subscriber id: " + subscriberId));
    }

    @Transactional
    public Balance initializeBalance(Subscriber subscriber) {
        return balanceRepository.findBySubscriberId(subscriber.getId())
                .orElseGet(() -> {
                    Balance balance = new Balance();
                    balance.setSubscriber(subscriber);
                    balance.setPrepaidAmount(BigDecimal.ZERO);
                    balance.setCreditLimit(BigDecimal.ZERO);
                    return balanceRepository.save(balance);
                });
    }

    @Transactional
    public Balance topUp(Long subscriberId, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Top-up amount must be a positive number");
        }
        Balance balance = getBalance(subscriberId);
        balance.setPrepaidAmount(balance.getPrepaidAmount().add(amount));
        return balanceRepository.save(balance);
    }

    @Transactional
    public Balance applyCharge(Subscriber subscriber, BigDecimal charge) {
        if (charge == null || charge.signum() <= 0) {
            return getOrCreateBalance(subscriber);
        }
        Balance balance = getOrCreateBalance(subscriber);
        if (subscriber.getPlan().getPlanType() == PlanType.PREPAID) {
            if (balance.getPrepaidAmount().compareTo(charge) < 0) {
                throw new InsufficientBalanceException(subscriber.getMsisdn(), balance.getPrepaidAmount(), charge);
            }
            balance.setPrepaidAmount(balance.getPrepaidAmount().subtract(charge));
        } else {
            BigDecimal updated = balance.getPrepaidAmount().subtract(charge);
            if (balance.getCreditLimit() != null && updated.negate().compareTo(balance.getCreditLimit()) > 0) {
                throw new InsufficientBalanceException(subscriber.getMsisdn(), balance.getCreditLimit(), charge);
            }
            balance.setPrepaidAmount(updated);
        }
        return balanceRepository.save(balance);
    }

    private Balance getOrCreateBalance(Subscriber subscriber) {
        return balanceRepository.findBySubscriberId(subscriber.getId())
                .orElseGet(() -> initializeBalance(subscriber));
    }
}
