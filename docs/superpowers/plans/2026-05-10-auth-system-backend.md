# Auth System Backend Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement a complete JWT + OAuth2 authentication system in the OssFlow Spring Boot 4 backend, replacing the hardcoded `owner_id = 1L` with real identity from SecurityContext.

**Architecture:** Hexagonal architecture under `com.ossflow.identity.auth` — domain records, application services, port interfaces, infrastructure adapters (persistence + security + web). Spring Security 6 filter chain with RS256 JWT access tokens and HttpOnly cookie refresh tokens. CSRF Double Submit Cookie pattern. Bucket4j rate limiting per endpoint.

**Tech Stack:** Spring Boot 4.0.5, Spring Security 6, JJWT 0.12.6, Bucket4j 8.10.1, BCrypt, JavaMailSender (Resend SMTP), PostgreSQL + Flyway, JPA/Hibernate.

---

## File Map

### New files — Flyway migrations
- `src/main/resources/db/migration/V240__add_account_table.sql`
- `src/main/resources/db/migration/V241__add_email_verification_token.sql`
- `src/main/resources/db/migration/V242__add_password_reset_token.sql`
- `src/main/resources/db/migration/V243__add_refresh_token.sql`
- `src/main/resources/db/migration/V244__fk_user_profile_account.sql`

### New files — Domain
- `src/main/java/com/ossflow/identity/auth/domain/AccountProvider.java`
- `src/main/java/com/ossflow/identity/auth/domain/Account.java`
- `src/main/java/com/ossflow/identity/auth/domain/RefreshToken.java`
- `src/main/java/com/ossflow/identity/auth/domain/EmailVerificationToken.java`
- `src/main/java/com/ossflow/identity/auth/domain/PasswordResetToken.java`

### New files — Application ports
- `src/main/java/com/ossflow/identity/auth/application/port/AccountRepositoryPort.java`
- `src/main/java/com/ossflow/identity/auth/application/port/RefreshTokenRepositoryPort.java`
- `src/main/java/com/ossflow/identity/auth/application/port/EmailVerificationTokenRepositoryPort.java`
- `src/main/java/com/ossflow/identity/auth/application/port/PasswordResetTokenRepositoryPort.java`

### New files — Application services
- `src/main/java/com/ossflow/identity/auth/application/JwtService.java`
- `src/main/java/com/ossflow/identity/auth/application/EmailService.java`
- `src/main/java/com/ossflow/identity/auth/application/AuthService.java`
- `src/main/java/com/ossflow/identity/auth/application/OAuth2UserService.java`
- `src/main/java/com/ossflow/identity/auth/application/OAuth2SuccessHandler.java`

### New files — Infrastructure persistence
- `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/AccountEntity.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/AccountJpaRepository.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/AccountPersistenceMapper.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/AccountPersistenceAdapter.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/RefreshTokenEntity.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/RefreshTokenJpaRepository.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/RefreshTokenPersistenceAdapter.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/EmailVerificationTokenEntity.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/EmailVerificationTokenJpaRepository.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/EmailVerificationTokenPersistenceAdapter.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/PasswordResetTokenEntity.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/PasswordResetTokenJpaRepository.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/PasswordResetTokenPersistenceAdapter.java`

### New files — Infrastructure security
- `src/main/java/com/ossflow/identity/auth/infrastructure/security/RsaKeyConfig.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/security/AccountPrincipal.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/security/AccountDetailsService.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/security/JwtAuthenticationFilter.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/security/RateLimitingFilter.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/security/SecurityConfig.java`

### New files — Infrastructure web
- `src/main/java/com/ossflow/identity/auth/infrastructure/web/dto/RegisterRequest.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/web/dto/LoginRequest.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/web/dto/AuthResponse.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/web/dto/RefreshResponse.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/web/dto/ForgotPasswordRequest.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/web/dto/ResetPasswordRequest.java`
- `src/main/java/com/ossflow/identity/auth/infrastructure/web/AuthController.java`

### New files — Test
- `src/test/java/com/ossflow/identity/auth/AuthIntegrationTest.java`

### Modified files
- `pom.xml` — add spring-security, oauth2-client, mail, jjwt, bucket4j dependencies
- `src/main/resources/application.yml` — add auth, mail, oauth2 config
- `src/main/java/com/ossflow/shared/web/CurrentOwner.java` — read from SecurityContext
- `src/main/java/com/ossflow/shared/config/WebMvcConfig.java` — remove addCorsMappings (CORS moves to SecurityConfig)
- `src/main/resources/certs/private.pem` — generated RSA private key (gitignored)
- `src/main/resources/certs/public.pem` — generated RSA public key

---

## Task 1: Flyway Migrations (V240–V244)

**Files:**
- Create: `src/main/resources/db/migration/V240__add_account_table.sql`
- Create: `src/main/resources/db/migration/V241__add_email_verification_token.sql`
- Create: `src/main/resources/db/migration/V242__add_password_reset_token.sql`
- Create: `src/main/resources/db/migration/V243__add_refresh_token.sql`
- Create: `src/main/resources/db/migration/V244__fk_user_profile_account.sql`

- [ ] **Step 1: Create V240__add_account_table.sql**

```sql
CREATE TABLE account (
    id               BIGSERIAL PRIMARY KEY,
    email            VARCHAR(254) NOT NULL,
    password_hash    VARCHAR(72),
    provider         VARCHAR(20)  NOT NULL DEFAULT 'LOCAL',
    provider_id      VARCHAR(255),
    email_verified   BOOLEAN      NOT NULL DEFAULT FALSE,
    token_version    INTEGER      NOT NULL DEFAULT 0,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_account_email       UNIQUE (email),
    CONSTRAINT uq_account_provider_id UNIQUE (provider, provider_id),
    CONSTRAINT ck_account_auth        CHECK (password_hash IS NOT NULL OR provider_id IS NOT NULL)
);
CREATE INDEX ix_account_email ON account(email);

INSERT INTO account (id, email, password_hash, email_verified, token_version)
OVERRIDING SYSTEM VALUE
VALUES (1, 'dev@ossflow.local', '$2a$12$ubsm5H.fe5sfCo6olxbRwO6UTIJhlGhlgqHzXtg8FmRZqz90z/1R.', TRUE, 0);

SELECT setval('account_id_seq', (SELECT MAX(id) FROM account));
```

Note: the bcrypt hash above is for `dev1234` (12 rounds). To verify: `BCrypt.checkpw("dev1234", hash)` returns true.

- [ ] **Step 2: Create V241__add_email_verification_token.sql**

```sql
CREATE TABLE email_verification_token (
    id         BIGSERIAL   PRIMARY KEY,
    account_id BIGINT      NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at    TIMESTAMPTZ,
    CONSTRAINT uq_evtoken_hash UNIQUE (token_hash)
);
CREATE INDEX ix_evtoken_account ON email_verification_token(account_id);
CREATE INDEX ix_evtoken_expires ON email_verification_token(expires_at) WHERE used_at IS NULL;
```

