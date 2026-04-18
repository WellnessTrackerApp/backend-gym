package com.gymtracker.app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SignIn(
        @NotBlank(message = "{sign-in.email.blank}")
        @Email(message = "{sign-in.email.pattern}")
        String email,

        @NotBlank(message = "{sign-in.password.blank}")
        String password) {
}
