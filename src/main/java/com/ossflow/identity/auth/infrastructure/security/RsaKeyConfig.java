package com.ossflow.identity.auth.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

@Configuration
@ConfigurationProperties(prefix = "auth.jwt")
public class RsaKeyConfig {

    private Resource privateKeyPath;
    private Resource publicKeyPath;
    private String privateKeyB64;
    private String publicKeyB64;
    private long accessTokenExpiry = 900;
    private long refreshTokenExpiry = 604800;

    private final Environment env;

    public RsaKeyConfig(Environment env) {
        this.env = env;
    }

    public void setPrivateKeyPath(Resource privateKeyPath) { this.privateKeyPath = privateKeyPath; }
    public void setPublicKeyPath(Resource publicKeyPath) { this.publicKeyPath = publicKeyPath; }
    public void setPrivateKeyB64(String privateKeyB64) { this.privateKeyB64 = privateKeyB64; }
    public void setPublicKeyB64(String publicKeyB64) { this.publicKeyB64 = publicKeyB64; }
    public void setAccessTokenExpiry(long accessTokenExpiry) { this.accessTokenExpiry = accessTokenExpiry; }
    public void setRefreshTokenExpiry(long refreshTokenExpiry) { this.refreshTokenExpiry = refreshTokenExpiry; }

    public long getAccessTokenExpiry() { return accessTokenExpiry; }
    public long getRefreshTokenExpiry() { return refreshTokenExpiry; }

    @Bean
    public RSAPrivateKey rsaPrivateKey() throws Exception {
        String pem = readPemSource(privateKeyB64, privateKeyPath, "AUTH_JWT_PRIVATE_KEY_B64");
        String stripped = pem
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(stripped);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    @Bean
    public RSAPublicKey rsaPublicKey() throws Exception {
        String pem = readPemSource(publicKeyB64, publicKeyPath, "AUTH_JWT_PUBLIC_KEY_B64");
        String stripped = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(stripped);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(decoded));
    }

    private String readPemSource(String b64Value, Resource path, String envVarName) throws IOException {
        if (b64Value != null && !b64Value.isBlank()) {
            return new String(Base64.getDecoder().decode(b64Value), StandardCharsets.UTF_8);
        }
        if (isDevOrTestProfile() && path != null) {
            return path.getContentAsString(StandardCharsets.UTF_8);
        }
        throw new IllegalStateException(envVarName + " env var required in non-dev profiles");
    }

    private boolean isDevOrTestProfile() {
        String[] active = env.getActiveProfiles();
        if (active.length == 0) return true;
        return Arrays.stream(active).anyMatch(p -> p.equals("dev") || p.equals("test"));
    }
}
