package com.gymtracker.app.repository;

import com.gymtracker.app.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID userId);
    Optional<User> findByIdWithoutCollections(UUID userId);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsById(UUID userId);
    User save(User user);

}
