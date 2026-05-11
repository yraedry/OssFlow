package com.ossflow.identity.auth.infrastructure.security;

import com.ossflow.identity.auth.application.JwtService;
import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import io.jsonwebtoken.Claims;
import java.util.Optional;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwtService;
    private final AccountRepositoryPort accountRepository;

    public JwtAuthenticationFilter(JwtService jwtService, AccountRepositoryPort accountRepository) {
        this.jwtService = jwtService;
        this.accountRepository = accountRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        Optional<Claims> claimsOpt = jwtService.validateToken(token);
        if (claimsOpt.isEmpty()) {
            log.warn("JWT validation failed for token on {} {}", request.getMethod(), request.getRequestURI());
        }
        claimsOpt.ifPresent(claims -> {
            Long accountId = Long.parseLong(claims.getSubject());
            int tokenVersion = claims.get("tokenVersion", Integer.class);

            accountRepository.findById(accountId).ifPresent(account -> {
                if (account.tokenVersion() == tokenVersion) {
                    AccountPrincipal principal = new AccountPrincipal(account.id(), account.email());
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.debug("JWT auth OK for account {} on {} {}", accountId, request.getMethod(), request.getRequestURI());
                } else {
                    log.warn("Token version mismatch for account {}: expected {}, got {}", accountId, account.tokenVersion(), tokenVersion);
                }
            });
        });

        chain.doFilter(request, response);
    }
}
