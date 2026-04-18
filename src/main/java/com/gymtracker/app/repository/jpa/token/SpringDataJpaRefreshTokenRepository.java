package com.gymtracker.app.repository.jpa.token;

import com.gymtracker.app.entity.RefreshTokenEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataJpaRefreshTokenRepository extends CrudRepository<RefreshTokenEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RefreshTokenEntity> getRefreshTokenEntityByTokenHash(String tokenHash);
    void deleteByTokenHash(String tokenHash);
}
