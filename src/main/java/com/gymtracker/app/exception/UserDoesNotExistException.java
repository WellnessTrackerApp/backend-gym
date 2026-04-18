package com.gymtracker.app.exception;

import org.springframework.http.HttpStatus;

public class UserDoesNotExistException extends DomainException {
    public UserDoesNotExistException(String subkey) {
        super("user-does-not-exist-exception." + subkey, HttpStatus.NOT_FOUND);
    }
}
