package com.ossflow.identity.auth.infrastructure.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
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

public class RateLimitingFilter extends OncePerRequestFilter {

    private final Cache<String, Bucket> loginBuckets;
    private final Cache<String, Bucket> registerBuckets;
    private final Cache<String, Bucket> forgotPasswordBuckets;

    public RateLimitingFilter() {
        this.loginBuckets = newCache();
        this.registerBuckets = newCache();
        this.forgotPasswordBuckets = newCache();
    }

    private static Cache<String, Bucket> newCache() {
        return Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(15))
                .maximumSize(10_000)
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String ip = getClientIp(request);

        if ("POST".equals(method)) {
            Bucket bucket = null;
            if (uri.endsWith("/api/auth/login")) {
                bucket = loginBuckets.get(ip, k -> newBucket(10, Duration.ofMinutes(5)));
            } else if (uri.endsWith("/api/auth/register")) {
                bucket = registerBuckets.get(ip, k -> newBucket(5, Duration.ofMinutes(10)));
            } else if (uri.endsWith("/api/auth/forgot-password") || uri.endsWith("/api/auth/resend-verification")) {
                bucket = forgotPasswordBuckets.get(ip, k -> newBucket(3, Duration.ofHours(1)));
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

    // server.forward-headers-strategy=framework hace que Spring populée
    // request.getRemoteAddr() ya resolviendo X-Forwarded-For del proxy de confianza.
    private String getClientIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
