package com.gymtracker.app.unit;

import com.gymtracker.app.domain.RefreshToken;
import com.gymtracker.app.domain.User;
import com.gymtracker.app.exception.SessionExpiredException;
import com.gymtracker.app.mapper.UserMapper;
import com.gymtracker.app.repository.RefreshTokenRepository;
import com.gymtracker.app.repository.UserRepository;
import com.gymtracker.app.security.JwtService;
import com.gymtracker.app.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@Import(UserMapper.class)
class AuthServiceTest {
    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Spy
    private UserMapper userMapper;

    @Spy
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(authService);
    }

    @Test
    void givenExpiredRefreshToken_whenRefreshTokenCalled_shouldThrowSessionExpiredException() {
        String hashedToken = "someHashedToken";

        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(hashedToken)
                .expiresAt(Instant.now().minusSeconds(60))
                .build();

        Mockito.when(refreshTokenRepository.getHashedRefreshToken(any()))
                .thenReturn(Optional.of(refreshToken));

        Assertions.assertThrows(SessionExpiredException.class, () -> {
            authService.refreshToken(hashedToken);
        });
    }

    @Test
    void givenNonExistingRefreshToken_whenRefreshTokenCalled_shouldThrowSessionExpiredException() {
        String hashedToken = "someHashedToken";

        Mockito.when(refreshTokenRepository.getHashedRefreshToken(any()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(SessionExpiredException.class, () -> {
            authService.refreshToken(hashedToken);
        });
    }

    @Test
    void givenValidRefreshToken_whenRefreshTokenCalled_shouldReturnNewTokens() {
        String hashedToken = "someHashedToken";
        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(hashedToken)
                .expiresAt(Instant.now().plusSeconds(3600))
                .user(User.builder().userId(UUID.randomUUID()).username("testuser123").build())
                .build();

        Mockito.when(refreshTokenRepository.getHashedRefreshToken(any()))
                .thenReturn(Optional.of(refreshToken));

        var response = authService.refreshToken(hashedToken);

        Assertions.assertNotNull(response);
        Mockito.verify(refreshTokenRepository).deleteById(any());
        Mockito.verify(jwtService).generateToken(any(), any());
        Assertions.assertNotNull(response.refreshToken());
    }

    @Test
    void givenValidRefreshToken_whenSignOutCalled_shouldDeleteRefreshToken() {
        String refreshToken = "some-refresh-token";

        authService.signOut(refreshToken);

        Mockito.verify(refreshTokenRepository).deleteByTokenHash(any());
    }
}
