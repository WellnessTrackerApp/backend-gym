package com.gymtracker.app.exception;

import org.springframework.http.HttpStatus;

public class InvalidPeriodException extends BaseKeyException {
    public InvalidPeriodException(String subkey) {
        super("invalid-period-exception." + subkey, HttpStatus.BAD_REQUEST);
    }
}
