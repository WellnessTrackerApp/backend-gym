package com.gymtracker.app.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record RefreshTokenRequest(
        @NotEmpty(message = "{refresh-token.empty}")
        String refreshToken) {
}
