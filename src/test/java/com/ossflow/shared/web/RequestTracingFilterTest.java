package com.ossflow.shared.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RequestTracingFilterTest {

    @Test
    void should_generate_trace_id_when_header_missing() throws Exception {
        var filter = new RequestTracingFilter();
        var req = mock(HttpServletRequest.class);
        var res = mock(HttpServletResponse.class);
        var chain = mock(FilterChain.class);
        when(req.getHeader("X-Trace-Id")).thenReturn(null);

        filter.doFilter(req, res, chain);

        verify(res).setHeader(eq("X-Trace-Id"), argThat(v -> v != null && !v.isBlank()));
        assertThat(MDC.get("traceId")).isNull();
    }

    @Test
    void should_propagate_existing_trace_id() throws Exception {
        var filter = new RequestTracingFilter();
        var req = mock(HttpServletRequest.class);
        var res = mock(HttpServletResponse.class);
        var chain = mock(FilterChain.class);
        when(req.getHeader("X-Trace-Id")).thenReturn("abc-123");

        filter.doFilter(req, res, chain);

        verify(res).setHeader("X-Trace-Id", "abc-123");
    }
}
