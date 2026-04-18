package com.gymtracker.app.exception;

import org.springframework.http.HttpStatus;

public class SessionExpiredException extends BaseKeyException {
    public SessionExpiredException(String subkey) {
        super("session-expired-exception." + subkey, HttpStatus.UNAUTHORIZED);
    }
}
