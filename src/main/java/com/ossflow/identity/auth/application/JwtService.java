package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.infrastructure.security.RsaKeyConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private final long accessTokenExpirySeconds;

    public JwtService(RSAPrivateKey privateKey, RSAPublicKey publicKey, RsaKeyConfig config) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.accessTokenExpirySeconds = config.getAccessTokenExpiry();
    }

    public String issueAccessToken(Account account) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(account.id()))
                .claim("email", account.email())
                .claim("tokenVersion", account.tokenVersion())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTokenExpirySeconds)))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public Optional<Claims> validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(claims);
        } catch (Exception e) {
            log.warn("JWT validation error: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return Optional.empty();
        }
    }
}
