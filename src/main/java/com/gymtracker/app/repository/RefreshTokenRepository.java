package com.gymtracker.app.repository;

import com.gymtracker.app.domain.RefreshToken;
import com.gymtracker.app.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    void createRefreshToken(String refreshTokenHash, User user);
    Optional<RefreshToken> getHashedRefreshToken(String hashedRefreshToken);
    void deleteById(UUID refreshTokenId);
    void deleteByTokenHash(String tokenHash);
}
