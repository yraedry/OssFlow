package com.ossflow.identity.auth.infrastructure.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> registerBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> forgotPasswordBuckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String ip = getClientIp(request);

        if ("POST".equals(method)) {
            Bucket bucket = null;
            if (uri.endsWith("/api/auth/login")) {
                bucket = loginBuckets.computeIfAbsent(ip, k -> newBucket(10, Duration.ofMinutes(5)));
            } else if (uri.endsWith("/api/auth/register")) {
                bucket = registerBuckets.computeIfAbsent(ip, k -> newBucket(5, Duration.ofMinutes(10)));
            } else if (uri.endsWith("/api/auth/forgot-password") || uri.endsWith("/api/auth/resend-verification")) {
                bucket = forgotPasswordBuckets.computeIfAbsent(ip, k -> newBucket(3, Duration.ofHours(1)));
            }

            if (bucket != null && !bucket.tryConsume(1)) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("{\"error\":\"RATE_LIMIT_EXCEEDED\",\"message\":\"Demasiados intentos. Por favor espera.\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private Bucket newBucket(int capacity, Duration duration) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(capacity, duration)
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
