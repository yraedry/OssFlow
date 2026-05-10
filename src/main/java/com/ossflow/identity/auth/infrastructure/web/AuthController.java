package com.ossflow.identity.auth.infrastructure.web;

import com.ossflow.identity.auth.application.AuthService;
import com.ossflow.identity.auth.infrastructure.web.dto.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String REFRESH_COOKIE_NAME = "refresh_token";
    private static final int REFRESH_TTL = 604800;

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletResponse response) {
        AuthService.LoginResult result = authService.login(request);
        setRefreshCookie(response, result.rawRefreshToken(), REFRESH_TTL);
        AuthResponse body = new AuthResponse(
                result.accessToken(),
                new AuthResponse.UserDto(result.account().id(), result.account().email())
        );
        return ResponseEntity.ok(body);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String rawToken = extractRefreshCookie(request);
        if (rawToken != null) {
            authService.logout(rawToken);
        }
        setRefreshCookie(response, "", 0);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(HttpServletRequest request,
                                                   HttpServletResponse response) {
        String rawToken = extractRefreshCookie(request);
        if (rawToken == null) {
            return ResponseEntity.status(401).build();
        }
        AuthService.RefreshResult result = authService.refresh(rawToken);
        setRefreshCookie(response, result.rawRefreshToken(), REFRESH_TTL);
        return ResponseEntity.ok(new RefreshResponse(result.accessToken()));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(@RequestBody ForgotPasswordRequest request) {
        authService.resendVerification(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok().build();
    }

    private void setRefreshCookie(HttpServletResponse response, String value, int maxAge) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/auth/refresh");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    private String extractRefreshCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> REFRESH_COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