- [ ] **Step 3: Create V242__add_password_reset_token.sql**

```sql
CREATE TABLE password_reset_token (
    id         BIGSERIAL   PRIMARY KEY,
    account_id BIGINT      NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at    TIMESTAMPTZ,
    CONSTRAINT uq_prtoken_hash UNIQUE (token_hash)
);
CREATE INDEX ix_prtoken_account ON password_reset_token(account_id);
CREATE INDEX ix_prtoken_expires ON password_reset_token(expires_at) WHERE used_at IS NULL;
```

- [ ] **Step 4: Create V243__add_refresh_token.sql**

```sql
CREATE TABLE refresh_token (
    id            BIGSERIAL   PRIMARY KEY,
    account_id    BIGINT      NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    token_hash    VARCHAR(64) NOT NULL,
    token_version INTEGER     NOT NULL,
    expires_at    TIMESTAMPTZ NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    revoked_at    TIMESTAMPTZ,
    CONSTRAINT uq_rftoken_hash UNIQUE (token_hash)
);
CREATE INDEX ix_rftoken_account ON refresh_token(account_id) WHERE revoked_at IS NULL;
```

- [ ] **Step 5: Create V244__fk_user_profile_account.sql**

```sql
ALTER TABLE user_profile
    ADD CONSTRAINT fk_user_profile_account
    FOREIGN KEY (owner_id) REFERENCES account(id);
```

- [ ] **Step 6: Commit**

```bash
git add src/main/resources/db/migration/V240__add_account_table.sql \
        src/main/resources/db/migration/V241__add_email_verification_token.sql \
        src/main/resources/db/migration/V242__add_password_reset_token.sql \
        src/main/resources/db/migration/V243__add_refresh_token.sql \
        src/main/resources/db/migration/V244__fk_user_profile_account.sql
git commit -m "feat(auth): add Flyway migrations V240-V244 for account and token tables"
```

---

## Task 2: Add Maven Dependencies

**Files:**
- Modify: `pom.xml`

- [ ] **Step 1: Add auth dependencies to pom.xml**

Inside the `<dependencies>` block (after the existing `spring-boot-starter-test` dependency), add:

```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.12.6</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.12.6</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.12.6</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>com.bucket4j</groupId>
            <artifactId>bucket4j-core</artifactId>
            <version>8.10.1</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
```

- [ ] **Step 2: Commit**

```bash
git add pom.xml
git commit -m "feat(auth): add spring-security, jjwt, bucket4j, mail dependencies"
```

---

## Task 3: RSA Keys + application.yml Config

**Files:**
- Create: `src/main/resources/certs/private.pem` (generated)
- Create: `src/main/resources/certs/public.pem` (generated)
- Modify: `src/main/resources/application.yml`
- Modify: `.gitignore`

- [ ] **Step 1: Generate RSA keypair**

```bash
mkdir -p src/main/resources/certs
openssl genrsa -out src/main/resources/certs/private.pem 2048
openssl rsa -in src/main/resources/certs/private.pem -pubout -out src/main/resources/certs/public.pem
```

Expected output: `writing RSA key`

- [ ] **Step 2: Add private.pem to .gitignore**

Add this line to `.gitignore`:
```
src/main/resources/certs/private.pem
```

- [ ] **Step 3: Add auth, mail, oauth2 config to application.yml**

Append the following to `src/main/resources/application.yml`:

```yaml
auth:
  jwt:
    private-key-path: classpath:certs/private.pem
    public-key-path: classpath:certs/public.pem
    access-token-expiry: 900
    refresh-token-expiry: 604800

spring:
  mail:
    host: smtp.resend.com
    port: 587
    username: resend
    password: ${RESEND_API_KEY:re_placeholder}
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID:placeholder}
            client-secret: ${GOOGLE_CLIENT_SECRET:placeholder}
            scope: email,profile
            redirect-uri: "{baseUrl}/oauth2/callback/{registrationId}"
        provider:
          google:
            authorization-uri: https://accounts.google.com/o/oauth2/v2/auth
            token-uri: https://oauth2.googleapis.com/token
            user-info-uri: https://www.googleapis.com/oauth2/v3/userinfo
            user-name-attribute: sub
```

- [ ] **Step 4: Commit**

```bash
git add src/main/resources/certs/public.pem .gitignore src/main/resources/application.yml
git commit -m "feat(auth): add RSA keypair and auth/mail/oauth2 config"
```

---

## Task 4: Domain Records

**Files:**
- Create: `src/main/java/com/ossflow/identity/auth/domain/AccountProvider.java`
- Create: `src/main/java/com/ossflow/identity/auth/domain/Account.java`
- Create: `src/main/java/com/ossflow/identity/auth/domain/RefreshToken.java`
- Create: `src/main/java/com/ossflow/identity/auth/domain/EmailVerificationToken.java`
- Create: `src/main/java/com/ossflow/identity/auth/domain/PasswordResetToken.java`

- [ ] **Step 1: Create AccountProvider enum**

```java
package com.ossflow.identity.auth.domain;

public enum AccountProvider {
    LOCAL, GOOGLE
}
```

- [ ] **Step 2: Create Account record**

```java
package com.ossflow.identity.auth.domain;

import java.time.Instant;

public record Account(
        Long id,
        String email,
        String passwordHash,
        AccountProvider provider,
        String providerId,
        boolean emailVerified,
        int tokenVersion,
        Instant createdAt,
        Instant updatedAt
) {}
```

- [ ] **Step 3: Create RefreshToken record**

```java
package com.ossflow.identity.auth.domain;

import java.time.Instant;

public record RefreshToken(
        Long id,
        Long accountId,
        String tokenHash,
        int tokenVersion,
        Instant expiresAt,
        Instant createdAt,
        Instant revokedAt
) {}
```

- [ ] **Step 4: Create EmailVerificationToken record**

```java
package com.ossflow.identity.auth.domain;

import java.time.Instant;

public record EmailVerificationToken(
        Long id,
        Long accountId,
        String tokenHash,
        Instant expiresAt,
        Instant usedAt
) {}
```

- [ ] **Step 5: Create PasswordResetToken record**

```java
package com.ossflow.identity.auth.domain;

import java.time.Instant;

public record PasswordResetToken(
        Long id,
        Long accountId,
        String tokenHash,
        Instant expiresAt,
        Instant usedAt
) {}
```

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/ossflow/identity/auth/domain/
git commit -m "feat(auth): add domain records Account, RefreshToken, EmailVerificationToken, PasswordResetToken"
```

---

## Task 5: Repository Port Interfaces

**Files:**
- Create: `src/main/java/com/ossflow/identity/auth/application/port/AccountRepositoryPort.java`
- Create: `src/main/java/com/ossflow/identity/auth/application/port/RefreshTokenRepositoryPort.java`
- Create: `src/main/java/com/ossflow/identity/auth/application/port/EmailVerificationTokenRepositoryPort.java`
- Create: `src/main/java/com/ossflow/identity/auth/application/port/PasswordResetTokenRepositoryPort.java`

- [ ] **Step 1: Create AccountRepositoryPort**

```java
package com.ossflow.identity.auth.application.port;

