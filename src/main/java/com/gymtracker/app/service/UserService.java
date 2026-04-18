package com.gymtracker.app.service;

import com.gymtracker.app.dto.response.UserProfileResponse;

import java.util.UUID;

public interface UserService {
    UserProfileResponse getUserProfile(UUID userId);
}
