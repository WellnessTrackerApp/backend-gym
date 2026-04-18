package com.gymtracker.app.domain;

import com.gymtracker.app.exception.InvalidPasswordException;

public record Password(String hashedPassword) {
    public Password {
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            throw new InvalidPasswordException("hashed-password-null");
        }
    }
}