import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;

import java.util.Optional;

public interface AccountRepositoryPort {
    Optional<Account> findByEmail(String email);
    Optional<Account> findById(Long id);
    Optional<Account> findByProviderAndProviderId(AccountProvider provider, String providerId);
    Account save(Account account);
}
```

- [ ] **Step 2: Create RefreshTokenRepositoryPort**

```java
package com.ossflow.identity.auth.application.port;

import com.ossflow.identity.auth.domain.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepositoryPort {
    RefreshToken save(RefreshToken token);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void revokeByAccountId(Long accountId);
}
```

- [ ] **Step 3: Create EmailVerificationTokenRepositoryPort**

```java
package com.ossflow.identity.auth.application.port;

import com.ossflow.identity.auth.domain.EmailVerificationToken;

import java.util.Optional;

public interface EmailVerificationTokenRepositoryPort {
    EmailVerificationToken save(EmailVerificationToken token);
    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);
    void deleteByAccountId(Long accountId);
}
```

- [ ] **Step 4: Create PasswordResetTokenRepositoryPort**

```java
package com.ossflow.identity.auth.application.port;

import com.ossflow.identity.auth.domain.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepositoryPort {
    PasswordResetToken save(PasswordResetToken token);
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
}
```

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/ossflow/identity/auth/application/port/
git commit -m "feat(auth): add repository port interfaces for auth domain"
```

---

## Task 6: RsaKeyConfig + AccountPrincipal + AccountDetailsService

**Files:**
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/security/RsaKeyConfig.java`
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/security/AccountPrincipal.java`
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/security/AccountDetailsService.java`

- [ ] **Step 1: Create RsaKeyConfig**

```java
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
```

- [ ] **Step 2: Create AccountPrincipal**

```java
package com.ossflow.identity.auth.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record AccountPrincipal(Long id, String email) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
```

- [ ] **Step 3: Create AccountDetailsService**

```java
package com.ossflow.identity.auth.infrastructure.security;

import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountDetailsService implements UserDetailsService {

    private final AccountRepositoryPort accountRepository;

    public AccountDetailsService(AccountRepositoryPort accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return accountRepository.findByEmail(email)
                .map(account -> new AccountPrincipal(account.id(), account.email()))
                .orElseThrow(() -> new UsernameNotFoundException("Account not found: " + email));
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/ossflow/identity/auth/infrastructure/security/RsaKeyConfig.java \
        src/main/java/com/ossflow/identity/auth/infrastructure/security/AccountPrincipal.java \
        src/main/java/com/ossflow/identity/auth/infrastructure/security/AccountDetailsService.java
git commit -m "feat(auth): add RsaKeyConfig, AccountPrincipal, AccountDetailsService"
```

---

## Task 7: Persistence Layer — Account

**Files:**
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/AccountEntity.java`
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/AccountJpaRepository.java`
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/AccountPersistenceMapper.java`
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/AccountPersistenceAdapter.java`

- [ ] **Step 1: Create AccountEntity**

Note: AccountEntity does NOT extend BaseEntity (which adds owner_id, version, deleted_at etc.). It manages its own timestamps.

```java
package com.ossflow.identity.auth.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "account")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "password_hash", length = 72)
    private String passwordHash;

    @Column(name = "provider", nullable = false, length = 20)
    private String provider;

    @Column(name = "provider_id", length = 255)
    private String providerId;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "token_version", nullable = false)
    private int tokenVersion;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
```

- [ ] **Step 2: Create AccountJpaRepository**

```java
package com.ossflow.identity.auth.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, Long> {
    Optional<AccountEntity> findByEmail(String email);
    Optional<AccountEntity> findByProviderAndProviderId(String provider, String providerId);
}
```

- [ ] **Step 3: Create AccountPersistenceMapper**

```java
package com.ossflow.identity.auth.infrastructure.persistence;

import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;
import org.springframework.stereotype.Component;

@Component
public class AccountPersistenceMapper {

    public Account toDomain(AccountEntity entity) {
        return new Account(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                AccountProvider.valueOf(entity.getProvider()),
                entity.getProviderId(),
                entity.isEmailVerified(),
                entity.getTokenVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public AccountEntity toEntity(Account domain) {
        return AccountEntity.builder()
                .id(domain.id())
                .email(domain.email())
                .passwordHash(domain.passwordHash())
                .provider(domain.provider().name())
                .providerId(domain.providerId())
                .emailVerified(domain.emailVerified())
                .tokenVersion(domain.tokenVersion())
                .createdAt(domain.createdAt())
                .updatedAt(domain.updatedAt())
                .build();
    }
}
```

- [ ] **Step 4: Create AccountPersistenceAdapter**

```java
package com.ossflow.identity.auth.infrastructure.persistence;

import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AccountPersistenceAdapter implements AccountRepositoryPort {

    private final AccountJpaRepository jpaRepository;
    private final AccountPersistenceMapper mapper;

    public AccountPersistenceAdapter(AccountJpaRepository jpaRepository, AccountPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<Account> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Account> findByProviderAndProviderId(AccountProvider provider, String providerId) {
        return jpaRepository.findByProviderAndProviderId(provider.name(), providerId).map(mapper::toDomain);
    }

    @Override
    public Account save(Account account) {
        AccountEntity entity = mapper.toEntity(account);
        return mapper.toDomain(jpaRepository.save(entity));
    }
}
```

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/ossflow/identity/auth/infrastructure/persistence/Account*
git commit -m "feat(auth): add Account persistence layer (entity, repository, mapper, adapter)"
```

---

## Task 8: Persistence Layer — Token Entities

**Files:**
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/RefreshTokenEntity.java`
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/RefreshTokenJpaRepository.java`
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/RefreshTokenPersistenceAdapter.java`
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/EmailVerificationTokenEntity.java`
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/EmailVerificationTokenJpaRepository.java`
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/EmailVerificationTokenPersistenceAdapter.java`
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/PasswordResetTokenEntity.java`
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/PasswordResetTokenJpaRepository.java`
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/PasswordResetTokenPersistenceAdapter.java`

- [ ] **Step 1: Create RefreshTokenEntity**

```java
package com.ossflow.identity.auth.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "refresh_token")
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "token_version", nullable = false)
    private int tokenVersion;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
```

- [ ] **Step 2: Create RefreshTokenJpaRepository**

```java
package com.ossflow.identity.auth.infrastructure.persistence;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Transactional
    @Query("UPDATE RefreshTokenEntity r SET r.revokedAt = :now WHERE r.accountId = :accountId AND r.revokedAt IS NULL")
    void revokeAllByAccountId(@Param("accountId") Long accountId, @Param("now") Instant now);
}
```

- [ ] **Step 3: Create RefreshTokenPersistenceAdapter**

```java
package com.ossflow.identity.auth.infrastructure.persistence;

