package com.ossflow.identity.auth.infrastructure.web;

import com.ossflow.identity.auth.application.AuthService;
import com.ossflow.identity.auth.infrastructure.web.dto.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Arrays;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String REFRESH_COOKIE_NAME = "refresh_token";

    private final AuthService authService;
    private final boolean cookieSecure;
    private final String cookieSameSite;
    private final String cookiePath;
    private final long refreshExpirySeconds;

    public AuthController(AuthService authService,
                          @Value("${app.cookie.secure:true}") boolean cookieSecure,
                          @Value("${app.cookie.same-site:Lax}") String cookieSameSite,
                          @Value("${app.cookie.path:/api/auth}") String cookiePath,
                          @Value("${app.refresh-token.expiry:2592000}") long refreshExpirySeconds) {
        this.authService = authService;
        this.cookieSecure = cookieSecure;
        this.cookieSameSite = cookieSameSite;
        this.cookiePath = cookiePath;
        this.refreshExpirySeconds = refreshExpirySeconds;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(201).body(new RegisterResponse("verification_sent"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletResponse response) {
        AuthService.LoginResult result = authService.login(request);
        setRefreshCookie(response, result.rawRefreshToken(), refreshExpirySeconds);
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
        // En la ventana de gracia (double-click) reusa la cookie existente y solo emite access nuevo.
        if (result.rawRefreshToken() != null) {
            setRefreshCookie(response, result.rawRefreshToken(), refreshExpirySeconds);
        }
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

    // ResponseCookie permite SameSite; jakarta.servlet.http.Cookie no.
    private void setRefreshCookie(HttpServletResponse response, String value, long maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .path(cookiePath)
                .maxAge(Duration.ofSeconds(maxAgeSeconds))
                .sameSite(cookieSameSite)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
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
