package com.ossflow.identity.auth.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@ConfigurationProperties(prefix = "auth.jwt")
public class RsaKeyConfig {

    private Resource privateKeyPath;
    private Resource publicKeyPath;
    private long accessTokenExpiry = 900;
    private long refreshTokenExpiry = 604800;

    public void setPrivateKeyPath(Resource privateKeyPath) { this.privateKeyPath = privateKeyPath; }
    public void setPublicKeyPath(Resource publicKeyPath) { this.publicKeyPath = publicKeyPath; }
    public void setAccessTokenExpiry(long accessTokenExpiry) { this.accessTokenExpiry = accessTokenExpiry; }
    public void setRefreshTokenExpiry(long refreshTokenExpiry) { this.refreshTokenExpiry = refreshTokenExpiry; }

    public long getAccessTokenExpiry() { return accessTokenExpiry; }
    public long getRefreshTokenExpiry() { return refreshTokenExpiry; }

    @Bean
    public RSAPrivateKey rsaPrivateKey() throws Exception {
        String pem = readPem(privateKeyPath)
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(pem);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    @Bean
    public RSAPublicKey rsaPublicKey() throws Exception {
        String pem = readPem(publicKeyPath)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(pem);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(decoded));
    }

    private String readPem(Resource resource) throws IOException {
        return resource.getContentAsString(StandardCharsets.UTF_8);
    }
}
