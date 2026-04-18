package com.gymtracker.app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SignUp(
        @NotBlank(message = "{sign-up.username.blank}")
        @Size(min = 2, max = 25, message = "{sign-up.username.size}")
        String username,

        @NotBlank(message = "{sign-up.email.blank}")
        @Email(message = "{sign-up.email.pattern}")
        String email,

        @NotBlank(message = "{sign-up.password.blank}")
        @Size(min = 8, max = 128, message = "{sign-up.password.size}")
        String password) {
}
