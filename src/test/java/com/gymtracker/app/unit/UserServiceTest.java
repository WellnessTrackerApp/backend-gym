package com.gymtracker.app.unit;

import com.gymtracker.app.domain.User;
import com.gymtracker.app.exception.UserDoesNotExistException;
import com.gymtracker.app.mapper.UserMapper;
import com.gymtracker.app.repository.UserRepository;
import com.gymtracker.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void contextLoads() {
        Assertions.assertNotNull(userService);
    }

    @Test
    void givenNonExistingUserId_whenGetUserProfileCalled_shouldThrowException() {
        UUID nonExistingUserId = UUID.randomUUID();

        Mockito.when(userRepository.findByIdWithoutCollections(nonExistingUserId))
                .thenReturn(Optional.empty());


        Assertions.assertThrows(UserDoesNotExistException.class, () -> {
            userService.getUserProfile(nonExistingUserId);
        });
    }

    @Test
    void givenExistingUserId_whenGetUserProfileCalled_shouldReturnUserProfile() {
        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .userId(userId)
                .username("johndoe")
                .email("john.doe@domain.com")
                .createdAt(Instant.now())
                .build();

        Mockito.when(userRepository.findByIdWithoutCollections(userId))
                .thenReturn(Optional.of(user));

        var response = userService.getUserProfile(userId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(userId, response.userId());
        Assertions.assertEquals(user.getEmail(), response.email());
        Assertions.assertEquals(user.getDisplayUsername(), response.username());
    }

}
