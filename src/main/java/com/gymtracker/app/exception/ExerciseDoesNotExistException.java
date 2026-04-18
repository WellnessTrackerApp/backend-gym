package com.gymtracker.app.exception;

import org.springframework.http.HttpStatus;

public class ExerciseDoesNotExistException extends DomainException {
    public ExerciseDoesNotExistException(String subkey, Object... args) {
        super("exercise-does-not-exist-exception." + subkey, HttpStatus.NOT_FOUND, args);
    }
}
