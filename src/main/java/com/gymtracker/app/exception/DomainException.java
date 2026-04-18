package com.gymtracker.app.exception;

import org.springframework.http.HttpStatus;

public class DomainException extends BaseKeyException {
    public DomainException(String subkey, Object... args) {
        super("domain-exception." + subkey, HttpStatus.BAD_REQUEST, args);
    }

    public DomainException(String subkey, HttpStatus status, Object... args) {
        super("domain-exception." + subkey, status, args);
    }
}
