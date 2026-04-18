package com.gymtracker.app.exception;

public class UserAlreadyExistsException extends DomainException {
    public UserAlreadyExistsException(String subkey) {
        super("user-already-exists-exception." + subkey);
    }
}
