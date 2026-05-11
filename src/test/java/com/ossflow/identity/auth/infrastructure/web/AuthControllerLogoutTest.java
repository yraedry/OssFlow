package com.ossflow.identity.auth.infrastructure.web;

import com.ossflow.identity.auth.application.AuthService;
import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.application.port.RefreshTokenRepositoryPort;
import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;
import com.ossflow.testsupport.TestSecurityContext;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthControllerLogoutTest {

    @Autowired WebApplicationContext wac;
    @Autowired AccountRepositoryPort accountRepository;
    @Autowired RefreshTokenRepositoryPort refreshTokenRepository;

    MockMvc mvc() {
        return MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
    }

    @BeforeEach
    void setUp() { TestSecurityContext.setOwner(1L); }

    @AfterEach
    void tearDown() { TestSecurityContext.clear(); }

    @Test
    void logout_with_valid_cookie_revokes_token_and_bumps_token_version_and_clears_cookie() throws Exception {
        // Crea cuenta y dispara login para obtener cookie real
        Account saved = accountRepository.save(new Account(
                null, "logout@example.com",
                new BCryptPasswordEncoder(12).encode("Pass1234"),
                AccountProvider.LOCAL, null, true, 0, null, null));
        int originalVersion = saved.tokenVersion();

        MvcResult loginResult = mvc().perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"logout@example.com","password":"Pass1234"}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        String setCookie = loginResult.getResponse().getHeader("Set-Cookie");
        assertThat(setCookie).isNotNull();
        String rawToken = extractCookieValue(setCookie);
        assertThat(rawToken).isNotBlank();

        // Logout con cookie válida → 204, cookie con Max-Age=0, token revocado, token_version bumpeado
        MvcResult logoutResult = mvc().perform(post("/api/auth/logout")
                        .cookie(new Cookie("refresh_token", rawToken)))
                .andExpect(status().isNoContent())
                .andReturn();

        String clearCookie = logoutResult.getResponse().getHeader("Set-Cookie");
        assertThat(clearCookie).isNotNull();
        assertThat(clearCookie).contains("refresh_token=");
        assertThat(clearCookie).containsIgnoringCase("Max-Age=0");

        // Verificar revocación en BD
        var stored = refreshTokenRepository.findByTokenHash(AuthService.sha256(rawToken));
        assertThat(stored).isPresent();
        assertThat(stored.get().revokedAt()).isNotNull();

        // Verificar bump de tokenVersion
        var account = accountRepository.findById(saved.id()).orElseThrow();
        assertThat(account.tokenVersion()).isGreaterThan(originalVersion);
    }

    @Test
    void logout_without_cookie_returns_204_idempotent() throws Exception {
        mvc().perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent());
    }

    @Test
    void logout_with_unknown_cookie_returns_204_silently() throws Exception {
        mvc().perform(post("/api/auth/logout")
                        .cookie(new Cookie("refresh_token", "token-que-no-existe")))
                .andExpect(status().isNoContent());
    }

    @Test
    void refresh_with_revoked_cookie_fails_outside_grace_window() throws Exception {
        // Crea cuenta y login
        Account saved = accountRepository.save(new Account(
                null, "refresh-after-logout@example.com",
                new BCryptPasswordEncoder(12).encode("Pass1234"),
                AccountProvider.LOCAL, null, true, 0, null, null));

        MvcResult loginResult = mvc().perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"refresh-after-logout@example.com","password":"Pass1234"}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        String rawToken = extractCookieValue(loginResult.getResponse().getHeader("Set-Cookie"));

        // Logout revoca el token
        mvc().perform(post("/api/auth/logout")
                        .cookie(new Cookie("refresh_token", rawToken)))
                .andExpect(status().isNoContent());

        // Forzamos que la ventana de gracia ya haya pasado retrocediendo revokedAt
        String hash = AuthService.sha256(rawToken);
        var revoked = refreshTokenRepository.findByTokenHash(hash).orElseThrow();
        refreshTokenRepository.save(new com.ossflow.identity.auth.domain.RefreshToken(
                revoked.id(), revoked.accountId(), revoked.tokenHash(), revoked.tokenVersion(),
                revoked.expiresAt(), revoked.createdAt(),
                java.time.Instant.now().minusSeconds(60), revoked.replacedById()));

        // Intentar refresh con la misma cookie revocada (fuera de la ventana de gracia) → 422
        mvc().perform(post("/api/auth/refresh")
                        .cookie(new Cookie("refresh_token", rawToken)))
                .andExpect(status().isUnprocessableEntity());
    }

    private static String extractCookieValue(String setCookieHeader) {
        // Set-Cookie: refresh_token=VALUE; Path=...; Max-Age=...
        int eq = setCookieHeader.indexOf('=');
        int semi = setCookieHeader.indexOf(';');
        if (eq < 0 || semi < 0) return null;
        return setCookieHeader.substring(eq + 1, semi);
    }
}
