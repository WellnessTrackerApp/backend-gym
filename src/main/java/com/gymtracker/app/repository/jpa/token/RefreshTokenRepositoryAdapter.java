package com.gymtracker.app.repository.jpa.token;

import com.gymtracker.app.domain.RefreshToken;
import com.gymtracker.app.domain.User;
import com.gymtracker.app.entity.RefreshTokenEntity;
import com.gymtracker.app.entity.UserEntity;
import com.gymtracker.app.mapper.RefreshTokenMapper;
import com.gymtracker.app.mapper.UserMapper;
import com.gymtracker.app.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {
    private static final int EXPIRATION_DAYS = 30;

    private final SpringDataJpaRefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;
    private final RefreshTokenMapper refreshTokenMapper;

    @Override
    public void createRefreshToken(String refreshTokenHash, User user) {
        UserEntity userEntity = userMapper.userToUserEntity(user);

        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .user(userEntity)
                .tokenHash(refreshTokenHash)
                .expiresAt(Instant.now().plus(EXPIRATION_DAYS, ChronoUnit.DAYS))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);
    }

    @Override
    public Optional<RefreshToken> getHashedRefreshToken(String hashedRefreshToken) {
        Optional<RefreshTokenEntity> refreshTokenEntity = refreshTokenRepository
                .getRefreshTokenEntityByTokenHash(hashedRefreshToken);
        return refreshTokenEntity.map(refreshTokenMapper::refreshTokenEntityToRefreshToken);
    }

    @Override
    public void deleteById(UUID tokenId) {
        refreshTokenRepository.deleteById(tokenId);
    }

    @Override
    public void deleteByTokenHash(String tokenHash) {
        refreshTokenRepository.deleteByTokenHash(tokenHash);
    }
}
