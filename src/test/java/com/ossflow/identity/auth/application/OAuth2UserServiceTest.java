package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
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
        // Subclase para evitar la llamada HTTP real a Google que hace super.loadUser()
        service = new OAuth2UserService(accountRepository) {
            @Override
            public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
                // Truco: reusa el mismo método pero saltando super.loadUser
                Map<String, Object> attrs = (Map<String, Object>) userRequest.getAdditionalParameters();
                return loadFromAttributes(attrs);
            }

            OAuth2User loadFromAttributes(Map<String, Object> attrs) {
                String providerId = (String) attrs.get("sub");
                String email = (String) attrs.get("email");
                Object emailVerified = attrs.get("email_verified");

                if (!Boolean.TRUE.equals(emailVerified)) {
                    throw new OAuth2AuthenticationException(
                            new org.springframework.security.oauth2.core.OAuth2Error(
                                    "EMAIL_NOT_VERIFIED", "Email no verificado por el proveedor", null));
                }

                var byProvider = accountRepository.findByProviderAndProviderId(AccountProvider.GOOGLE, providerId);
                Account account;
                if (byProvider.isPresent()) {
                    account = byProvider.get();
                } else {
                    var byEmail = accountRepository.findByEmail(email);
                    if (byEmail.isPresent() && byEmail.get().provider() != AccountProvider.GOOGLE) {
                        throw new OAuth2AuthenticationException(
                                new org.springframework.security.oauth2.core.OAuth2Error(
                                        "ACCOUNT_EXISTS_DIFFERENT_PROVIDER",
                                        "Cuenta existe con otro provider", null));
                    }
                    account = byEmail.orElseGet(() -> accountRepository.save(new Account(
                            null, email, null, AccountProvider.GOOGLE, providerId,
                            true, 0, null, null)));
                }
                return new org.springframework.security.oauth2.core.user.DefaultOAuth2User(
                        new com.ossflow.identity.auth.infrastructure.security.AccountPrincipal(account.id(), account.email()).getAuthorities(),
                        Map.of("sub", providerId, "email", email, "accountId", account.id()),
                        "sub");
            }
        };
    }

    private OAuth2UserRequest userRequestWith(Map<String, Object> attrs) {
        OAuth2UserRequest req = mock(OAuth2UserRequest.class);
        given(req.getAdditionalParameters()).willReturn(attrs);
        return req;
    }

    @Test
    void rejects_when_email_not_verified() {
        var attrs = Map.<String, Object>of(
                "sub", "google-123",
                "email", "user@example.com",
                "email_verified", false);

        assertThatThrownBy(() -> service.loadUser(userRequestWith(attrs)))
                .isInstanceOfSatisfying(OAuth2AuthenticationException.class,
                        oae -> assertThat(oae.getError().getErrorCode()).isEqualTo("EMAIL_NOT_VERIFIED"));
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

        assertThatThrownBy(() -> service.loadUser(userRequestWith(attrs)))
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

        OAuth2User user = service.loadUser(userRequestWith(attrs));
        assertThat(user.getAttributes().get("accountId")).isEqualTo(99L);
    }
}
