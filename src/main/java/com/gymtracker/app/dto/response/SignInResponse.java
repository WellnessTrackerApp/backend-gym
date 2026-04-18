package com.gymtracker.app.dto.response;

import lombok.Builder;

@Builder
public record SignInResponse(String username, String accessToken, String refreshToken) {
}
