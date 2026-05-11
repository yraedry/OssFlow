package com.ossflow.identity.auth.infrastructure.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class RateLimitingFilterCaffeineTest {

    private final RateLimitingFilter filter = new RateLimitingFilter();

    private MockHttpServletRequest req(String uri, String ip) {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("POST");
        req.setRequestURI(uri);
        req.setRemoteAddr(ip);
        return req;
    }

    @Test
    void allows_requests_below_limit_for_login() throws Exception {
        FilterChain chain = mock(FilterChain.class);
        for (int i = 0; i < 10; i++) {
            MockHttpServletResponse res = new MockHttpServletResponse();
            filter.doFilter(req("/api/auth/login", "10.0.0.1"), res, chain);
            assertThat(res.getStatus()).isEqualTo(200);
        }
    }

    @Test
    void blocks_when_login_capacity_exceeded() throws Exception {
        FilterChain chain = mock(FilterChain.class);
        for (int i = 0; i < 10; i++) {
            filter.doFilter(req("/api/auth/login", "10.0.0.2"), new MockHttpServletResponse(), chain);
        }
        MockHttpServletResponse res = new MockHttpServletResponse();
        filter.doFilter(req("/api/auth/login", "10.0.0.2"), res, chain);

        assertThat(res.getStatus()).isEqualTo(429);
        assertThat(res.getContentAsString()).contains("RATE_LIMIT_EXCEEDED");
    }

    @Test
    void buckets_isolated_per_ip() throws Exception {
        FilterChain chain = mock(FilterChain.class);
        for (int i = 0; i < 10; i++) {
            filter.doFilter(req("/api/auth/login", "10.0.0.3"), new MockHttpServletResponse(), chain);
        }
        MockHttpServletResponse otherIp = new MockHttpServletResponse();
        filter.doFilter(req("/api/auth/login", "10.0.0.4"), otherIp, chain);
        assertThat(otherIp.getStatus()).isEqualTo(200);
    }

    @Test
    void non_post_requests_skip_rate_limiting() throws Exception {
        FilterChain chain = mock(FilterChain.class);
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("GET");
        req.setRequestURI("/api/auth/login");
        req.setRemoteAddr("10.0.0.5");
        for (int i = 0; i < 20; i++) {
            filter.doFilter(req, new MockHttpServletResponse(), chain);
        }
        // GETs no consumen del bucket; el chain se invoca cada vez.
        verify(chain, org.mockito.Mockito.times(20)).doFilter(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }
}
