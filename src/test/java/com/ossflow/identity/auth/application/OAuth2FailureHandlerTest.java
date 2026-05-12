package com.ossflow.identity.auth.application;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

import static org.assertj.core.api.Assertions.assertThat;

class OAuth2FailureHandlerTest {

    private final OAuth2FailureHandler handler = new OAuth2FailureHandler("http://localhost:5173");

    @Test
    void redirects_with_email_unverified_code() throws Exception {
        MockHttpServletResponse res = new MockHttpServletResponse();
        handler.onAuthenticationFailure(new MockHttpServletRequest(), res,
                new OAuth2AuthenticationException(new OAuth2Error("EMAIL_NOT_VERIFIED")));

        assertThat(res.getRedirectedUrl()).endsWith("/login?error=oauth_email_unverified");
    }

    @Test
    void redirects_with_account_exists_code() throws Exception {
        MockHttpServletResponse res = new MockHttpServletResponse();
        handler.onAuthenticationFailure(new MockHttpServletRequest(), res,
                new OAuth2AuthenticationException(new OAuth2Error("ACCOUNT_EXISTS_DIFFERENT_PROVIDER")));

        assertThat(res.getRedirectedUrl()).endsWith("/login?error=oauth_account_exists");
    }

    @Test
    void redirects_with_generic_oauth_failed_code() throws Exception {
        MockHttpServletResponse res = new MockHttpServletResponse();
        handler.onAuthenticationFailure(new MockHttpServletRequest(), res,
                new OAuth2AuthenticationException(new OAuth2Error("UNKNOWN_ERROR")));

        assertThat(res.getRedirectedUrl()).endsWith("/login?error=oauth_failed");
    }

    @Test
    void redirects_with_generic_for_non_oauth_exception() throws Exception {
        MockHttpServletResponse res = new MockHttpServletResponse();
        handler.onAuthenticationFailure(new MockHttpServletRequest(), res,
                new BadCredentialsException("bad creds"));

        assertThat(res.getRedirectedUrl()).endsWith("/login?error=oauth_failed");
    }
}
