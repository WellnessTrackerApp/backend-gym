package com.gymtracker.app.controller;

import com.gymtracker.app.dto.request.RefreshTokenRequest;
import com.gymtracker.app.dto.request.SignIn;
import com.gymtracker.app.dto.request.SignUp;
import com.gymtracker.app.dto.response.MessageResponse;
import com.gymtracker.app.dto.response.RefreshTokenResponse;
import com.gymtracker.app.dto.response.SignInResponse;
import com.gymtracker.app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    private final MessageSource messageSource;

    @PostMapping("/sign-up")
    public ResponseEntity<MessageResponse> signUp(@Valid @RequestBody SignUp signUp) {
        authService.signUp(signUp);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse(
                        messageSource.getMessage("message-response.user-registered-successfully", null, LocaleContextHolder.getLocale())
                ));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(@Valid @RequestBody SignIn signIn) {
        SignInResponse response = authService.signIn(signIn);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshTokenResponse refreshTokenResponse = authService
                .refreshToken(refreshTokenRequest.refreshToken());

        return ResponseEntity.status(HttpStatus.OK)
                .body(refreshTokenResponse);
    }

    @PostMapping("/sign-out")
    public ResponseEntity<MessageResponse> signOut(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        authService.signOut(refreshTokenRequest.refreshToken());
        return ResponseEntity.ok(new MessageResponse(
                messageSource.getMessage("message-response.user-signed-out-successfully", null, LocaleContextHolder.getLocale())
        ));
    }
}
