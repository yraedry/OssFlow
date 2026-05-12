package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.application.port.RefreshTokenRepositoryPort;
import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.RefreshToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AccountRepositoryPort accountRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final String frontendUrl;
    private final long refreshTokenExpirySeconds;
    private final boolean cookieSecure;
    private final String cookieSameSite;
    private final String cookiePath;

    public OAuth2SuccessHandler(AccountRepositoryPort accountRepository,
                                RefreshTokenRepositoryPort refreshTokenRepository,
                                @Value("${app.frontend-url:http://localhost:5173}") String frontendUrl,
                                @Value("${app.refresh-token.expiry:2592000}") long refreshTokenExpirySeconds,
                                @Value("${app.cookie.secure:true}") boolean cookieSecure,
                                @Value("${app.cookie.same-site:Lax}") String cookieSameSite,
                                @Value("${app.cookie.path:/api/auth}") String cookiePath) {
        this.accountRepository = accountRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.frontendUrl = frontendUrl;
        this.refreshTokenExpirySeconds = refreshTokenExpirySeconds;
        this.cookieSecure = cookieSecure;
        this.cookieSameSite = cookieSameSite;
        this.cookiePath = cookiePath;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        Long accountId = (Long) oauthUser.getAttributes().get("accountId");

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalStateException("Account not found after OAuth2 login"));

        // El access token NO se emite aquí: el frontend lo obtendrá vía silent refresh
        // sobre la cookie httpOnly. Ver A11 en plan de hardening.
        String rawRefreshToken = generateToken();
        refreshTokenRepository.save(new RefreshToken(
                null, account.id(), AuthService.sha256(rawRefreshToken), account.tokenVersion(),
                Instant.now().plusSeconds(refreshTokenExpirySeconds), Instant.now(), null
        ));

        ResponseCookie cookie = ResponseCookie.from("refresh_token", rawRefreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path(cookiePath)
                .maxAge(Duration.ofSeconds(refreshTokenExpirySeconds))
                .sameSite(cookieSameSite)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // El access token NO viaja en URL: el frontend obtendrá uno nuevo vía silent refresh
        // sobre la cookie httpOnly que acabamos de setear. Ver A11 en plan de hardening.
        getRedirectStrategy().sendRedirect(request, response,
                frontendUrl + "/auth/callback?ok=1");
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
