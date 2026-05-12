# OssFlow Authentication System — Design Spec
**Date:** 2026-05-10  
**Status:** Approved for implementation  
**Branch:** feature/auth

---

## Decisions Summary

| Decisión | Elección |
|---|---|
| Modelo identidad | `account` separado de `user_profile` |
| Proveedores OAuth2 | Google v1, Facebook futuro |
| Sesiones | HttpOnly Cookie (refresh) + JWT en memoria React (access) |
| Algoritmo JWT | RS256 (RSA keypair) |
| Verificación email | Obligatoria antes del primer login |
| Email provider | Resend (via Spring Mail SMTP) |
| Password reset | `token_version` en `account` para invalidar todas las sesiones |
| Rate limiting | Bucket4j por capas + Nginx |
| CSRF | Double Submit Cookie (CookieCsrfTokenRepository) |
| Migración datos | Flyway preserva dev account con `id = 1` |
| Landing page | Dark & técnica (BJJ aesthetics) |

---

## 1. Backend Package Structure

```
com.ossflow.identity.auth/
├── domain/
│   ├── Account.java                    # record: id, email, passwordHash, provider, providerId, emailVerified, tokenVersion, createdAt, updatedAt
│   ├── AccountProvider.java            # enum: LOCAL, GOOGLE
│   ├── RefreshToken.java               # record: id, accountId, tokenHash, expiresAt, createdAt, revokedAt
│   ├── EmailVerificationToken.java     # record: id, accountId, tokenHash, expiresAt, usedAt
│   └── PasswordResetToken.java         # record: id, accountId, tokenHash, expiresAt, usedAt
├── application/
│   ├── AuthService.java
│   ├── OAuth2SuccessHandler.java
│   ├── OAuth2UserService.java
│   ├── JwtService.java                 # RS256 sign/validate
│   ├── EmailService.java               # Resend SMTP
│   ├── RateLimitService.java           # Bucket4j
│   └── port/
│       ├── AccountRepositoryPort.java
│       ├── RefreshTokenRepositoryPort.java
│       ├── EmailVerificationTokenRepositoryPort.java
│       └── PasswordResetTokenRepositoryPort.java
└── infrastructure/
    ├── persistence/
    │   ├── AccountEntity.java
    │   ├── AccountJpaRepository.java
    │   ├── AccountPersistenceAdapter.java
    │   ├── AccountPersistenceMapper.java
    │   ├── RefreshTokenEntity.java
    │   ├── RefreshTokenJpaRepository.java
    │   ├── RefreshTokenPersistenceAdapter.java
    │   ├── EmailVerificationTokenEntity.java
    │   ├── EmailVerificationTokenJpaRepository.java
    │   ├── EmailVerificationTokenPersistenceAdapter.java
    │   ├── PasswordResetTokenEntity.java
    │   ├── PasswordResetTokenJpaRepository.java
    │   └── PasswordResetTokenPersistenceAdapter.java
    ├── security/
    │   ├── SecurityConfig.java
    │   ├── JwtAuthenticationFilter.java
    │   ├── AccountPrincipal.java
    │   ├── AccountDetailsService.java
    │   ├── RsaKeyConfig.java
    │   └── RateLimitingFilter.java
    └── web/
        ├── AuthController.java
        └── dto/
            ├── RegisterRequest.java
            ├── LoginRequest.java
            ├── AuthResponse.java
            ├── RefreshResponse.java
            ├── ForgotPasswordRequest.java
            └── ResetPasswordRequest.java
```

`CurrentOwner.id()` se reemplaza para leer de SecurityContext — todos los controllers existentes siguen funcionando sin cambios.

---

## 2. Database Schema

### V240 — account + dev account (id=1)
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
VALUES (1, 'dev@ossflow.local', '$2a$12$PLACEHOLDER_REPLACE_WITH_BCRYPT', TRUE, 0);

SELECT setval('account_id_seq', (SELECT MAX(id) FROM account));
```

### V241 — email_verification_token
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

### V242 — password_reset_token
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

### V243 — refresh_token
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

### V244 — FK user_profile → account
```sql
ALTER TABLE user_profile
    ADD CONSTRAINT fk_user_profile_account
    FOREIGN KEY (owner_id) REFERENCES account(id);
