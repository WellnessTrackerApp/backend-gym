package com.gymtracker.app.exception;

public class PlanWithSameNameAlreadyExistsException extends DomainException {
    public PlanWithSameNameAlreadyExistsException(String subkey) {
        super("plan-with-same-name-already-exists-exception." + subkey);
    }
}
