package com.gymtracker.app.service.impl;

import com.gymtracker.app.domain.RefreshToken;
import com.gymtracker.app.domain.User;
import com.gymtracker.app.dto.request.SignIn;
import com.gymtracker.app.dto.request.SignUp;
import com.gymtracker.app.dto.response.RefreshTokenResponse;
import com.gymtracker.app.dto.response.SignInResponse;
import com.gymtracker.app.exception.SessionExpiredException;
import com.gymtracker.app.exception.SignInException;
import com.gymtracker.app.exception.UserAlreadyExistsException;
import com.gymtracker.app.mapper.UserMapper;
import com.gymtracker.app.repository.RefreshTokenRepository;
import com.gymtracker.app.repository.UserRepository;
import com.gymtracker.app.security.JwtService;
import com.gymtracker.app.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public void signUp(SignUp signUp) {
        if (userRepository.existsByEmail(signUp.email()) || userRepository.existsByUsername(signUp.username()))
            throw new UserAlreadyExistsException("same-email-or-username");

        User user = userMapper.signUpToUser(signUp);
        user.updatePassword(passwordEncoder.encode(signUp.password()));

        userRepository.save(user);
    }

    @Override
    public SignInResponse signIn(SignIn signIn) {
        User user = userRepository.findByEmail(signIn.email())
                .orElseThrow(() -> new SignInException("email-or-password-incorrect"));

        if (!passwordEncoder.matches(signIn.password(), user.getPassword()))
            throw new SignInException("email-or-password-incorrect");

        String accessToken = jwtService.generateToken(user.getDisplayUsername(), user.getUsername());
        String refreshToken = UUID.randomUUID().toString();

        refreshTokenRepository.createRefreshToken(hashToken(refreshToken), user);

        return new SignInResponse(user.getDisplayUsername(), accessToken, refreshToken);
    }

    @Override
    @Transactional(noRollbackFor = SessionExpiredException.class)
    public RefreshTokenResponse refreshToken(String refreshToken) {
        RefreshToken refreshTokenDomain = refreshTokenRepository.getHashedRefreshToken(hashToken(refreshToken))
                .orElseThrow(() -> new SessionExpiredException("not-found"));

        if (refreshTokenDomain.isRevoked() || refreshTokenDomain.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.deleteById(refreshTokenDomain.getId());
            throw new SessionExpiredException("expired");
        }

        User user = refreshTokenDomain.getUser();

        refreshTokenRepository.deleteById(refreshTokenDomain.getId());
        String newRefreshToken = UUID.randomUUID().toString();
        refreshTokenRepository.createRefreshToken(hashToken(newRefreshToken), user);
        String newAccessToken = jwtService.generateToken(user.getDisplayUsername(), user.getUsername());

        return new RefreshTokenResponse(newAccessToken, newRefreshToken);
    }

    @Override
    @Transactional
    public void signOut(String refreshToken) {
        String tokenHash = hashToken(refreshToken);
        refreshTokenRepository.deleteByTokenHash(tokenHash);
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
