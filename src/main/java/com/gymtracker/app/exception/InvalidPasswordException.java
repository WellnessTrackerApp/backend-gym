package com.gymtracker.app.exception;

public class InvalidPasswordException extends DomainException {
    public InvalidPasswordException(String subkey) {
        super("invalid-password-exception." + subkey);
    }
}