import com.ossflow.identity.auth.application.port.RefreshTokenRepositoryPort;
import com.ossflow.identity.auth.domain.RefreshToken;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class RefreshTokenPersistenceAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository jpaRepository;

    public RefreshTokenPersistenceAdapter(RefreshTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .id(token.id())
                .accountId(token.accountId())
                .tokenHash(token.tokenHash())
                .tokenVersion(token.tokenVersion())
                .expiresAt(token.expiresAt())
                .createdAt(token.createdAt())
                .revokedAt(token.revokedAt())
                .build();
        RefreshTokenEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(this::toDomain);
    }

    @Override
    public void revokeByAccountId(Long accountId) {
        jpaRepository.revokeAllByAccountId(accountId, Instant.now());
    }

    private RefreshToken toDomain(RefreshTokenEntity e) {
        return new RefreshToken(e.getId(), e.getAccountId(), e.getTokenHash(),
                e.getTokenVersion(), e.getExpiresAt(), e.getCreatedAt(), e.getRevokedAt());
    }
}
```

- [ ] **Step 4: Create EmailVerificationTokenEntity**

```java
package com.ossflow.identity.auth.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "email_verification_token")
public class EmailVerificationTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used_at")
    private Instant usedAt;
}
```

- [ ] **Step 5: Create EmailVerificationTokenJpaRepository**

```java
package com.ossflow.identity.auth.infrastructure.persistence;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface EmailVerificationTokenJpaRepository extends JpaRepository<EmailVerificationTokenEntity, Long> {
    Optional<EmailVerificationTokenEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Transactional
    void deleteByAccountId(Long accountId);
}
```

- [ ] **Step 6: Create EmailVerificationTokenPersistenceAdapter**

```java
package com.ossflow.identity.auth.infrastructure.persistence;

import com.ossflow.identity.auth.application.port.EmailVerificationTokenRepositoryPort;
import com.ossflow.identity.auth.domain.EmailVerificationToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EmailVerificationTokenPersistenceAdapter implements EmailVerificationTokenRepositoryPort {

    private final EmailVerificationTokenJpaRepository jpaRepository;

    public EmailVerificationTokenPersistenceAdapter(EmailVerificationTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public EmailVerificationToken save(EmailVerificationToken token) {
        EmailVerificationTokenEntity entity = EmailVerificationTokenEntity.builder()
                .id(token.id())
                .accountId(token.accountId())
                .tokenHash(token.tokenHash())
                .expiresAt(token.expiresAt())
                .usedAt(token.usedAt())
                .build();
        EmailVerificationTokenEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<EmailVerificationToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(this::toDomain);
    }

    @Override
    public void deleteByAccountId(Long accountId) {
        jpaRepository.deleteByAccountId(accountId);
    }

    private EmailVerificationToken toDomain(EmailVerificationTokenEntity e) {
        return new EmailVerificationToken(e.getId(), e.getAccountId(), e.getTokenHash(),
                e.getExpiresAt(), e.getUsedAt());
    }
}
```

- [ ] **Step 7: Create PasswordResetTokenEntity**

```java
package com.ossflow.identity.auth.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "password_reset_token")
public class PasswordResetTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used_at")
    private Instant usedAt;
}
```

- [ ] **Step 8: Create PasswordResetTokenJpaRepository**

```java
package com.ossflow.identity.auth.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenJpaRepository extends JpaRepository<PasswordResetTokenEntity, Long> {
    Optional<PasswordResetTokenEntity> findByTokenHash(String tokenHash);
}
```

- [ ] **Step 9: Create PasswordResetTokenPersistenceAdapter**

```java
package com.ossflow.identity.auth.infrastructure.persistence;

import com.ossflow.identity.auth.application.port.PasswordResetTokenRepositoryPort;
import com.ossflow.identity.auth.domain.PasswordResetToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PasswordResetTokenPersistenceAdapter implements PasswordResetTokenRepositoryPort {

    private final PasswordResetTokenJpaRepository jpaRepository;

    public PasswordResetTokenPersistenceAdapter(PasswordResetTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        PasswordResetTokenEntity entity = PasswordResetTokenEntity.builder()
                .id(token.id())
                .accountId(token.accountId())
                .tokenHash(token.tokenHash())
                .expiresAt(token.expiresAt())
                .usedAt(token.usedAt())
                .build();
        PasswordResetTokenEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<PasswordResetToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(this::toDomain);
    }

    private PasswordResetToken toDomain(PasswordResetTokenEntity e) {
        return new PasswordResetToken(e.getId(), e.getAccountId(), e.getTokenHash(),
                e.getExpiresAt(), e.getUsedAt());
    }
}
```

- [ ] **Step 10: Commit**

```bash
git add src/main/java/com/ossflow/identity/auth/infrastructure/persistence/RefreshToken* \
        src/main/java/com/ossflow/identity/auth/infrastructure/persistence/EmailVerificationToken* \
        src/main/java/com/ossflow/identity/auth/infrastructure/persistence/PasswordResetToken*
