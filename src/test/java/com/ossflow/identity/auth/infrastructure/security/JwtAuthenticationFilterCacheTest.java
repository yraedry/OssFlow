package com.ossflow.identity.auth.infrastructure.security;

import com.ossflow.identity.auth.application.JwtService;
import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;
import com.ossflow.identity.auth.domain.AccountRole;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class JwtAuthenticationFilterCacheTest {

    private JwtService jwtService;
    private AccountRepositoryPort accountRepository;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        accountRepository = mock(AccountRepositoryPort.class);
        filter = new JwtAuthenticationFilter(jwtService, accountRepository);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private MockHttpServletRequest reqWithToken() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("GET");
        req.setRequestURI("/api/v1/whatever");
        req.addHeader("Authorization", "Bearer fake-token");
        return req;
    }

    @Test
    void second_request_uses_cache_no_repo_lookup() throws Exception {
        Claims claims = mock(Claims.class);
        given(claims.getSubject()).willReturn("42");
        given(claims.get("tokenVersion", Integer.class)).willReturn(0);
        given(jwtService.validateToken("fake-token")).willReturn(Optional.of(claims));

        Account account = new Account(42L, "user@example.com", null,
                AccountProvider.LOCAL, null, true, 0, AccountRole.ATHLETE, null, null);
        given(accountRepository.findById(42L)).willReturn(Optional.of(account));

        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(reqWithToken(), new MockHttpServletResponse(), chain);
        SecurityContextHolder.clearContext();
        filter.doFilter(reqWithToken(), new MockHttpServletResponse(), chain);

        // El repo solo se consulta una vez; la segunda hit del cache.
        verify(accountRepository, times(1)).findById(42L);
    }

    @Test
    void token_version_mismatch_invalidates_cache() throws Exception {
        Claims claims = mock(Claims.class);
        given(claims.getSubject()).willReturn("99");
        // Claim trae tokenVersion=5
        given(claims.get("tokenVersion", Integer.class)).willReturn(5);
        given(jwtService.validateToken("fake-token")).willReturn(Optional.of(claims));

        // Cuenta tiene tokenVersion=10 → mismatch
        Account account = new Account(99L, "x@example.com", null,
                AccountProvider.LOCAL, null, true, 10, AccountRole.ATHLETE, null, null);
        given(accountRepository.findById(99L)).willReturn(Optional.of(account));

        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(reqWithToken(), new MockHttpServletResponse(), chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        // Tras mismatch, próximo lookup re-consulta el repo (cache invalidada).
        filter.doFilter(reqWithToken(), new MockHttpServletResponse(), chain);
        verify(accountRepository, atLeast(2)).findById(99L);
    }

    @Test
    void no_authorization_header_skips_lookup() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("GET");
        req.setRequestURI("/api/public");

        filter.doFilter(req, new MockHttpServletResponse(), mock(FilterChain.class));

        verify(accountRepository, never()).findById(eq(0L));
    }
}
