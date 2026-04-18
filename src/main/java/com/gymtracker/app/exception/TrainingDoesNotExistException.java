package com.gymtracker.app.exception;

import org.springframework.http.HttpStatus;

public class TrainingDoesNotExistException extends DomainException {
    public TrainingDoesNotExistException(String subkey) {
        super("training-does-not-exist-exception." + subkey, HttpStatus.NOT_FOUND);
    }
}
