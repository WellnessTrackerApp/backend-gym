package com.gymtracker.app.exception;

public class DuplicatedExercisesException extends DomainException {
    public DuplicatedExercisesException(String subkey) {
        super("duplicated-exercises-exception." + subkey);
    }
}
