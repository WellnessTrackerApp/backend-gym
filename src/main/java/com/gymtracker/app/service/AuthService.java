package com.gymtracker.app.service;

import com.gymtracker.app.dto.request.SignIn;
import com.gymtracker.app.dto.request.SignUp;
import com.gymtracker.app.dto.response.RefreshTokenResponse;
import com.gymtracker.app.dto.response.SignInResponse;

import java.util.UUID;

public interface AuthService {
    UUID signUp(SignUp signUp);
    SignInResponse signIn(SignIn signIn);
    RefreshTokenResponse refreshToken(String refreshToken);
    void signOut(String refreshToken);
}
