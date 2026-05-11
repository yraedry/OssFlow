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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

public class RateLimitingFilter extends OncePerRequestFilter {

    private final Cache<String, Bucket> loginBuckets;
    private final Cache<String, Bucket> registerBuckets;
    private final Cache<String, Bucket> forgotPasswordBuckets;
    // S1.4: caché por userId para endpoints generales /api/** — 200 req/min por usuario autenticado.
    private final Cache<Long, Bucket> userApiBuckets;

    public RateLimitingFilter() {
        this.loginBuckets = newCache();
        this.registerBuckets = newCache();
        this.forgotPasswordBuckets = newCache();
        this.userApiBuckets = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(5))
                .maximumSize(50_000)
                .build();
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

        // S1.4: Rate limit por userId para /api/** (excluye rutas auth ya controladas por IP arriba).
        // Aplica solo a usuarios autenticados con JWT válido.
        if (uri.startsWith("/api/") && !isAuthEndpoint(uri)) {
            Long userId = resolveUserId();
            if (userId != null) {
                Bucket userBucket = userApiBuckets.get(userId, k -> newBucket(200, Duration.ofMinutes(1)));
                if (!userBucket.tryConsume(1)) {
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    response.getWriter().write("{\"error\":\"RATE_LIMIT_EXCEEDED\",\"message\":\"Demasiadas peticiones. Por favor espera un momento.\"}");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isAuthEndpoint(String uri) {
        return uri.startsWith("/api/auth/");
    }

    /** Extrae el userId del SecurityContext si el JWT fue validado por JwtAuthenticationFilter. */
    private Long resolveUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof AccountPrincipal principal) {
            return principal.id();
        }
        return null;
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
