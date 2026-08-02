package com.tbs.exception;

public class SubscriberNotFoundException extends RuntimeException {

    public SubscriberNotFoundException(Long id) {
        super("Subscriber not found with id: " + id);
    }

    public SubscriberNotFoundException(String msisdn) {
        super("Subscriber not found with msisdn: " + msisdn);
    }
}
