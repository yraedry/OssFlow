package com.ossflow.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void should_map_NotFoundException_to_404() {
        var req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/api/v1/x");
        MDC.put("traceId", "trace-1");

        var response = handler.handleDomain(
                new NotFoundException("X_NOT_FOUND", "no existe", Map.of("id", 5)),
                req
        );

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        var body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.code()).isEqualTo("X_NOT_FOUND");
        assertThat(body.message()).isEqualTo("no existe");
        assertThat(body.traceId()).isEqualTo("trace-1");
        assertThat(body.details()).containsEntry("id", 5);
        MDC.clear();
    }

    @Test
    void should_map_DuplicateNameException_to_409() {
        var req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/x");

        var response = handler.handleDomain(
                new DuplicateNameException("X_DUP", "duplicado", Map.of()),
                req
        );

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody().code()).isEqualTo("X_DUP");
    }

    @Test
    void should_map_unexpected_to_500_with_trace_id() {
        var req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/x");
        MDC.put("traceId", "trace-err");

        var response = handler.handleUnexpected(new RuntimeException("boom"), req);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().code()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.getBody().traceId()).isEqualTo("trace-err");
        MDC.clear();
    }
}
