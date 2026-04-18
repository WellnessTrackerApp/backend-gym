package com.gymtracker.app.dto.response;

import org.springframework.http.HttpStatus;

public record ErrorResponse(Integer statusCode, String message) {
    public ErrorResponse(HttpStatus httpStatus, String message) {
        this(httpStatus.value(), message);
    }
}
