package com.gymtracker.app.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(UUID userId, String username, String email, Instant createdAt) {
}
