package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.application.port.RefreshTokenRepositoryPort;
import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;
import com.ossflow.identity.auth.domain.RefreshToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class OAuth2SuccessHandlerTest {

    private AccountRepositoryPort accountRepository;
    private RefreshTokenRepositoryPort refreshTokenRepository;
    private OAuth2SuccessHandler handler;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepositoryPort.class);
        refreshTokenRepository = mock(RefreshTokenRepositoryPort.class);
        handler = new OAuth2SuccessHandler(
                accountRepository, refreshTokenRepository,
                "http://localhost:5173", 2592000L, false, "Lax", "/api/auth");
    }

    @Test
    void sets_refresh_cookie_and_redirects_without_token_in_url() throws Exception {
        Account account = new Account(42L, "user@example.com", null,
                AccountProvider.GOOGLE, "google-id", true, 0, null, null);
        given(accountRepository.findById(42L)).willReturn(Optional.of(account));
        given(refreshTokenRepository.save(any(RefreshToken.class)))
                .willAnswer(inv -> inv.getArgument(0));

        var principal = new DefaultOAuth2User(
                List.of(),
                Map.of("accountId", 42L, "email", "user@example.com", "sub", "google-id"),
                "sub");
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(req, res, auth);

        assertThat(res.getRedirectedUrl()).isEqualTo("http://localhost:5173/auth/callback?ok=1");
        assertThat(res.getRedirectedUrl()).doesNotContain("#token=");

        String setCookie = res.getHeader("Set-Cookie");
        assertThat(setCookie).isNotNull();
        assertThat(setCookie).contains("refresh_token=");
        assertThat(setCookie).containsIgnoringCase("HttpOnly");
        assertThat(setCookie).containsIgnoringCase("SameSite=Lax");
    }

    @Test
    void throws_when_accountId_not_found() {
        given(accountRepository.findById(99L)).willReturn(Optional.empty());

        var principal = new DefaultOAuth2User(
                List.of(),
                Map.of("accountId", 99L, "email", "missing@example.com", "sub", "g"),
                "sub");
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                handler.onAuthenticationSuccess(new MockHttpServletRequest(), new MockHttpServletResponse(), auth))
                .isInstanceOf(IllegalStateException.class);
    }
}
