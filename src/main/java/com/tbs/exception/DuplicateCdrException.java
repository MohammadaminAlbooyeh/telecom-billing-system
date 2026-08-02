package com.tbs.exception;

public class DuplicateCdrException extends RuntimeException {

    public DuplicateCdrException(String cdrId) {
        super("Duplicate CDR detected with id: " + cdrId);
    }
}
