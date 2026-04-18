package com.gymtracker.app.integration.auth;

import com.gymtracker.app.dto.request.RefreshTokenRequest;
import com.gymtracker.app.dto.request.SignIn;
import com.gymtracker.app.dto.request.SignUp;
import com.gymtracker.app.entity.RefreshTokenEntity;
import com.gymtracker.app.entity.UserEntity;
import com.gymtracker.app.integration.BaseIntegrationTest;
import com.gymtracker.app.repository.jpa.token.SpringDataJpaRefreshTokenRepository;
import com.gymtracker.app.repository.jpa.user.SpringDataJpaUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserAuthTest extends BaseIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private SpringDataJpaUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SpringDataJpaRefreshTokenRepository refreshTokenRepository;

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(webTestClient);
    }

    @Test
    void givenValidUserDetails_whenRegisteringUser_thenSuccess() {
        SignUp signUp = SignUp.builder()
                .username("testuser")
                .email("test.user@domain.com")
                .password("testuser123")
                .build();

        webTestClient.post().uri("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signUp)
                .exchange()
                .expectStatus()
                .isCreated();

        UserEntity user = userRepository.findAll().iterator().next();

        Assertions.assertNotNull(user);
        Assertions.assertEquals(signUp.username(), user.getUsername());
        Assertions.assertNotEquals(signUp.password(), user.getPasswordHash());
    }

    @Test
    void givenUserAlreadyCreated_whenRegisteringUserWithSameEmail_thenThrowsException() {
        SignUp signUp = SignUp.builder()
                .username("testuser")
                .email("test.user@domain.com")
                .password("testuser123")
                .build();

        UserEntity user = UserEntity.builder()
                .username("testuser")
                .email("test.user@domain.com")
                .passwordHash(passwordEncoder.encode("testuser123"))
                .createdAt(Instant.now())
                .build();

        userRepository.save(user);

        webTestClient.post().uri("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signUp)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void givenValidUserDetails_whenLoggingUser_thenReturnsAccessAndRefreshTokens() {
        String plainPassword = "testuser123";
        String hashedPassword = passwordEncoder.encode(plainPassword);

        SignIn signIn = SignIn.builder()
                .email("test.user@domain.com")
                .password(plainPassword)
                .build();

        UserEntity user = UserEntity.builder()
                .email("test.user@domain.com")
                .createdAt(Instant.now())
                .username("testuser")
                .passwordHash(hashedPassword)
                .build();

        userRepository.save(user);

        webTestClient.post().uri("/auth/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signIn)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.accessToken").exists()
                .jsonPath("$.refreshToken").exists();
    }

    @Test
    void givenRefreshTokenRequest_whenRefreshingToken_thenReturnsNewAccessToken() {
        UserEntity user = UserEntity.builder()
                .username("testuser")
                .email("testuser@domain.com")
                .passwordHash(passwordEncoder.encode("testuser123"))
                .createdAt(Instant.now())
                .build();

        user = userRepository.save(user);

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest("some-refresh-token");

        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .tokenHash(hashToken(refreshTokenRequest.refreshToken()))
                .revoked(false)
                .expiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .user(user)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

        webTestClient.post()
                .uri("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(refreshTokenRequest)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.accessToken").exists()
                .jsonPath("$.refreshToken").exists();

        Assertions.assertTrue(refreshTokenRepository.findAll().iterator().hasNext());
        RefreshTokenEntity storedToken = refreshTokenRepository.findAll().iterator().next();
        Assertions.assertNotEquals(refreshTokenEntity.getTokenHash(), storedToken.getTokenHash());
    }

    @Test
    void givenValidRefreshToken_whenLoggingOut_thenRemovesRefreshToken() {
        UserEntity user = UserEntity.builder()
                .username("testuser")
                .email("testuser@domain.com")
                .passwordHash(passwordEncoder.encode("testuser123"))
                .createdAt(Instant.now())
                .build();
        user = userRepository.save(user);

        String rawRefreshToken = "some-refresh-token";
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .tokenHash(hashToken(rawRefreshToken))
                .revoked(false)
                .expiresAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .user(user)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        webTestClient.post()
                .uri("/auth/sign-out")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new RefreshTokenRequest(rawRefreshToken))
                .exchange()
                .expectStatus()
                .isOk();

        Assertions.assertFalse(refreshTokenRepository.findAll().iterator().hasNext());
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }
}
