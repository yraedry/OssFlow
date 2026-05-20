package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;
import com.ossflow.identity.auth.domain.AccountRole;
import com.ossflow.identity.auth.infrastructure.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ossflow.shared.exception.ConflictException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class AuthServiceRegisterTest {

    @Autowired AuthService authService;
    @Autowired AccountRepositoryPort accountRepository;

    @Test
    void register_creates_account_for_new_email() {
        authService.register(new RegisterRequest("new-user@example.com", "Pass1234", null));

        var account = accountRepository.findByEmail("new-user@example.com");
        assertThat(account).isPresent();
        assertThat(account.get().emailVerified()).isFalse();
    }

    @Test
    void register_throws_conflict_when_email_already_verified() {
        accountRepository.save(new Account(
                null, "existing@example.com",
                new BCryptPasswordEncoder(12).encode("Pass1234"),
                AccountProvider.LOCAL, null, true, 0, AccountRole.ATHLETE, null, null));

        assertThatThrownBy(() -> authService.register(new RegisterRequest("existing@example.com", "Pass1234", null)))
                .isInstanceOf(ConflictException.class)
                .satisfies(ex -> assertThat(((ConflictException) ex).getErrorCode()).isEqualTo("EMAIL_ALREADY_EXISTS"));
    }

    @Test
    void register_throws_conflict_and_resends_verification_when_email_unverified() {
        accountRepository.save(new Account(
                null, "unverified@example.com",
                new BCryptPasswordEncoder(12).encode("Pass1234"),
                AccountProvider.LOCAL, null, false, 0, AccountRole.ATHLETE, null, null));

        assertThatThrownBy(() -> authService.register(new RegisterRequest("unverified@example.com", "Pass1234", null)))
                .isInstanceOf(ConflictException.class)
                .satisfies(ex -> assertThat(((ConflictException) ex).getErrorCode()).isEqualTo("EMAIL_UNVERIFIED"));
    }
}
