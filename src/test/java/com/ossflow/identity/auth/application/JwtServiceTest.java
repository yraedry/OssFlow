package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;
import com.ossflow.identity.auth.domain.AccountRole;
import com.ossflow.identity.auth.infrastructure.security.RsaKeyConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        Environment env = mock(Environment.class);
        given(env.getActiveProfiles()).willReturn(new String[]{"test"});

        RsaKeyConfig config = new RsaKeyConfig(env);
        config.setPrivateKeyPath(new ClassPathResource("certs/private.pem"));
        config.setPublicKeyPath(new ClassPathResource("certs/public.pem"));
        config.setAccessTokenExpiry(900);

        jwtService = new JwtService(config.rsaPrivateKey(), config.rsaPublicKey(), config);
    }

    @Test
    void issues_token_that_round_trips() {
        Account account = new Account(42L, "user@example.com", null,
                AccountProvider.LOCAL, null, true, 7, AccountRole.ATHLETE, null, null);

        String token = jwtService.issueAccessToken(account);
        var claims = jwtService.validateToken(token);

        assertThat(claims).isPresent();
        assertThat(claims.get().getSubject()).isEqualTo("42");
        assertThat(claims.get().get("email")).isEqualTo("user@example.com");
        assertThat(claims.get().get("tokenVersion", Integer.class)).isEqualTo(7);
    }

    @Test
    void invalid_token_returns_empty() {
        assertThat(jwtService.validateToken("nope")).isEmpty();
    }

    @Test
    void empty_token_returns_empty() {
        assertThat(jwtService.validateToken("")).isEmpty();
    }
}