git commit -m "feat(auth): add token persistence layers (refresh, email verification, password reset)"
```

---

## Task 9: JwtService

**Files:**
- Create: `src/main/java/com/ossflow/identity/auth/application/JwtService.java`

- [ ] **Step 1: Create JwtService**

```java
package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.domain.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private final long accessTokenExpirySeconds;

    public JwtService(RSAPrivateKey privateKey, RSAPublicKey publicKey,
                      com.ossflow.identity.auth.infrastructure.security.RsaKeyConfig config) {
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
            return Optional.empty();
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/com/ossflow/identity/auth/application/JwtService.java
git commit -m "feat(auth): add JwtService with RS256 sign and validate"
```

---

## Task 10: EmailService

**Files:**
- Create: `src/main/java/com/ossflow/identity/auth/application/EmailService.java`

- [ ] **Step 1: Create EmailService**

```java
package com.ossflow.identity.auth.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String frontendUrl;

    public EmailService(JavaMailSender mailSender,
                        @Value("${app.frontend-url:http://localhost:5173}") String frontendUrl) {
        this.mailSender = mailSender;
        this.frontendUrl = frontendUrl;
    }

    public void sendVerificationEmail(String to, String rawToken) {
        String link = frontendUrl + "/verify-email?token=" + rawToken;
        String html = """
                <html><body style="font-family: sans-serif; background: #0a0a0a; color: #e5e5e5; padding: 40px;">
                  <h2 style="color: #a78bfa;">Verifica tu correo en OssFlow</h2>
                  <p>Haz clic en el botón para verificar tu cuenta:</p>
                  <a href="%s" style="display:inline-block;padding:12px 24px;background:#7c3aed;color:white;border-radius:6px;text-decoration:none;font-weight:bold;">
                    Verificar correo
                  </a>
                  <p style="color:#666;margin-top:24px;">Este enlace expira en 24 horas. Si no creaste una cuenta en OssFlow, ignora este correo.</p>
                </body></html>
                """.formatted(link);
        send(to, "Verifica tu correo en OssFlow", html);
    }

    public void sendPasswordResetEmail(String to, String rawToken) {
        String link = frontendUrl + "/reset-password?token=" + rawToken;
        String html = """
                <html><body style="font-family: sans-serif; background: #0a0a0a; color: #e5e5e5; padding: 40px;">
                  <h2 style="color: #a78bfa;">Restablecer contraseña — OssFlow</h2>
                  <p>Haz clic en el botón para restablecer tu contraseña:</p>
                  <a href="%s" style="display:inline-block;padding:12px 24px;background:#7c3aed;color:white;border-radius:6px;text-decoration:none;font-weight:bold;">
                    Restablecer contraseña
                  </a>
                  <p style="color:#666;margin-top:24px;">Este enlace expira en 1 hora. Si no solicitaste esto, ignora este correo.</p>
                </body></html>
                """.formatted(link);
        send(to, "Restablecer contraseña — OssFlow", html);
    }

    private void send(String to, String subject, String html) {
        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("noreply@ossflow.app");
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/com/ossflow/identity/auth/application/EmailService.java
git commit -m "feat(auth): add EmailService with Resend SMTP and HTML templates in Spanish"
```

---

## Task 11: AuthService

**Files:**
- Create: `src/main/java/com/ossflow/identity/auth/application/AuthService.java`

- [ ] **Step 1: Create AuthService**

```java
package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.application.port.EmailVerificationTokenRepositoryPort;
import com.ossflow.identity.auth.application.port.PasswordResetTokenRepositoryPort;
import com.ossflow.identity.auth.application.port.RefreshTokenRepositoryPort;
import com.ossflow.identity.auth.domain.*;
import com.ossflow.identity.auth.infrastructure.web.dto.*;
import com.ossflow.shared.exception.BadRequestException;
import com.ossflow.shared.exception.NotFoundException;
import com.ossflow.shared.exception.UnprocessableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Slf4j
@Service
public class AuthService {

    private static final int BCRYPT_STRENGTH = 12;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AccountRepositoryPort accountRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final EmailVerificationTokenRepositoryPort emailVerificationTokenRepository;
    private final PasswordResetTokenRepositoryPort passwordResetTokenRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(AccountRepositoryPort accountRepository,
                       RefreshTokenRepositoryPort refreshTokenRepository,
                       EmailVerificationTokenRepositoryPort emailVerificationTokenRepository,
                       PasswordResetTokenRepositoryPort passwordResetTokenRepository,
                       JwtService jwtService,
                       EmailService emailService) {
        this.accountRepository = accountRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.passwordEncoder = new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (accountRepository.findByEmail(request.email()).isPresent()) {
            throw new BadRequestException("EMAIL_ALREADY_REGISTERED", "El correo ya está registrado");
        }
        String hash = passwordEncoder.encode(request.password());
        Account account = accountRepository.save(new Account(
                null, request.email(), hash, AccountProvider.LOCAL,
                null, false, 0, null, null
        ));
        String rawToken = generateToken();
        emailVerificationTokenRepository.save(new EmailVerificationToken(
                null, account.id(), sha256(rawToken),
                Instant.now().plusSeconds(86400), null
        ));
        emailService.sendVerificationEmail(account.email(), rawToken);
    }

    @Transactional
    public LoginResult login(LoginRequest request) {
        Account account = accountRepository.findByEmail(request.email())
                .filter(a -> a.passwordHash() != null && passwordEncoder.matches(request.password(), a.passwordHash()))
                .orElseThrow(() -> new UnprocessableException("INVALID_CREDENTIALS", "Credenciales inválidas"));

        if (!account.emailVerified()) {
            throw new UnprocessableException("EMAIL_NOT_VERIFIED", "Debes verificar tu correo antes de iniciar sesión");
        }

        String accessToken = jwtService.issueAccessToken(account);
        String rawRefreshToken = generateToken();
        refreshTokenRepository.save(new RefreshToken(
                null, account.id(), sha256(rawRefreshToken), account.tokenVersion(),
                Instant.now().plusSeconds(604800), Instant.now(), null
        ));
        return new LoginResult(accessToken, rawRefreshToken, account);
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        String hash = sha256(rawRefreshToken);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(rt ->
                refreshTokenRepository.revokeByAccountId(rt.accountId())
        );
    }

    @Transactional
    public RefreshResult refresh(String rawRefreshToken) {
        String hash = sha256(rawRefreshToken);
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new UnprocessableException("INVALID_REFRESH_TOKEN", "Token inválido"));

        if (stored.revokedAt() != null || stored.expiresAt().isBefore(Instant.now())) {
            throw new UnprocessableException("INVALID_REFRESH_TOKEN", "Token expirado o revocado");
        }

        Account account = accountRepository.findById(stored.accountId())
                .orElseThrow(() -> new NotFoundException("ACCOUNT_NOT_FOUND", "Cuenta no encontrada"));

        if (stored.tokenVersion() != account.tokenVersion()) {
            throw new UnprocessableException("INVALID_REFRESH_TOKEN", "Token invalidado");
        }

        // Rotate: revoke old, issue new
        refreshTokenRepository.revokeByAccountId(account.id());
        String newRawToken = generateToken();
        refreshTokenRepository.save(new RefreshToken(
                null, account.id(), sha256(newRawToken), account.tokenVersion(),
                Instant.now().plusSeconds(604800), Instant.now(), null
        ));

        String accessToken = jwtService.issueAccessToken(account);
        return new RefreshResult(accessToken, newRawToken);
    }

    @Transactional
    public void verifyEmail(String rawToken) {
        String hash = sha256(rawToken);
        EmailVerificationToken token = emailVerificationTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new BadRequestException("INVALID_TOKEN", "Token inválido o expirado"));

        if (token.usedAt() != null || token.expiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("INVALID_TOKEN", "Token inválido o expirado");
        }

        Account account = accountRepository.findById(token.accountId())
                .orElseThrow(() -> new NotFoundException("ACCOUNT_NOT_FOUND", "Cuenta no encontrada"));
        Account verified = new Account(account.id(), account.email(), account.passwordHash(),
                account.provider(), account.providerId(), true, account.tokenVersion(),
                account.createdAt(), account.updatedAt());
        accountRepository.save(verified);

        // Mark token as used
        emailVerificationTokenRepository.save(new EmailVerificationToken(
                token.id(), token.accountId(), token.tokenHash(), token.expiresAt(), Instant.now()
        ));
    }

    @Transactional
    public void resendVerification(String email) {
        // Anti-enumeration: always 200, only send if account exists and unverified
        accountRepository.findByEmail(email)
                .filter(a -> !a.emailVerified())
                .ifPresent(account -> {
                    emailVerificationTokenRepository.deleteByAccountId(account.id());
                    String rawToken = generateToken();
                    emailVerificationTokenRepository.save(new EmailVerificationToken(
                            null, account.id(), sha256(rawToken),
                            Instant.now().plusSeconds(86400), null
                    ));
                    emailService.sendVerificationEmail(account.email(), rawToken);
                });
    }

    @Transactional
    public void forgotPassword(String email) {
        // Anti-enumeration: always 200
        accountRepository.findByEmail(email).ifPresent(account -> {
            String rawToken = generateToken();
            passwordResetTokenRepository.save(new PasswordResetToken(
                    null, account.id(), sha256(rawToken),
                    Instant.now().plusSeconds(3600), null
            ));
            emailService.sendPasswordResetEmail(account.email(), rawToken);
        });
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        String hash = sha256(rawToken);
        PasswordResetToken token = passwordResetTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new BadRequestException("INVALID_TOKEN", "Token inválido o expirado"));

        if (token.usedAt() != null || token.expiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("INVALID_TOKEN", "Token inválido o expirado");
        }

        Account account = accountRepository.findById(token.accountId())
                .orElseThrow(() -> new NotFoundException("ACCOUNT_NOT_FOUND", "Cuenta no encontrada"));

        String newHash = passwordEncoder.encode(newPassword);
        int newTokenVersion = account.tokenVersion() + 1;
        Account updated = new Account(account.id(), account.email(), newHash,
                account.provider(), account.providerId(), account.emailVerified(), newTokenVersion,
                account.createdAt(), account.updatedAt());
        accountRepository.save(updated);

        // Mark token as used
        passwordResetTokenRepository.save(new PasswordResetToken(
                token.id(), token.accountId(), token.tokenHash(), token.expiresAt(), Instant.now()
        ));

        // Revoke all refresh tokens (token_version bump invalidates them)
        refreshTokenRepository.revokeByAccountId(account.id());
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public record LoginResult(String accessToken, String rawRefreshToken, Account account) {}
    public record RefreshResult(String accessToken, String rawRefreshToken) {}
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/com/ossflow/identity/auth/application/AuthService.java
git commit -m "feat(auth): add AuthService with register, login, logout, refresh, email verification, password reset"
```

---

## Task 12: OAuth2 Services

**Files:**
- Create: `src/main/java/com/ossflow/identity/auth/application/OAuth2UserService.java`
- Create: `src/main/java/com/ossflow/identity/auth/application/OAuth2SuccessHandler.java`

- [ ] **Step 1: Create OAuth2UserService**

```java
package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;
import com.ossflow.identity.auth.infrastructure.security.AccountPrincipal;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final AccountRepositoryPort accountRepository;

    public OAuth2UserService(AccountRepositoryPort accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = super.loadUser(userRequest);
        Map<String, Object> attributes = oauthUser.getAttributes();

        String providerId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");

        Account account = accountRepository.findByProviderAndProviderId(AccountProvider.GOOGLE, providerId)
                .or(() -> accountRepository.findByEmail(email))
                .map(existing -> {
                    // If found by email but different provider, link Google
                    if (existing.provider() == AccountProvider.LOCAL && existing.providerId() == null) {
                        return accountRepository.save(new Account(
                                existing.id(), existing.email(), existing.passwordHash(),
                                AccountProvider.GOOGLE, providerId, true, existing.tokenVersion(),
                                existing.createdAt(), existing.updatedAt()
                        ));
                    }
                    return existing;
                })
                .orElseGet(() -> accountRepository.save(new Account(
                        null, email, null, AccountProvider.GOOGLE, providerId,
                        true, 0, null, null
                )));

        return new DefaultOAuth2User(
                new AccountPrincipal(account.id(), account.email()).getAuthorities(),
                Map.of("sub", providerId, "email", email, "accountId", account.id()),
                "sub"
        );
    }
}
```

- [ ] **Step 2: Create OAuth2SuccessHandler**

```java
package com.ossflow.identity.auth.application;

