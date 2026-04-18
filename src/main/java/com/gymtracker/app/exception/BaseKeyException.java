package com.gymtracker.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseKeyException extends RuntimeException {
    private final String key;
    private final HttpStatus status;
    private final Object[] args;

    public BaseKeyException(String key, HttpStatus status, Object... args) {
        super(key);
        this.key = key;
        this.status = status;
        this.args = args;
    }
}
