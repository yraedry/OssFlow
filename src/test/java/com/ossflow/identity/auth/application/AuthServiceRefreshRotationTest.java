package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.application.port.RefreshTokenRepositoryPort;
import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;
import com.ossflow.identity.auth.infrastructure.web.dto.LoginRequest;
import com.ossflow.shared.exception.UnprocessableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class AuthServiceRefreshRotationTest {

    @Autowired AuthService authService;
    @Autowired AccountRepositoryPort accountRepository;
    @Autowired RefreshTokenRepositoryPort refreshTokenRepository;

    private String rawRefreshToken;
    private Long accountId;

    @BeforeEach
    void setUp() {
        Account saved = accountRepository.save(new Account(
                null, "rotate@example.com",
                new BCryptPasswordEncoder(12).encode("Pass1234"),
                AccountProvider.LOCAL, null, true, 0, null, null));
        accountId = saved.id();
        var login = authService.login(new LoginRequest("rotate@example.com", "Pass1234"));
        rawRefreshToken = login.rawRefreshToken();
    }

    @Test
    void valid_token_rotates_to_new_token() {
        var result = authService.refresh(rawRefreshToken);
        assertThat(result.accessToken()).isNotBlank();
        assertThat(result.rawRefreshToken()).isNotBlank();
        assertThat(result.rawRefreshToken()).isNotEqualTo(rawRefreshToken);
    }

    @Test
    void double_click_within_grace_returns_access_without_new_refresh() {
        // primera llamada rota el token
        var first = authService.refresh(rawRefreshToken);
        assertThat(first.rawRefreshToken()).isNotNull();

        // segunda llamada con el MISMO token original (ya revocado) dentro de la ventana
        var second = authService.refresh(rawRefreshToken);
        assertThat(second.accessToken()).isNotBlank();
        // Idempotente: no se emite nuevo refresh (el cliente mantiene el nuevo)
        assertThat(second.rawRefreshToken()).isNull();
    }

    @Test
    void reuse_outside_grace_invalidates_family_and_bumps_token_version() {
        // primera rotación crea un token revocado replaced_by_id apunta al nuevo
        var first = authService.refresh(rawRefreshToken);
        // simulamos que la ventana de gracia ya pasó: revocamos también el reemplazo
        String newHash = com.ossflow.identity.auth.application.AuthService.sha256(first.rawRefreshToken());
        var replacement = refreshTokenRepository.findByTokenHash(newHash).orElseThrow();
        refreshTokenRepository.save(new com.ossflow.identity.auth.domain.RefreshToken(
                replacement.id(), replacement.accountId(), replacement.tokenHash(),
                replacement.tokenVersion(), replacement.expiresAt(), replacement.createdAt(),
                java.time.Instant.now(), replacement.replacedById()));

        int originalVersion = accountRepository.findById(accountId).orElseThrow().tokenVersion();

        assertThatThrownBy(() -> authService.refresh(rawRefreshToken))
                .isInstanceOf(UnprocessableException.class);

        int newVersion = accountRepository.findById(accountId).orElseThrow().tokenVersion();
        assertThat(newVersion).isGreaterThan(originalVersion);
    }

    @Test
    void unknown_token_returns_unprocessable() {
        assertThatThrownBy(() -> authService.refresh("unknown-raw-token"))
                .isInstanceOf(UnprocessableException.class);
    }

    @Test
    void expired_token_returns_unprocessable() {
        // Forzamos expiración: actualizamos el RefreshToken con expiresAt en el pasado
        String hash = com.ossflow.identity.auth.application.AuthService.sha256(rawRefreshToken);
        var token = refreshTokenRepository.findByTokenHash(hash).orElseThrow();
        refreshTokenRepository.save(new com.ossflow.identity.auth.domain.RefreshToken(
                token.id(), token.accountId(), token.tokenHash(), token.tokenVersion(),
                java.time.Instant.now().minusSeconds(60), token.createdAt(),
                token.revokedAt(), token.replacedById()));

        assertThatThrownBy(() -> authService.refresh(rawRefreshToken))
                .isInstanceOf(UnprocessableException.class)
                .hasMessageContaining("expirado");
    }
}
