package com.tbs.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(String msisdn, BigDecimal available, BigDecimal required) {
        super("Insufficient balance for subscriber " + msisdn
                + ": available " + available + ", required " + required);
    }
}
