package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class OAuth2UserServiceTest {

    private AccountRepositoryPort accountRepository;
    private OAuth2UserService service;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepositoryPort.class);
        service = new OAuth2UserService(accountRepository);
    }

    @Test
    void rejects_when_email_not_verified() {
        var attrs = Map.<String, Object>of(
                "sub", "google-123",
                "email", "user@example.com",
                "email_verified", false);

        assertThatThrownBy(() -> service.processAttributes(attrs))
                .isInstanceOfSatisfying(OAuth2AuthenticationException.class,
                        oae -> assertThat(oae.getError().getErrorCode()).isEqualTo("EMAIL_NOT_VERIFIED"));
    }

    @Test
    void rejects_when_email_verified_missing() {
        var attrs = Map.<String, Object>of(
                "sub", "google-123",
                "email", "user@example.com");
        // Sin la clave email_verified, también se rechaza.
        assertThatThrownBy(() -> service.processAttributes(attrs))
                .isInstanceOf(OAuth2AuthenticationException.class);
    }

    @Test
    void rejects_when_local_account_with_same_email_exists() {
        Account localAccount = new Account(5L, "user@example.com", "hash",
                AccountProvider.LOCAL, null, true, 0, null, null);
        given(accountRepository.findByProviderAndProviderId(AccountProvider.GOOGLE, "google-123"))
                .willReturn(Optional.empty());
        given(accountRepository.findByEmail("user@example.com"))
                .willReturn(Optional.of(localAccount));

        var attrs = Map.<String, Object>of(
                "sub", "google-123",
                "email", "user@example.com",
                "email_verified", true);

        assertThatThrownBy(() -> service.processAttributes(attrs))
                .isInstanceOfSatisfying(OAuth2AuthenticationException.class,
                        oae -> assertThat(oae.getError().getErrorCode()).isEqualTo("ACCOUNT_EXISTS_DIFFERENT_PROVIDER"));
    }

    @Test
    void creates_new_google_account_when_verified_and_no_existing() {
        given(accountRepository.findByProviderAndProviderId(AccountProvider.GOOGLE, "google-456"))
                .willReturn(Optional.empty());
        given(accountRepository.findByEmail("new@example.com")).willReturn(Optional.empty());
        Account saved = new Account(99L, "new@example.com", null,
                AccountProvider.GOOGLE, "google-456", true, 0, null, null);
        given(accountRepository.save(any(Account.class))).willReturn(saved);

        var attrs = Map.<String, Object>of(
                "sub", "google-456",
                "email", "new@example.com",
                "email_verified", true);

        OAuth2User user = service.processAttributes(attrs);
        assertThat(user.getAttributes().get("accountId")).isEqualTo(99L);
    }

    @Test
    void returns_existing_account_when_found_by_provider_id() {
        Account existing = new Account(77L, "linked@example.com", null,
                AccountProvider.GOOGLE, "google-789", true, 0, null, null);
        given(accountRepository.findByProviderAndProviderId(AccountProvider.GOOGLE, "google-789"))
                .willReturn(Optional.of(existing));

        var attrs = Map.<String, Object>of(
                "sub", "google-789",
                "email", "linked@example.com",
                "email_verified", true);

        OAuth2User user = service.processAttributes(attrs);
        assertThat(user.getAttributes().get("accountId")).isEqualTo(77L);
    }
}
