package com.gymtracker.app.exception;

public class SignInException extends DomainException {
    public SignInException(String subkey) {
        super("sign-in-exception." + subkey);
    }
}