```

---

## 3. Spring Security Filter Chain

```
[1] CorsFilter          — allowedOrigins exactos, allowCredentials=true
[2] CsrfFilter          — Double Submit Cookie (CookieCsrfTokenRepository.withHttpOnlyFalse())
[3] RateLimitingFilter  — Bucket4j por IP/endpoint en /api/auth/**
[4] JwtAuthFilter       — extrae Bearer → valida RS256 → set SecurityContext
[5] Authorization       — permitAll en endpoints públicos, authenticated en el resto
```

Session: `STATELESS` — ninguna HttpSession.

---

## 4. JWT Flow

### Login
```
POST /api/auth/login { email, password }
→ BCrypt.matches(password, hash)
→ JwtService.issueAccessToken(accountId, email, tokenVersion)  [RS256, 15min]
→ RefreshTokenRepo.save(SHA256(rawToken), accountId, 7d)
← 200 { accessToken, user }
← Set-Cookie: refresh_token=...; HttpOnly; Secure; SameSite=Strict; Path=/api/auth/refresh; Max-Age=604800
```

### Refresh
```
POST /api/auth/refresh (sin body, solo cookie)
→ SHA256(cookie) → buscar en refresh_token
→ check revoked_at IS NULL + expires_at > NOW() + token_version == account.token_version
→ revocar RT viejo, emitir RT nuevo (rotación)
← 200 { accessToken }
← Set-Cookie: refresh_token=... (nuevo, 7 días)
```

### Logout
```
POST /api/auth/logout
→ marcar refresh_token.revoked_at = NOW()
← 204
← Set-Cookie: refresh_token=; Max-Age=0
```

---

## 5. OAuth2 Google Flow

```
GET /oauth2/authorize/google
→ Spring Security → redirect Google
→ usuario autentica
→ GET /oauth2/callback/google?code=...
→ Spring intercambia code → user info (sub, email, name)
→ OAuth2UserService: findByProviderId o crear cuenta
→ OAuth2SuccessHandler: emitir tokens
← redirect /auth/callback#token=<AT>
← Set-Cookie: refresh_token=... HttpOnly
```

El AT viaja en el hash de la URL (nunca llega al servidor). OAuthCallbackPage.tsx lo lee y lo guarda en Zustand.

---

## 6. Frontend Router

```
PublicShell (sin auth)
  /                   LandingPage
  /login              LoginPage
  /register           RegisterPage
  /verify-email       VerifyEmailPage
  /forgot-password    ForgotPasswordPage
  /reset-password     ResetPasswordPage

/auth/callback        OAuthCallbackPage

AuthGuard (protected)
  AppLayout
    /onboarding       OnboardingPage
    /                 HomePage
    /diario/**        (rutas existentes sin cambios)
    /estudio/**
    /planificacion/**
    /analisis
    /profile
    ...
```

---

## 7. Auth State (Zustand)

```typescript
interface AuthState {
  accessToken: string | null   // en memoria, NUNCA en localStorage
  user: { id: number; email: string; displayName: string | null } | null
  isInitialized: boolean
}
```

En hard refresh: store vacío → AuthGuard intenta silent refresh via cookie HttpOnly → si 401 → redirect /login.

---

## 8. API Contract

| Endpoint | Método | Rate limit | Auth requerida |
|---|---|---|---|
| /api/auth/register | POST | 5/10min/IP | No |
| /api/auth/login | POST | 10/5min/IP | No |
| /api/auth/logout | POST | — | Bearer |
| /api/auth/refresh | POST | 10/min/IP | Cookie only |
| /api/auth/verify-email | GET | 10/h/IP | No |
| /api/auth/resend-verification | POST | 3/h/IP | No |
| /api/auth/forgot-password | POST | 3/h/IP | No |
| /api/auth/reset-password | POST | 5/10min/IP | No |
| /oauth2/authorize/google | GET | — | No |
| /oauth2/callback/google | GET | — | No (Spring) |

Respuestas anti-enumeración en resend-verification, forgot-password (siempre 200).
Error 401 en login: siempre "INVALID_CREDENTIALS" sin distinguir email/password.

---

## 9. Security Threat Model

| Amenaza | Mitigación |
|---|---|
| XSS token theft | AT en memoria (Zustand), nunca localStorage |
| XSS refresh theft | Cookie HttpOnly — JS no puede leerla |
| CSRF | Double Submit Cookie (XSRF-TOKEN + X-XSRF-TOKEN header) |
| Brute force login | Bucket4j 10/5min + Nginx primera capa |
| Password reset abuse | Anti-enumeración, tokens SHA-256 en DB, expiry 1h |
| Refresh token theft | Secure+SameSite=Strict, rotación en cada refresh |
| Session fixation | Nuevo refresh token en cada login |
| JWT algorithm confusion | JJWT whitelist RS256 únicamente |
| Token replay post-reset | token_version en JWT validado contra DB |
| SQL injection | Spring Data JPA parameterized queries, cero concatenación |
| IDOR | Todos los queries scoped por ownerId = currentOwner.id() |
| Google account linking exploit | Lookup por email primero, cuenta existente gana |

---

## 10. Implementation Order

### Fase 1 — DB (sin riesgo, sin cambios de código)
1. V240–V244 migrations
2. Generar BCrypt hash del dev password offline

### Fase 2 — Spring Security scaffold (todo sigue funcionando)
3. Añadir dependencias: spring-boot-starter-security, oauth2-client, jjwt, bucket4j
4. SecurityConfig con permitAll temporal en todo /api/**
5. RsaKeyConfig, AccountEntity, AccountDetailsService, JwtAuthFilter (no-op sin Bearer)

### Fase 3 — Core auth email/password
6. JwtService (RS256)
7. RefreshToken domain + persistence
8. register + email verification
9. login + cookie
10. refresh con rotación
11. logout
12. password reset + token_version bump
13. Activar authorization en SecurityConfig
14. Reemplazar CurrentOwner.id() → SecurityContext

### Fase 4 — Frontend auth
15. authStore.ts (Zustand)
16. apiClient con Bearer injection + 401 retry
17. LoginPage, RegisterPage, VerifyEmailPage
18. ForgotPasswordPage, ResetPasswordPage
19. Reescribir AuthGuard
20. Reestructurar router

### Fase 5 — Google OAuth2
21. Configurar Google Cloud Console
22. OAuth2UserService + OAuth2SuccessHandler
23. OAuthCallbackPage.tsx
24. GoogleLoginButton component

### Fase 6 — Rate limiting + hardening
25. RateLimitingFilter Bucket4j
26. Nginx rate limit layer
27. CSRF end-to-end verification
28. Security checklist pass

### Fase 7 — Landing page
29. LandingPage dark & técnica (BJJ aesthetics)
30. Deploy a LXC

---

## Dependencies to Add (pom.xml)

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
```
