package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.application.port.RefreshTokenRepositoryPort;
import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.RefreshToken;
import com.ossflow.identity.auth.infrastructure.security.RsaKeyConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AccountRepositoryPort accountRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final JwtService jwtService;
    private final String frontendUrl;
    private final long refreshTokenExpirySeconds;

    public OAuth2SuccessHandler(AccountRepositoryPort accountRepository,
                                RefreshTokenRepositoryPort refreshTokenRepository,
                                JwtService jwtService,
                                @Value("${app.frontend-url:http://localhost:5173}") String frontendUrl,
                                RsaKeyConfig config) {
        this.accountRepository = accountRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.frontendUrl = frontendUrl;
        this.refreshTokenExpirySeconds = config.getRefreshTokenExpiry();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        Long accountId = (Long) oauthUser.getAttributes().get("accountId");

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalStateException("Account not found after OAuth2 login"));

        String accessToken = jwtService.issueAccessToken(account);
        String rawRefreshToken = generateToken();
        refreshTokenRepository.save(new RefreshToken(
                null, account.id(), AuthService.sha256(rawRefreshToken), account.tokenVersion(),
                Instant.now().plusSeconds(refreshTokenExpirySeconds), Instant.now(), null
        ));

        Cookie refreshCookie = new Cookie("refresh_token", rawRefreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/api/auth/refresh");
        refreshCookie.setMaxAge((int) refreshTokenExpirySeconds);
        response.addCookie(refreshCookie);

        getRedirectStrategy().sendRedirect(request, response,
                frontendUrl + "/auth/callback#token=" + accessToken);
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
