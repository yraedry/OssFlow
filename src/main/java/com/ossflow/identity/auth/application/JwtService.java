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
import java.util.HexFormat;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private final long accessTokenExpirySeconds;
    // S2.6: Fingerprint SHA-256 del public key (primeros 8 chars del hex) como kid.
    // Permite detectar si el token fue firmado con una clave distinta a la actual.
    private final String currentKid;

    public JwtService(RSAPrivateKey privateKey, RSAPublicKey publicKey, RsaKeyConfig config) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.accessTokenExpirySeconds = config.getAccessTokenExpiry();
        this.currentKid = computeKid(publicKey);
    }

    public String issueAccessToken(Account account) {
        Instant now = Instant.now();
        return Jwts.builder()
                .header().add(Map.of("kid", currentKid)).and()
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
            var jws = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token);

            // S2.6: Verificar que el kid del token coincide con la clave actual.
            // Si no coincide, el token fue firmado con una clave rotada — sesión inválida.
            String tokenKid = (String) jws.getHeader().get("kid");
            if (tokenKid != null && !currentKid.equals(tokenKid)) {
                log.warn("JWT kid mismatch: token has kid={}, current kid={} — key rotated, session invalid",
                        tokenKid, currentKid);
                return Optional.empty();
            }

            return Optional.of(jws.getPayload());
        } catch (Exception e) {
            log.warn("JWT validation error: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return Optional.empty();
        }
    }

    /** Devuelve el kid activo (fingerprint del public key). */
    public String getCurrentKid() {
        return currentKid;
    }

    /**
     * S2.6: Computa el kid como los primeros 8 chars del hex SHA-256 del encoded public key.
     * Es determinista y no requiere base de datos — la rotación de key automáticamente
     * invalida tokens emitidos con la clave anterior.
     */
    private static String computeKid(RSAPublicKey publicKey) {
        try {
            byte[] encoded = publicKey.getEncoded();
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(encoded);
            return HexFormat.of().formatHex(hash).substring(0, 8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute RSA public key fingerprint", e);
        }
    }
}
