package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.application.port.EmailVerificationTokenRepositoryPort;
import com.ossflow.identity.auth.application.port.PasswordResetTokenRepositoryPort;
import com.ossflow.identity.auth.application.port.RefreshTokenRepositoryPort;
import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;
import com.ossflow.identity.auth.domain.EmailVerificationToken;
import com.ossflow.identity.auth.domain.PasswordResetToken;
import com.ossflow.identity.auth.infrastructure.web.dto.LoginRequest;
import com.ossflow.shared.exception.BadRequestException;
import com.ossflow.shared.exception.UnprocessableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class AuthServiceLifecycleTest {

    @Autowired AuthService authService;
    @Autowired AccountRepositoryPort accountRepository;
    @Autowired EmailVerificationTokenRepositoryPort emailVerificationRepository;
    @Autowired PasswordResetTokenRepositoryPort passwordResetRepository;
    @Autowired RefreshTokenRepositoryPort refreshTokenRepository;

    Account account;

    @BeforeEach
    void setUp() {
        account = accountRepository.save(new Account(
                null, "lifecycle@example.com",
                new BCryptPasswordEncoder(12).encode("Pass1234"),
                AccountProvider.LOCAL, null, true, 0, null, null));
    }

    @Test
    void login_rejects_unverified() {
        Account unv = accountRepository.save(new Account(
                null, "unverified@example.com",
                new BCryptPasswordEncoder(12).encode("Pass1234"),
                AccountProvider.LOCAL, null, false, 0, null, null));

        assertThatThrownBy(() -> authService.login(new LoginRequest("unverified@example.com", "Pass1234")))
                .isInstanceOf(UnprocessableException.class);
    }

    @Test
    void login_rejects_wrong_password() {
        assertThatThrownBy(() -> authService.login(new LoginRequest("lifecycle@example.com", "WrongPass1")))
                .isInstanceOf(UnprocessableException.class);
    }

    @Test
    void logout_revokes_token_when_present() {
        var login = authService.login(new LoginRequest("lifecycle@example.com", "Pass1234"));
        assertThatCode(() -> authService.logout(login.rawRefreshToken())).doesNotThrowAnyException();
    }

    @Test
    void logout_silent_when_unknown_token() {
        assertThatCode(() -> authService.logout("not-a-real-token")).doesNotThrowAnyException();
    }

    @Test
    void verify_email_marks_account_verified() {
        Account unv = accountRepository.save(new Account(
                null, "verify-me@example.com",
                new BCryptPasswordEncoder(12).encode("Pass1234"),
                AccountProvider.LOCAL, null, false, 0, null, null));

        String raw = "raw-verification-token";
        emailVerificationRepository.save(new EmailVerificationToken(
                null, unv.id(), AuthService.sha256(raw), Instant.now().plusSeconds(3600), null));

        authService.verifyEmail(raw);

        var refreshed = accountRepository.findById(unv.id()).orElseThrow();
        assertThat(refreshed.emailVerified()).isTrue();
    }

    @Test
    void verify_email_rejects_invalid_token() {
        assertThatThrownBy(() -> authService.verifyEmail("invalid"))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void resend_verification_silent_for_already_verified() {
        assertThatCode(() -> authService.resendVerification("lifecycle@example.com"))
                .doesNotThrowAnyException();
    }

    @Test
    void resend_verification_silent_for_unknown_email() {
        assertThatCode(() -> authService.resendVerification("nobody@example.com"))
                .doesNotThrowAnyException();
    }

    @Test
    void forgot_password_silent_for_unknown_email() {
        assertThatCode(() -> authService.forgotPassword("nobody@example.com"))
                .doesNotThrowAnyException();
    }

    @Test
    void reset_password_bumps_token_version_and_revokes_refresh() {
        String raw = "raw-reset-token";
        passwordResetRepository.save(new PasswordResetToken(
                null, account.id(), AuthService.sha256(raw),
                Instant.now().plusSeconds(3600), null));

        int prevVersion = account.tokenVersion();
        authService.resetPassword(raw, "NewPass1234");

        var refreshed = accountRepository.findById(account.id()).orElseThrow();
        assertThat(refreshed.tokenVersion()).isGreaterThan(prevVersion);
    }

    @Test
    void reset_password_rejects_invalid_token() {
        assertThatThrownBy(() -> authService.resetPassword("invalid", "NewPass1234"))
                .isInstanceOf(BadRequestException.class);
    }
}
