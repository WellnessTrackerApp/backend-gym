package com.gymtracker.app.exception;

import org.springframework.http.HttpStatus;

public class ExerciseAlreadyExistsException extends DomainException {
    public ExerciseAlreadyExistsException(String subkey, Object... args) {
        super("exercise-already-exists-exception." + subkey, HttpStatus.CONFLICT, args);
    }
}
