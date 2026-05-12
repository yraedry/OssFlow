package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;
import com.ossflow.identity.auth.infrastructure.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class AuthServiceRegisterTest {

    @Autowired AuthService authService;
    @Autowired AccountRepositoryPort accountRepository;

    @Test
    void register_creates_account_for_new_email() {
        authService.register(new RegisterRequest("new-user@example.com", "Pass1234"));

        var account = accountRepository.findByEmail("new-user@example.com");
        assertThat(account).isPresent();
        assertThat(account.get().emailVerified()).isFalse();
    }

    @Test
    void register_does_not_throw_when_email_already_registered() {
        // A7: anti-enumeración → no debe revelar que el correo ya existe.
        accountRepository.save(new Account(
                null, "existing@example.com",
                new BCryptPasswordEncoder(12).encode("Pass1234"),
                AccountProvider.LOCAL, null, true, 0, null, null));

        assertThatCode(() -> authService.register(new RegisterRequest("existing@example.com", "Pass1234")))
                .doesNotThrowAnyException();
    }

    @Test
    void register_returns_same_response_for_existing_unverified_and_new() {
        // Unverified existente: debe reenviar verificación silenciosamente (no excepción).
        accountRepository.save(new Account(
                null, "unverified@example.com",
                new BCryptPasswordEncoder(12).encode("Pass1234"),
                AccountProvider.LOCAL, null, false, 0, null, null));

        assertThatCode(() -> authService.register(new RegisterRequest("unverified@example.com", "Pass1234")))
                .doesNotThrowAnyException();
        assertThatCode(() -> authService.register(new RegisterRequest("brand-new@example.com", "Pass1234")))
                .doesNotThrowAnyException();
    }
}