import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.application.port.RefreshTokenRepositoryPort;
import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.RefreshToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.security.SecureRandom;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AccountRepositoryPort accountRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final JwtService jwtService;
    private final String frontendUrl;
    private final long refreshTokenExpirySeconds;

    public OAuth2SuccessHandler(AccountRepositoryPort accountRepository,
                                RefreshTokenRepositoryPort refreshTokenRepository,
                                JwtService jwtService,
                                @Value("${app.frontend-url:http://localhost:5173}") String frontendUrl,
                                com.ossflow.identity.auth.infrastructure.security.RsaKeyConfig config) {
        this.accountRepository = accountRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.frontendUrl = frontendUrl;
        this.refreshTokenExpirySeconds = config.getRefreshTokenExpiry();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        Long accountId = (Long) oauthUser.getAttributes().get("accountId");

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalStateException("Account not found after OAuth2 login"));

        String accessToken = jwtService.issueAccessToken(account);
        String rawRefreshToken = generateToken();
        refreshTokenRepository.save(new RefreshToken(
                null, account.id(), AuthService.sha256(rawRefreshToken), account.tokenVersion(),
                Instant.now().plusSeconds(refreshTokenExpirySeconds), Instant.now(), null
        ));

        Cookie refreshCookie = new Cookie("refresh_token", rawRefreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/api/auth/refresh");
        refreshCookie.setMaxAge((int) refreshTokenExpirySeconds);
        response.addCookie(refreshCookie);

        getRedirectStrategy().sendRedirect(request, response,
                frontendUrl + "/auth/callback#token=" + accessToken);
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/ossflow/identity/auth/application/OAuth2UserService.java \
        src/main/java/com/ossflow/identity/auth/application/OAuth2SuccessHandler.java
git commit -m "feat(auth): add OAuth2UserService and OAuth2SuccessHandler for Google login"
```

---

## Task 13: Web DTOs + AuthController

**Files:**
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/web/dto/RegisterRequest.java`
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/web/dto/LoginRequest.java`
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/web/dto/AuthResponse.java`
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/web/dto/RefreshResponse.java`
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/web/dto/ForgotPasswordRequest.java`
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/web/dto/ResetPasswordRequest.java`
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/web/AuthController.java`

- [ ] **Step 1: Create DTOs**

`RegisterRequest.java`:
```java
package com.ossflow.identity.auth.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
                message = "La contraseña debe tener al menos 8 caracteres, una mayúscula y un número"
        ) String password,
        @NotBlank @Size(max = 120) String displayName
) {}
```

`LoginRequest.java`:
```java
package com.ossflow.identity.auth.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {}
```

`AuthResponse.java`:
```java
package com.ossflow.identity.auth.infrastructure.web.dto;

public record AuthResponse(
        String accessToken,
        UserDto user
) {
    public record UserDto(Long id, String email) {}
}
```

`RefreshResponse.java`:
```java
package com.ossflow.identity.auth.infrastructure.web.dto;

public record RefreshResponse(String accessToken) {}
```

`ForgotPasswordRequest.java`:
```java
package com.ossflow.identity.auth.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(@NotBlank @Email String email) {}
```

`ResetPasswordRequest.java`:
```java
package com.ossflow.identity.auth.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequest(
        @NotBlank String token,
        @NotBlank @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
                message = "La contraseña debe tener al menos 8 caracteres, una mayúscula y un número"
        ) String newPassword
) {}
```

- [ ] **Step 2: Create AuthController**

```java
package com.ossflow.identity.auth.infrastructure.web;

import com.ossflow.identity.auth.application.AuthService;
import com.ossflow.identity.auth.infrastructure.web.dto.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String REFRESH_COOKIE_NAME = "refresh_token";
    private static final int REFRESH_TTL = 604800;

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletResponse response) {
        AuthService.LoginResult result = authService.login(request);
        setRefreshCookie(response, result.rawRefreshToken(), REFRESH_TTL);
        AuthResponse body = new AuthResponse(
                result.accessToken(),
                new AuthResponse.UserDto(result.account().id(), result.account().email())
        );
        return ResponseEntity.ok(body);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String rawToken = extractRefreshCookie(request);
        if (rawToken != null) {
            authService.logout(rawToken);
        }
        setRefreshCookie(response, "", 0);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(HttpServletRequest request,
                                                   HttpServletResponse response) {
        String rawToken = extractRefreshCookie(request);
        if (rawToken == null) {
            return ResponseEntity.status(401).build();
        }
        AuthService.RefreshResult result = authService.refresh(rawToken);
        setRefreshCookie(response, result.rawRefreshToken(), REFRESH_TTL);
        return ResponseEntity.ok(new RefreshResponse(result.accessToken()));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(@RequestBody ForgotPasswordRequest request) {
        authService.resendVerification(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok().build();
    }

    private void setRefreshCookie(HttpServletResponse response, String value, int maxAge) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/auth/refresh");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    private String extractRefreshCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> REFRESH_COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/ossflow/identity/auth/infrastructure/web/
git commit -m "feat(auth): add AuthController and DTOs for register, login, logout, refresh, password reset"
```

---

## Task 14: JwtAuthenticationFilter + RateLimitingFilter

**Files:**
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/security/JwtAuthenticationFilter.java`
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/security/RateLimitingFilter.java`

- [ ] **Step 1: Create JwtAuthenticationFilter**

```java
package com.ossflow.identity.auth.infrastructure.security;

import com.ossflow.identity.auth.application.JwtService;
import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

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
        jwtService.validateToken(token).ifPresent(claims -> {
            Long accountId = Long.parseLong(claims.getSubject());
            int tokenVersion = claims.get("tokenVersion", Integer.class);

            accountRepository.findById(accountId).ifPresent(account -> {
                if (account.tokenVersion() == tokenVersion) {
                    AccountPrincipal principal = new AccountPrincipal(account.id(), account.email());
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            });
        });

        chain.doFilter(request, response);
    }
}
```

- [ ] **Step 2: Create RateLimitingFilter**

```java
package com.ossflow.identity.auth.infrastructure.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
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
        Bandwidth limit = Bandwidth.classic(capacity, Refill.greedy(capacity, duration));
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
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/ossflow/identity/auth/infrastructure/security/JwtAuthenticationFilter.java \
        src/main/java/com/ossflow/identity/auth/infrastructure/security/RateLimitingFilter.java
git commit -m "feat(auth): add JwtAuthenticationFilter and RateLimitingFilter"
```

---

## Task 15: SecurityConfig

**Files:**
- Create: `src/main/java/com/ossflow/identity/auth/infrastructure/security/SecurityConfig.java`

- [ ] **Step 1: Create SecurityConfig**

```java
package com.ossflow.identity.auth.infrastructure.security;

import com.ossflow.identity.auth.application.JwtService;
import com.ossflow.identity.auth.application.OAuth2SuccessHandler;
import com.ossflow.identity.auth.application.OAuth2UserService;
import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtService jwtService;
    private final AccountRepositoryPort accountRepository;
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    public SecurityConfig(JwtService jwtService,
                          AccountRepositoryPort accountRepository,
                          OAuth2UserService oAuth2UserService,
                          OAuth2SuccessHandler oAuth2SuccessHandler) {
        this.jwtService = jwtService;
        this.accountRepository = accountRepository;
        this.oAuth2UserService = oAuth2UserService;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers(
                    "/api/auth/login",
                    "/api/auth/register",
                    "/api/auth/refresh",
                    "/api/auth/logout",
                    "/api/auth/verify-email",
                    "/api/auth/resend-verification",
                    "/api/auth/forgot-password",
                    "/api/auth/reset-password",
                    "/oauth2/**",
                    "/actuator/**"
                )
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(new RateLimitingFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JwtAuthenticationFilter(jwtService, accountRepository),
                    UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/register",
                    "/api/auth/refresh",
                    "/api/auth/verify-email",
                    "/api/auth/resend-verification",
                    "/api/auth/forgot-password",
                    "/api/auth/reset-password"
                ).permitAll()
                .requestMatchers("/oauth2/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(info -> info.userService(oAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
            "http://localhost:5173",
            "http://localhost:3000",
            "http://127.0.0.1:5173",
            "http://10.10.100.15:5173"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/com/ossflow/identity/auth/infrastructure/security/SecurityConfig.java
git commit -m "feat(auth): add SecurityConfig with CORS, CSRF, JWT filter chain, OAuth2, rate limiting"
```

---

## Task 16: Modify Existing Files

**Files:**
- Modify: `src/main/java/com/ossflow/shared/web/CurrentOwner.java`
- Modify: `src/main/java/com/ossflow/shared/config/WebMvcConfig.java`

- [ ] **Step 1: Update CurrentOwner to read from SecurityContext**

Replace the contents of `CurrentOwner.java` with:

```java
package com.ossflow.shared.web;

import com.ossflow.identity.auth.infrastructure.security.AccountPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentOwner {
    public Long id() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AccountPrincipal principal)) {
            return 1L; // fallback for tests and dev without auth
        }
        return principal.id();
    }
}
```

- [ ] **Step 2: Remove addCorsMappings from WebMvcConfig (CORS now managed by SecurityConfig)**

Replace the contents of `WebMvcConfig.java` with:

```java
package com.ossflow.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    // CORS is configured in SecurityConfig via CorsConfigurationSource bean
}
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/ossflow/shared/web/CurrentOwner.java \
        src/main/java/com/ossflow/shared/config/WebMvcConfig.java
git commit -m "feat(auth): update CurrentOwner to read from SecurityContext, remove CORS from WebMvcConfig"
```

---

## Task 17: Integration Test

**Files:**
- Create: `src/test/java/com/ossflow/identity/auth/AuthIntegrationTest.java`

- [ ] **Step 1: Write the failing integration test**

```java
package com.ossflow.identity.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.identity.auth.application.AuthService;
import com.ossflow.identity.auth.application.port.AccountRepositoryPort;
import com.ossflow.identity.auth.domain.Account;
import com.ossflow.identity.auth.domain.AccountProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

    @Autowired WebApplicationContext wac;
    @Autowired AccountRepositoryPort accountRepository;

    final ObjectMapper json = new ObjectMapper();

    MockMvc mvc() {
        return MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    void register_returns_201() throws Exception {
        mvc().perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"test@example.com","password":"Test1234","displayName":"Tester"}
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void login_with_verified_account_returns_access_token() throws Exception {
        // Arrange: save a verified account directly
        Account account = accountRepository.save(new Account(
                null, "verified@example.com",
                new BCryptPasswordEncoder(12).encode("Test1234"),
                AccountProvider.LOCAL, null, true, 0, null, null
        ));
        assertThat(account.id()).isNotNull();

        // Act: login
        MvcResult result = mvc().perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"verified@example.com","password":"Test1234"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value("verified@example.com"))
                .andReturn();

        String accessToken = json.readTree(result.getResponse().getContentAsString())
                .get("accessToken").asText();
        assertThat(accessToken).isNotBlank();

        // Refresh cookie should be set
        assertThat(result.getResponse().getCookie("refresh_token")).isNotNull();
    }

    @Test
    void login_with_wrong_password_returns_422() throws Exception {
        accountRepository.save(new Account(
                null, "wrong@example.com",
                new BCryptPasswordEncoder(12).encode("RightPass1"),
                AccountProvider.LOCAL, null, true, 0, null, null
        ));

        mvc().perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"wrong@example.com","password":"WrongPass1"}
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void refresh_with_valid_cookie_returns_new_access_token() throws Exception {
        // Arrange: create verified account and login to get refresh cookie
        accountRepository.save(new Account(
                null, "refresh@example.com",
                new BCryptPasswordEncoder(12).encode("Test1234"),
                AccountProvider.LOCAL, null, true, 0, null, null
        ));

        MvcResult loginResult = mvc().perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"refresh@example.com","password":"Test1234"}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        jakarta.servlet.http.Cookie refreshCookie = loginResult.getResponse().getCookie("refresh_token");
        assertThat(refreshCookie).isNotNull();

        // Act: refresh
        mvc().perform(post("/api/auth/refresh")
                        .cookie(refreshCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }
}
```

- [ ] **Step 2: Run test to verify it fails (expected since SecurityConfig not wired yet)**

```bash
cd /Users/adrian/Programacion/repositorio/ossflow/OssFlow
mvn test -pl . -Dtest=AuthIntegrationTest -q 2>&1 | tail -20
```

Expected: compilation error or test failures — this is expected at this stage.

- [ ] **Step 3: Compile the full project to check for errors**

```bash
cd /Users/adrian/Programacion/repositorio/ossflow/OssFlow
mvn compile -q 2>&1 | tail -40
```

Fix any compilation errors before committing.

- [ ] **Step 4: Commit**

```bash
git add src/test/java/com/ossflow/identity/auth/AuthIntegrationTest.java
git commit -m "test(auth): add integration test for register, login, refresh flow"
```

---

## Task 18: Final Verification

- [ ] **Step 1: Run mvn compile to verify no compilation errors**

```bash
cd /Users/adrian/Programacion/repositorio/ossflow/OssFlow
mvn compile 2>&1 | tail -20
```

Expected: `BUILD SUCCESS`

- [ ] **Step 2: Run auth integration tests**

```bash
cd /Users/adrian/Programacion/repositorio/ossflow/OssFlow
mvn test -Dtest=AuthIntegrationTest 2>&1 | tail -30
```

Expected: Tests pass (note: email sending will fail gracefully with placeholder RESEND key — that's OK).

- [ ] **Step 3: Verify existing tests still pass**

```bash
cd /Users/adrian/Programacion/repositorio/ossflow/OssFlow
mvn test 2>&1 | tail -30
```

Investigate and fix any test regressions (most likely: existing integration tests that call `/api/**` now need auth. Fix by adding `.with(SecurityMockMvcRequestPostProcessors.csrf())` and ensuring the test profile permits all endpoints or uses a test security config).

- [ ] **Step 4: Final commit**

```bash
git add -A
git commit -m "feat(auth): complete backend auth system - JWT RS256, OAuth2 Google, BCrypt, email verification, password reset, rate limiting"
```

---

## Appendix: Known Pitfalls

### Bucket4j API in v8.x
The `Bandwidth.classic(capacity, Refill.greedy(...))` API may differ slightly between versions. If `Refill` is not found, use:
```java
Bandwidth limit = Bandwidth.builder().capacity(capacity).refillGreedy(capacity, duration).build();
Bucket bucket = Bucket.builder().addLimit(limit).build();
```

### JJWT 0.12.x API changes
In JJWT 0.12.x, `Jwts.builder().setSubject()` is replaced by `.subject()`. The plan uses the new API.

### Spring Boot 4 + Spring Security 6
Spring Boot 4 ships with Spring Security 6.x. The `HttpSecurity` lambda DSL is mandatory (no deprecated `.and()` chains). The plan uses the correct lambda style throughout.

### Test profile and DB
The test profile (`@ActiveProfiles("test")`) uses H2 in-memory. Flyway migrations must run cleanly on H2. The `OVERRIDING SYSTEM VALUE` in V240 is PostgreSQL-specific — add a test profile that skips or uses a compatible SQL variant, or use Testcontainers.

**If H2 fails on V240**, add `src/test/resources/db/migration/V240__add_account_table.sql` that replaces `OVERRIDING SYSTEM VALUE` with H2-compatible syntax:
```sql
CREATE TABLE account (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    email            VARCHAR(254) NOT NULL,
    password_hash    VARCHAR(72),
    provider         VARCHAR(20)  NOT NULL DEFAULT 'LOCAL',
    provider_id      VARCHAR(255),
    email_verified   BOOLEAN      NOT NULL DEFAULT FALSE,
    token_version    INTEGER      NOT NULL DEFAULT 0,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_account_email UNIQUE (email)
);
INSERT INTO account (id, email, password_hash, email_verified, token_version)
VALUES (1, 'dev@ossflow.local', '$2a$12$ubsm5H.fe5sfCo6olxbRwO6UTIJhlGhlgqHzXtg8FmRZqz90z/1R.', TRUE, 0);
```

### UnprocessableException usage
`UnprocessableException` is used in AuthService for 422 errors. Verify it exists and maps to HTTP 422 in `GlobalExceptionHandler`.
