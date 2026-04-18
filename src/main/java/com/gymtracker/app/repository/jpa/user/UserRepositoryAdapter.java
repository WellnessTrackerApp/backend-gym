package com.gymtracker.app.repository.jpa.user;

import com.gymtracker.app.domain.User;
import com.gymtracker.app.entity.UserEntity;
import com.gymtracker.app.mapper.UserMapper;
import com.gymtracker.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {
    private final SpringDataJpaUserRepository repository;
    private final UserMapper userMapper;

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<UserEntity> userEntity = repository.findByEmail(email);
        return userEntity.map(userMapper::userEntityToUser);
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return repository.findById(userId).map(userMapper::userEntityToUser);
    }

    @Override
    public Optional<User> findByIdWithoutCollections(UUID userId) {
        return repository.findById(userId).map(userMapper::userEntityToUserWithoutCollections);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public boolean existsById(UUID userId) {
        return repository.existsById(userId);
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = userMapper.userToUserEntity(user);
        UserEntity savedUserEntity = repository.save(userEntity);
        return userMapper.userEntityToUser(savedUserEntity);
    }
}
