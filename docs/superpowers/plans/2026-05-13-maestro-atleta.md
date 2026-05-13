# Maestro-Atleta (Fase 1+2) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Añadir sistema de roles (ATHLETE / ATHLETE_COACH) y vinculación maestro-atleta con invitaciones, notificaciones in-app + email, y panel de coaching en el frontend.

**Architecture:** Rol como enum VARCHAR en `Account` (identidad), leído desde cache Caffeine de 60s en el filtro JWT. Nuevo bounded context `coaching/` con tres sub-features (invitation, relationship, notification). `AthleteProfileComposer` agrega datos de repositorios existentes pasando `athleteId` como `ownerId` — sin cambiar los repositorios de dominio. Emails HTML rediseñados con la identidad visual de OssFlow (dark mode, Playfair Display, paleta púrpura/crema).

**Tech Stack:** Spring Boot 4 · Java 25 · PostgreSQL 17 · Flyway · MapStruct · Lombok · React 19 · TypeScript · TanStack Query v5 · React Hook Form · Zod v4 · Tailwind v4 · ky v2

**Rama de desarrollo:** `feature/maestro-atleta` (nueva desde `main`)
**Deploy al terminar:** LXC `10.10.100.15` — NO producción VPS `82.165.143.40`

---

## Preparación: crear rama

- [ ] Crear rama de desarrollo:
```bash
cd /ruta/OssFlow && git checkout -b feature/maestro-atleta
cd /ruta/OssFlow-frontend && git checkout -b feature/maestro-atleta
```

---

## Task 1: Migración V256 — campo `role` en `account`

**Files:**
- Create: `src/main/resources/db/migration/V256__add_role_to_account.sql`

- [ ] **Step 1: Crear migración**

```sql
-- V256__add_role_to_account.sql
ALTER TABLE account
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'ATHLETE';

ALTER TABLE account
    ADD CONSTRAINT ck_account_role
    CHECK (role IN ('ATHLETE', 'ATHLETE_COACH'));
```

- [ ] **Step 2: Aplicar migración y verificar**

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/microsoft-25.jdk/Contents/Home \
  mvn flyway:migrate -Dspring-boot.run.profiles=dev
```

Esperado: `Successfully applied 1 migration to schema "public"` — versión V256.

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/db/migration/V256__add_role_to_account.sql
git commit -m "feat(identity): V256 añadir columna role a account"
```

---

## Task 2: Enum `AccountRole` + refactor `Account` record

**Files:**
- Create: `src/main/java/com/ossflow/identity/auth/domain/AccountRole.java`
- Modify: `src/main/java/com/ossflow/identity/auth/domain/Account.java`

- [ ] **Step 1: Crear enum**

```java
// src/main/java/com/ossflow/identity/auth/domain/AccountRole.java
package com.ossflow.identity.auth.domain;

public enum AccountRole {
    ATHLETE,
    ATHLETE_COACH
}
```

- [ ] **Step 2: Añadir `@Builder(toBuilder=true)` y campo `role` a `Account`**

```java
// src/main/java/com/ossflow/identity/auth/domain/Account.java
package com.ossflow.identity.auth.domain;

import lombok.Builder;
import java.time.Instant;

@Builder(toBuilder = true)
public record Account(
        Long id,
        String email,
        String passwordHash,
        AccountProvider provider,
        String providerId,
        boolean emailVerified,
        int tokenVersion,
        AccountRole role,
        Instant createdAt,
        Instant updatedAt
) {}
```

- [ ] **Step 3: Verificar que el compilador falla en los 27 callsites**

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/microsoft-25.jdk/Contents/Home \
  mvn compile 2>&1 | grep "error:" | head -30
```

Esperado: errores de compilación en `AuthService`, `OAuth2UserService`, `AccountPersistenceMapper` y ficheros de test. Esto es correcto — los pasos siguientes los resuelven.

---

## Task 3: Actualizar `AccountEntity` y `AccountPersistenceMapper`

**Files:**
- Modify: `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/AccountEntity.java`
- Modify: `src/main/java/com/ossflow/identity/auth/infrastructure/persistence/AccountPersistenceMapper.java`

- [ ] **Step 1: Añadir campo `role` a `AccountEntity`**

Abre `AccountEntity.java` y añade después del campo `tokenVersion`:

```java
@Enumerated(EnumType.STRING)
@Column(name = "role", nullable = false, length = 20)
private AccountRole role;
```

Añade también el import:
```java
import com.ossflow.identity.auth.domain.AccountRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
```

- [ ] **Step 2: Propagar `role` en `AccountPersistenceMapper`**

En `toDomain()` añade `.role(entity.getRole())`.
En `toEntity()` añade `entity.setRole(domain.role())`.

- [ ] **Step 3: Compilar solo el módulo de persistencia**

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/microsoft-25.jdk/Contents/Home \
  mvn compile 2>&1 | grep "AccountPersistenceMapper\|AccountEntity" | head -10
```

Esperado: sin errores en estos dos ficheros.

---

## Task 4: Actualizar `AuthService` y `OAuth2UserService`

**Files:**
- Modify: `src/main/java/com/ossflow/identity/auth/application/AuthService.java`
- Modify: `src/main/java/com/ossflow/identity/auth/application/OAuth2UserService.java`
- Modify: `src/main/java/com/ossflow/identity/auth/infrastructure/web/dto/RegisterRequest.java`

- [ ] **Step 1: Añadir campo `role` a `RegisterRequest`**

```java
// RegisterRequest.java — añadir campo (nullable, default en service)
public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8) String password,
        @Nullable AccountRole role   // null → ATHLETE por defecto
) {}
```

Import: `import com.ossflow.identity.auth.domain.AccountRole;`

- [ ] **Step 2: En `AuthService.register()`, resolver rol y usar builder**

Busca el bloque donde se construye el `Account` nuevo en `register()`. Reemplázalo usando el builder:

```java
AccountRole resolvedRole = (request.role() == AccountRole.ATHLETE_COACH)
        ? AccountRole.ATHLETE_COACH
        : AccountRole.ATHLETE;

Account account = Account.builder()
        .email(request.email().toLowerCase())
        .passwordHash(passwordEncoder.encode(request.password()))
        .provider(AccountProvider.LOCAL)
        .providerId(null)
        .emailVerified(false)
        .tokenVersion(0)
        .role(resolvedRole)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
```

- [ ] **Step 3: Convertir todas las copias `new Account(...)` en `AuthService` a `.toBuilder()`**

Busca los patrones `new Account(account.id(), ...)` en `AuthService` (hay ~5, en `verifyEmail`, `changePassword`, `refreshToken`, etc.) y cámbialos por:

```java
// Ejemplo para bump de tokenVersion:
Account updated = account.toBuilder()
        .tokenVersion(account.tokenVersion() + 1)
        .updatedAt(Instant.now())
        .build();
```

- [ ] **Step 4: En `OAuth2UserService`, asignar `ATHLETE` por defecto**

Busca la construcción del `Account` nuevo en `processAttributes()` y usa el builder:

```java
Account newAccount = Account.builder()
        .email(email)
        .passwordHash(null)
        .provider(provider)
        .providerId(providerId)
        .emailVerified(true)
        .tokenVersion(0)
        .role(AccountRole.ATHLETE)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
```

- [ ] **Step 5: Compilar producción (sin tests)**

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/microsoft-25.jdk/Contents/Home \
  mvn compile -DskipTests 2>&1 | tail -5
```

Esperado: `BUILD SUCCESS`.

---

## Task 5: Actualizar `AccountPrincipal` y `JwtAuthenticationFilter`

**Files:**
- Modify: `src/main/java/com/ossflow/identity/auth/infrastructure/security/AccountPrincipal.java`
- Modify: `src/main/java/com/ossflow/identity/auth/infrastructure/security/JwtAuthenticationFilter.java`

- [ ] **Step 1: Añadir `role` a `AccountPrincipal` y actualizar `getAuthorities()`**

```java
package com.ossflow.identity.auth.infrastructure.security;

import com.ossflow.identity.auth.domain.AccountRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record AccountPrincipal(Long id, String email, AccountRole role) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == AccountRole.ATHLETE_COACH) {
            return List.of(
                new SimpleGrantedAuthority("ROLE_ATHLETE"),
                new SimpleGrantedAuthority("ROLE_COACH")
            );
        }
        return List.of(new SimpleGrantedAuthority("ROLE_ATHLETE"));
    }

    @Override public String getPassword() { return null; }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
```

- [ ] **Step 2: Actualizar `JwtAuthenticationFilter` para pasar `role` al construir el principal**

Busca la línea donde se construye `new AccountPrincipal(account.id(), account.email())` y cámbiala por:

```java
var principal = new AccountPrincipal(account.id(), account.email(), account.role());
```

- [ ] **Step 3: Añadir endpoint `PATCH /api/v1/me/role` a `SecurityConfig`**

En `SecurityConfig`, el bloque de `requestMatchers` que permite acceso autenticado ya cubre `/api/v1/**`. Solo hay que verificar que `/api/v1/coaching/**` no está explícitamente bloqueado. Si `anyRequest().authenticated()` ya cubre todo, no se necesita cambio. Añadir comentario:

```java
// /api/v1/coaching/** — cubierto por anyRequest().authenticated()
// @PreAuthorize("hasRole('COACH')") se aplica a nivel de controller
```

- [ ] **Step 4: Compilar**

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/microsoft-25.jdk/Contents/Home \
  mvn compile -DskipTests 2>&1 | tail -5
```

Esperado: `BUILD SUCCESS`.

---

## Task 6: Endpoint `PATCH /api/v1/me/role` + actualizar tests

**Files:**
- Modify: `src/main/java/com/ossflow/identity/auth/infrastructure/web/AccountController.java`
- Modify: todos los ficheros de test que instancian `Account` con constructor canónico (19 ficheros)

- [ ] **Step 1: Añadir DTO y endpoint de cambio de rol en `AccountController`**

```java
// En AccountController.java, añadir:

public record ChangeRoleRequest(@NotNull AccountRole role) {}

@PatchMapping("/me/role")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void changeRole(@AuthenticationPrincipal AccountPrincipal principal,
                       @RequestBody @Valid ChangeRoleRequest request) {
    accountService.changeRole(principal.id(), request.role());
}
```

- [ ] **Step 2: Añadir `changeRole` en `AuthService`**

```java
public void changeRole(Long accountId, AccountRole newRole) {
    Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException(accountId));
    Account updated = account.toBuilder()
            .role(newRole)
            .tokenVersion(account.tokenVersion() + 1)
            .updatedAt(Instant.now())
            .build();
    accountRepository.save(updated);
}
```

- [ ] **Step 3: Actualizar los 19 ficheros de test — patrón de cambio**

En cada fichero de test que usa `new Account(id, email, hash, provider, providerId, verified, tokenVersion, createdAt, updatedAt)`, añadir `AccountRole.ATHLETE` como penúltimo parámetro antes de `createdAt`:

```java
// Antes:
new Account(1L, "test@test.com", "hash", AccountProvider.LOCAL, null, true, 0, now, now)
// Después:
new Account(1L, "test@test.com", "hash", AccountProvider.LOCAL, null, true, 0, AccountRole.ATHLETE, now, now)
```

O mejor, usar el builder donde sea posible:
```java
Account.builder()
    .id(1L).email("test@test.com").passwordHash("hash")
    .provider(AccountProvider.LOCAL).providerId(null)
    .emailVerified(true).tokenVersion(0)
    .role(AccountRole.ATHLETE)
    .createdAt(now).updatedAt(now)
    .build()
```

Ficheros a modificar (según validación del code-architect):
- `AuthServiceLifecycleTest.java` (3 instancias)
- `AuthServiceRefreshRotationTest.java` (1)
- `AuthServiceRegisterTest.java` (2 + actualizar constructor RegisterRequest)
- `AuthIntegrationTest.java` (3)
- `OAuth2UserServiceTest.java` (3)
- `JwtServiceTest.java` (1)
- `OAuth2SuccessHandlerTest.java` (1)
- `JwtAuthenticationFilterCacheTest.java` (2)
- `AuthControllerLogoutTest.java` (2)
- `AuthControllerCookieTest.java` (1)

- [ ] **Step 4: Ejecutar suite completa de tests**

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/microsoft-25.jdk/Contents/Home \
  mvn test 2>&1 | tail -20
```

Esperado: `BUILD SUCCESS`, todos los tests en verde.

- [ ] **Step 5: Commit**

```bash
git add -p
git commit -m "feat(identity): sistema de roles ATHLETE/ATHLETE_COACH con endpoint PATCH /me/role"
```

---

## Task 7: Rediseño de plantillas de email HTML

**Files:**
- Modify: `src/main/java/com/ossflow/identity/auth/application/EmailService.java`

El objetivo es reemplazar los emails inline actuales (fondo `#0a0a0a`, estructura mínima) por plantillas HTML completas armonizadas con la identidad visual de OssFlow: dark mode editorial, Playfair Display como tipografía de cabecera, paleta púrpura/crema.

- [ ] **Step 1: Crear método `baseTemplate()` privado en `EmailService`**

```java
private String baseTemplate(String title, String bodyContent) {
    return """
        <!DOCTYPE html>
        <html lang="es">
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <title>%s</title>
          <style>
            @import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700;900&family=Inter:wght@400;500;600&display=swap');
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body { background-color: #0f0f0f; color: #f0ebe3; font-family: 'Inter', sans-serif; }
            .wrapper { max-width: 560px; margin: 0 auto; padding: 40px 20px; }
            .logo { font-family: 'Playfair Display', serif; font-weight: 900; font-size: 28px;
                    color: #f0ebe3; letter-spacing: -0.5px; margin-bottom: 32px; }
            .logo span { color: #a78bfa; }
            .card { background: #1a1a1a; border: 1px solid #3a3a3a; border-radius: 12px;
                    padding: 36px 32px; }
            .card-title { font-family: 'Playfair Display', serif; font-weight: 700;
                          font-size: 22px; color: #f0ebe3; margin-bottom: 16px; line-height: 1.3; }
            .card-body { font-size: 15px; color: #a0a0a0; line-height: 1.6; margin-bottom: 28px; }
            .btn { display: inline-block; padding: 14px 28px; background: #7c3aed;
                   color: #ffffff; text-decoration: none; font-family: 'Inter', sans-serif;
                   font-weight: 600; font-size: 15px; border-radius: 8px; }
            .btn:hover { background: #6d28d9; }
            .divider { border: none; border-top: 1px solid #3a3a3a; margin: 28px 0; }
            .footer { font-size: 13px; color: #555; line-height: 1.5; margin-top: 24px; }
            .footer a { color: #a78bfa; text-decoration: none; }
            .badge { display: inline-block; background: #2a1a3e; border: 1px solid #4c1d95;
                     color: #a78bfa; font-size: 13px; font-weight: 600; padding: 4px 12px;
                     border-radius: 20px; margin-bottom: 20px; }
          </style>
        </head>
        <body>
          <div class="wrapper">
            <div class="logo">Oss<span>Flow</span></div>
            <div class="card">
              %s
            </div>
            <div class="footer">
              <p>© 2026 OssFlow · Brazilian Jiu-Jitsu Knowledge System</p>
              <p style="margin-top:8px;">Si no esperabas este correo, puedes ignorarlo de forma segura.</p>
            </div>
          </div>
        </body>
        </html>
        """.formatted(title, bodyContent);
}
```

- [ ] **Step 2: Reescribir `verificationBody()`**

```java
public String verificationBody(String rawToken) {
    String link = frontendUrl + "/verify-email?token=" + rawToken;
    String body = """
        <div class="badge">Verificación de cuenta</div>
        <div class="card-title">Confirma tu dirección de correo</div>
        <div class="card-body">
          Gracias por unirte a OssFlow. Haz clic en el botón para verificar tu correo
          y empezar a registrar tu progreso en Brazilian Jiu-Jitsu.
        </div>
        <a href="%s" class="btn">Verificar correo</a>
        <hr class="divider">
        <div class="footer">
          Este enlace expira en <strong style="color:#f0ebe3;">24 horas</strong>.
          Si no creaste una cuenta en OssFlow, ignora este correo.
        </div>
        """.formatted(link);
    return baseTemplate("Verifica tu correo — OssFlow", body);
}
```

- [ ] **Step 3: Reescribir `passwordResetBody()`**

```java
public String passwordResetBody(String rawToken) {
    String link = frontendUrl + "/reset-password?token=" + rawToken;
    String body = """
        <div class="badge">Seguridad</div>
        <div class="card-title">Restablecer contraseña</div>
        <div class="card-body">
          Recibimos una solicitud para restablecer la contraseña de tu cuenta en OssFlow.
          Si fuiste tú, haz clic en el botón. Si no, ignora este correo — tu cuenta está segura.
        </div>
        <a href="%s" class="btn">Restablecer contraseña</a>
        <hr class="divider">
        <div class="footer">
          Este enlace expira en <strong style="color:#f0ebe3;">1 hora</strong>.
        </div>
        """.formatted(link);
    return baseTemplate("Restablecer contraseña — OssFlow", body);
}
```

- [ ] **Step 4: Añadir método genérico `enqueue` a `EmailOutboxService`**

En `EmailOutboxService.java`, añadir método público que acepta subject y body directamente:

```java
public void enqueueCoachingEmail(Long accountId, String recipient,
                                  String subject, String bodyHtml) {
    var entry = EmailOutboxEntry.builder()
            .accountId(accountId)
            .recipient(recipient)
            .subject(subject)
            .body(bodyHtml)
            .status(EmailOutboxStatus.PENDING)
            .createdAt(Instant.now())
            .build();
    emailOutboxRepository.save(entry);
}
```

- [ ] **Step 5: Añadir tres plantillas de coaching a `EmailService`**

```java
public String athleteJoinedSubject() {
    return "Nuevo alumno en OssFlow";
}

public String athleteJoinedBody(String athleteDisplayName) {
    String body = """
        <div class="badge">Nuevo alumno</div>
        <div class="card-title">%s se ha unido a tu gimnasio</div>
        <div class="card-body">
          Un atleta ha redimido tu código de invitación y ahora forma parte de tu lista de alumnos en OssFlow.
          Puedes ver su ficha desde el panel de coaching.
        </div>
        <hr class="divider">
        <div class="footer">Entra en OssFlow para ver la ficha de tu nuevo alumno.</div>
        """.formatted(athleteDisplayName);
    return baseTemplate("Nuevo alumno — OssFlow", body);
}

public String athleteLeftSubject() {
    return "Un alumno se ha desvinculado — OssFlow";
}

public String athleteLeftBody(String athleteDisplayName) {
    String body = """
        <div class="badge">Cambio en tu gimnasio</div>
        <div class="card-title">%s se ha desvinculado</div>
        <div class="card-body">
          El atleta ha decidido desvincular su cuenta de tu gimnasio en OssFlow.
          Ya no tendrás acceso a su ficha.
        </div>
        """.formatted(athleteDisplayName);
    return baseTemplate("Alumno desvinculado — OssFlow", body);
}

public String coachRemovedYouSubject() {
    return "Tu maestro te ha desvinculado — OssFlow";
}

public String coachRemovedYouBody(String coachDisplayName) {
    String body = """
        <div class="badge">Cambio en tu cuenta</div>
        <div class="card-title">%s ha cancelado tu vinculación</div>
        <div class="card-body">
          Tu maestro ha desvinculado tu cuenta de su gimnasio en OssFlow.
          Tus datos de entrenamiento permanecen intactos y son completamente privados.
          Puedes vincularte a otro maestro cuando quieras.
        </div>
        """.formatted(coachDisplayName);
    return baseTemplate("Desvinculado del gimnasio — OssFlow", body);
}
```

- [ ] **Step 6: Compilar y ejecutar tests existentes de email**

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/microsoft-25.jdk/Contents/Home \
  mvn test -pl . -Dtest="EmailServiceTest,EmailOutboxServiceTest" 2>&1 | tail -10
```

Esperado: todos en verde.

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/ossflow/identity/auth/application/EmailService.java \
        src/main/java/com/ossflow/identity/auth/application/EmailOutboxService.java
git commit -m "feat(email): rediseño HTML emails con identidad visual OssFlow + plantillas coaching"
```

---

## Task 8: Migraciones V257-V259 (tablas coaching)

**Files:**
- Create: `src/main/resources/db/migration/V257__create_coach_invitation.sql`
- Create: `src/main/resources/db/migration/V258__create_coach_athlete.sql`
- Create: `src/main/resources/db/migration/V259__create_coaching_notification.sql`

- [ ] **Step 1: Crear V257**

```sql
-- V257__create_coach_invitation.sql
CREATE TABLE coach_invitation (
    id          BIGSERIAL    PRIMARY KEY,
    coach_id    BIGINT       NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    code        VARCHAR(6)   NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    used_count  INTEGER      NOT NULL DEFAULT 0,
    expires_at  TIMESTAMPTZ  NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_invitation_status CHECK (status IN ('PENDING','EXPIRED','REVOKED'))
);

CREATE UNIQUE INDEX ux_coach_invitation_active
    ON coach_invitation(coach_id) WHERE status = 'PENDING';

CREATE INDEX ix_coach_invitation_code
    ON coach_invitation(code);
```

- [ ] **Step 2: Crear V258**

```sql
-- V258__create_coach_athlete.sql
CREATE TABLE coach_athlete (
    id              BIGSERIAL    PRIMARY KEY,
    coach_id        BIGINT       NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    athlete_id      BIGINT       NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    invitation_id   BIGINT       REFERENCES coach_invitation(id) ON DELETE SET NULL,
    linked_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_coach_athlete UNIQUE (coach_id, athlete_id)
);

CREATE INDEX ix_coach_athlete_coach_id   ON coach_athlete(coach_id);
CREATE INDEX ix_coach_athlete_athlete_id ON coach_athlete(athlete_id);
```

- [ ] **Step 3: Crear V259**

```sql
-- V259__create_coaching_notification.sql
CREATE TABLE coaching_notification (
    id                   BIGSERIAL    PRIMARY KEY,
    recipient_account_id BIGINT       NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    type                 VARCHAR(40)  NOT NULL,
    payload              TEXT,
    read                 BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_coaching_notification_recipient
    ON coaching_notification(recipient_account_id, read, created_at DESC);
```

- [ ] **Step 4: Aplicar y verificar**

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/microsoft-25.jdk/Contents/Home \
  mvn flyway:migrate -Dspring-boot.run.profiles=dev 2>&1 | tail -5
```

Esperado: `Successfully applied 3 migrations` — versiones V257, V258, V259.

- [ ] **Step 5: Commit**

```bash
git add src/main/resources/db/migration/V257__create_coach_invitation.sql \
        src/main/resources/db/migration/V258__create_coach_athlete.sql \
        src/main/resources/db/migration/V259__create_coaching_notification.sql
git commit -m "feat(coaching): V257-V259 tablas coach_invitation, coach_athlete, coaching_notification"
```

---

## Task 9: Dominio `coaching/invitation`

**Files:**
- Create: `src/main/java/com/ossflow/coaching/invitation/domain/InvitationStatus.java`
- Create: `src/main/java/com/ossflow/coaching/invitation/domain/CoachInvitation.java`
- Create: `src/main/java/com/ossflow/coaching/invitation/application/port/CoachInvitationRepositoryPort.java`
- Create: `src/main/java/com/ossflow/coaching/invitation/application/CoachInvitationService.java`
- Create: `src/test/java/com/ossflow/coaching/invitation/application/CoachInvitationServiceTest.java`

- [ ] **Step 1: Escribir el test antes de implementar**

```java
// CoachInvitationServiceTest.java
package com.ossflow.coaching.invitation.application;

import com.ossflow.coaching.invitation.domain.CoachInvitation;
import com.ossflow.coaching.invitation.domain.InvitationStatus;
import com.ossflow.coaching.invitation.application.port.CoachInvitationRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoachInvitationServiceTest {

    @Mock CoachInvitationRepositoryPort repo;
    @InjectMocks CoachInvitationService service;

    @Test
    void generate_creates_new_code_and_revokes_previous() {
        CoachInvitation existing = CoachInvitation.builder()
                .id(1L).coachId(10L).code("ABC123")
                .status(InvitationStatus.PENDING).usedCount(0)
                .expiresAt(Instant.now().plusSeconds(3600))
                .createdAt(Instant.now()).build();

        when(repo.findActiveByCoachId(10L)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CoachInvitation result = service.generate(10L);

        assertThat(result.status()).isEqualTo(InvitationStatus.PENDING);
        assertThat(result.code()).hasSize(6);
        assertThat(result.coachId()).isEqualTo(10L);
        // verifica que el anterior fue revocado
        verify(repo).save(argThat(inv -> inv.status() == InvitationStatus.REVOKED && inv.id().equals(1L)));
        verify(repo).save(argThat(inv -> inv.status() == InvitationStatus.PENDING && inv.id() == null));
    }

    @Test
    void generate_without_previous_creates_new_code() {
        when(repo.findActiveByCoachId(10L)).thenReturn(Optional.empty());
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CoachInvitation result = service.generate(10L);

        assertThat(result.code()).hasSize(6);
        verify(repo, times(1)).save(any());
    }

    @Test
    void revoke_sets_status_revoked() {
        CoachInvitation existing = CoachInvitation.builder()
                .id(1L).coachId(10L).code("ABC123")
                .status(InvitationStatus.PENDING).usedCount(0)
                .expiresAt(Instant.now().plusSeconds(3600))
                .createdAt(Instant.now()).build();

        when(repo.findActiveByCoachId(10L)).thenReturn(Optional.of(existing));

        service.revoke(10L);

        verify(repo).save(argThat(inv -> inv.status() == InvitationStatus.REVOKED));
    }
}
```

- [ ] **Step 2: Verificar que el test falla (clases no existen aún)**

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/microsoft-25.jdk/Contents/Home \
  mvn test -Dtest="CoachInvitationServiceTest" 2>&1 | tail -5
```

Esperado: error de compilación — clases no encontradas.

- [ ] **Step 3: Crear `InvitationStatus`**

```java
package com.ossflow.coaching.invitation.domain;

public enum InvitationStatus { PENDING, EXPIRED, REVOKED }
```

- [ ] **Step 4: Crear `CoachInvitation`**

```java
package com.ossflow.coaching.invitation.domain;

import lombok.Builder;
import java.time.Instant;

@Builder(toBuilder = true)
public record CoachInvitation(
        Long id,
        Long coachId,
        String code,
        InvitationStatus status,
        int usedCount,
        Instant expiresAt,
        Instant createdAt
) {}
```

- [ ] **Step 5: Crear `CoachInvitationRepositoryPort`**

```java
package com.ossflow.coaching.invitation.application.port;

import com.ossflow.coaching.invitation.domain.CoachInvitation;
import java.util.Optional;

public interface CoachInvitationRepositoryPort {
    CoachInvitation save(CoachInvitation invitation);
    Optional<CoachInvitation> findActiveByCoachId(Long coachId);
    Optional<CoachInvitation> findByCode(String code);
}
```

- [ ] **Step 6: Crear `CoachInvitationService`**

```java
package com.ossflow.coaching.invitation.application;

import com.ossflow.coaching.invitation.application.port.CoachInvitationRepositoryPort;
import com.ossflow.coaching.invitation.domain.CoachInvitation;
import com.ossflow.coaching.invitation.domain.InvitationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CoachInvitationService {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final long TTL_HOURS = 48;
    private final SecureRandom rng = new SecureRandom();

    private final CoachInvitationRepositoryPort repo;

    public CoachInvitation generate(Long coachId) {
        repo.findActiveByCoachId(coachId).ifPresent(existing ->
            repo.save(existing.toBuilder().status(InvitationStatus.REVOKED).build())
        );
        return repo.save(CoachInvitation.builder()
                .coachId(coachId)
                .code(generateCode())
                .status(InvitationStatus.PENDING)
                .usedCount(0)
                .expiresAt(Instant.now().plusSeconds(TTL_HOURS * 3600))
                .createdAt(Instant.now())
                .build());
    }

    public CoachInvitation getActive(Long coachId) {
        return repo.findActiveByCoachId(coachId)
                .filter(inv -> inv.expiresAt().isAfter(Instant.now()))
                .orElse(null);
    }

    public void revoke(Long coachId) {
        repo.findActiveByCoachId(coachId).ifPresent(inv ->
            repo.save(inv.toBuilder().status(InvitationStatus.REVOKED).build())
        );
    }

    public CoachInvitation validateCode(String code) {
        return repo.findByCode(code)
                .filter(inv -> inv.status() == InvitationStatus.PENDING)
                .filter(inv -> inv.expiresAt().isAfter(Instant.now()))
                .orElse(null);
    }

    public CoachInvitation incrementUsedCount(CoachInvitation invitation) {
        return repo.save(invitation.toBuilder()
                .usedCount(invitation.usedCount() + 1)
                .build());
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARS.charAt(rng.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
```

- [ ] **Step 7: Ejecutar tests**

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/microsoft-25.jdk/Contents/Home \
  mvn test -Dtest="CoachInvitationServiceTest" 2>&1 | tail -5
```

Esperado: `BUILD SUCCESS`, 3 tests en verde.

- [ ] **Step 8: Commit**

```bash
git add src/main/java/com/ossflow/coaching/invitation/ \
        src/test/java/com/ossflow/coaching/invitation/
git commit -m "feat(coaching): dominio invitation — CoachInvitation, InvitationStatus, CoachInvitationService"
```

---

## Task 10: Infraestructura de persistencia `coaching/invitation`

**Files:**
- Create: `src/main/java/com/ossflow/coaching/invitation/infrastructure/persistence/CoachInvitationEntity.java`
- Create: `src/main/java/com/ossflow/coaching/invitation/infrastructure/persistence/CoachInvitationJpaRepository.java`
- Create: `src/main/java/com/ossflow/coaching/invitation/infrastructure/persistence/CoachInvitationPersistenceMapper.java`
- Create: `src/main/java/com/ossflow/coaching/invitation/infrastructure/persistence/CoachInvitationPersistenceAdapter.java`

- [ ] **Step 1: Crear `CoachInvitationEntity`**

```java
package com.ossflow.coaching.invitation.infrastructure.persistence;

import com.ossflow.coaching.invitation.domain.InvitationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "coach_invitation")
@Getter @Setter @NoArgsConstructor
public class CoachInvitationEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coach_id", nullable = false)
    private Long coachId;

    @Column(name = "code", nullable = false, length = 6)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InvitationStatus status;

    @Column(name = "used_count", nullable = false)
    private int usedCount;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
```

- [ ] **Step 2: Crear `CoachInvitationJpaRepository`**

```java
package com.ossflow.coaching.invitation.infrastructure.persistence;

import com.ossflow.coaching.invitation.domain.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoachInvitationJpaRepository extends JpaRepository<CoachInvitationEntity, Long> {
    Optional<CoachInvitationEntity> findByCoachIdAndStatus(Long coachId, InvitationStatus status);
    Optional<CoachInvitationEntity> findByCode(String code);
}
```

- [ ] **Step 3: Crear `CoachInvitationPersistenceMapper`**

```java
package com.ossflow.coaching.invitation.infrastructure.persistence;

import com.ossflow.coaching.invitation.domain.CoachInvitation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CoachInvitationPersistenceMapper {
    CoachInvitation toDomain(CoachInvitationEntity entity);
    CoachInvitationEntity toEntity(CoachInvitation domain);
}
```

- [ ] **Step 4: Crear `CoachInvitationPersistenceAdapter`**

```java
package com.ossflow.coaching.invitation.infrastructure.persistence;

import com.ossflow.coaching.invitation.application.port.CoachInvitationRepositoryPort;
import com.ossflow.coaching.invitation.domain.CoachInvitation;
import com.ossflow.coaching.invitation.domain.InvitationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CoachInvitationPersistenceAdapter implements CoachInvitationRepositoryPort {

    private final CoachInvitationJpaRepository jpa;
    private final CoachInvitationPersistenceMapper mapper;

    @Override
    public CoachInvitation save(CoachInvitation invitation) {
        return mapper.toDomain(jpa.save(mapper.toEntity(invitation)));
    }

    @Override
    public Optional<CoachInvitation> findActiveByCoachId(Long coachId) {
        return jpa.findByCoachIdAndStatus(coachId, InvitationStatus.PENDING)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<CoachInvitation> findByCode(String code) {
        return jpa.findByCode(code).map(mapper::toDomain);
    }
}
```

- [ ] **Step 5: Compilar**

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/microsoft-25.jdk/Contents/Home \
  mvn compile -DskipTests 2>&1 | tail -5
```

Esperado: `BUILD SUCCESS`.

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/ossflow/coaching/invitation/infrastructure/
git commit -m "feat(coaching): persistencia invitation — entity, repo, mapper, adapter"
```

---

## Task 11: Dominio y persistencia `coaching/relationship`

**Files:**
- Create: `src/main/java/com/ossflow/coaching/relationship/domain/CoachAthleteRelationship.java`
- Create: `src/main/java/com/ossflow/coaching/relationship/application/port/CoachAthleteRepositoryPort.java`
- Create: `src/main/java/com/ossflow/coaching/relationship/application/CoachAthleteService.java`
- Create: `src/main/java/com/ossflow/coaching/relationship/infrastructure/persistence/CoachAthleteEntity.java`
- Create: `src/main/java/com/ossflow/coaching/relationship/infrastructure/persistence/CoachAthleteJpaRepository.java`
- Create: `src/main/java/com/ossflow/coaching/relationship/infrastructure/persistence/CoachAthletePersistenceMapper.java`
- Create: `src/main/java/com/ossflow/coaching/relationship/infrastructure/persistence/CoachAthletePersistenceAdapter.java`
- Create: `src/test/java/com/ossflow/coaching/relationship/application/CoachAthleteServiceTest.java`

- [ ] **Step 1: Escribir el test**

```java
package com.ossflow.coaching.relationship.application;

import com.ossflow.coaching.relationship.application.port.CoachAthleteRepositoryPort;
import com.ossflow.coaching.relationship.domain.CoachAthleteRelationship;
import com.ossflow.shared.exception.OssFlowException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoachAthleteServiceTest {

    @Mock CoachAthleteRepositoryPort repo;
    @InjectMocks CoachAthleteService service;

    @Test
    void link_throws_when_already_linked() {
        when(repo.findByCoachIdAndAthleteId(1L, 2L))
                .thenReturn(Optional.of(CoachAthleteRelationship.builder()
                        .id(1L).coachId(1L).athleteId(2L)
                        .linkedAt(Instant.now()).build()));

        assertThatThrownBy(() -> service.link(1L, 2L, null))
                .isInstanceOf(OssFlowException.class)
                .hasMessageContaining("ALREADY_LINKED");
    }

    @Test
    void link_creates_relationship_when_not_exists() {
        when(repo.findByCoachIdAndAthleteId(1L, 2L)).thenReturn(Optional.empty());
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.link(1L, 2L, 5L);

        verify(repo).save(argThat(r ->
                r.coachId().equals(1L) && r.athleteId().equals(2L) && r.invitationId().equals(5L)));
    }

    @Test
    void unlinkByCoach_deletes_relationship() {
        service.unlinkByCoach(1L, 2L);
        verify(repo).deleteByCoachIdAndAthleteId(1L, 2L);
    }

    @Test
    void unlinkByAthlete_deletes_relationship() {
        service.unlinkByAthlete(2L, 1L);
        verify(repo).deleteByCoachIdAndAthleteId(1L, 2L);
    }
}
```

- [ ] **Step 2: Crear `CoachAthleteRelationship`**

```java
package com.ossflow.coaching.relationship.domain;

import lombok.Builder;
import java.time.Instant;

@Builder(toBuilder = true)
public record CoachAthleteRelationship(
        Long id,
        Long coachId,
        Long athleteId,
        Long invitationId,
        Instant linkedAt
) {}
```

- [ ] **Step 3: Crear `CoachAthleteRepositoryPort`**

```java
package com.ossflow.coaching.relationship.application.port;

import com.ossflow.coaching.relationship.domain.CoachAthleteRelationship;
import java.util.List;
import java.util.Optional;

public interface CoachAthleteRepositoryPort {
    CoachAthleteRelationship save(CoachAthleteRelationship relationship);
    Optional<CoachAthleteRelationship> findByCoachIdAndAthleteId(Long coachId, Long athleteId);
    List<CoachAthleteRelationship> findAllByCoachId(Long coachId);
    List<CoachAthleteRelationship> findAllByAthleteId(Long athleteId);
    void deleteByCoachIdAndAthleteId(Long coachId, Long athleteId);
    boolean existsByCoachIdAndAthleteId(Long coachId, Long athleteId);
}
```

- [ ] **Step 4: Crear `CoachAthleteService`**

```java
package com.ossflow.coaching.relationship.application;

import com.ossflow.coaching.relationship.application.port.CoachAthleteRepositoryPort;
import com.ossflow.coaching.relationship.domain.CoachAthleteRelationship;
import com.ossflow.shared.exception.OssFlowException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoachAthleteService {

    private final CoachAthleteRepositoryPort repo;

    public CoachAthleteRelationship link(Long coachId, Long athleteId, Long invitationId) {
        if (repo.findByCoachIdAndAthleteId(coachId, athleteId).isPresent()) {
            throw new OssFlowException("ALREADY_LINKED", "El atleta ya está vinculado a este maestro");
        }
        return repo.save(CoachAthleteRelationship.builder()
                .coachId(coachId)
                .athleteId(athleteId)
                .invitationId(invitationId)
                .linkedAt(Instant.now())
                .build());
    }

    public void unlinkByCoach(Long coachId, Long athleteId) {
        repo.deleteByCoachIdAndAthleteId(coachId, athleteId);
    }

    public void unlinkByAthlete(Long athleteId, Long coachId) {
        repo.deleteByCoachIdAndAthleteId(coachId, athleteId);
    }

    public List<CoachAthleteRelationship> getAthletes(Long coachId) {
        return repo.findAllByCoachId(coachId);
    }

    public List<CoachAthleteRelationship> getCoaches(Long athleteId) {
        return repo.findAllByAthleteId(athleteId);
    }

    public boolean isLinked(Long coachId, Long athleteId) {
        return repo.existsByCoachIdAndAthleteId(coachId, athleteId);
    }
}
```

- [ ] **Step 5: Crear `CoachAthleteEntity`**

```java
package com.ossflow.coaching.relationship.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "coach_athlete")
@Getter @Setter @NoArgsConstructor
public class CoachAthleteEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coach_id", nullable = false)
    private Long coachId;

    @Column(name = "athlete_id", nullable = false)
    private Long athleteId;

    @Column(name = "invitation_id")
    private Long invitationId;

    @Column(name = "linked_at", nullable = false)
    private Instant linkedAt;
}
```

- [ ] **Step 6: Crear `CoachAthleteJpaRepository`**

```java
package com.ossflow.coaching.relationship.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CoachAthleteJpaRepository extends JpaRepository<CoachAthleteEntity, Long> {
    Optional<CoachAthleteEntity> findByCoachIdAndAthleteId(Long coachId, Long athleteId);
    List<CoachAthleteEntity> findAllByCoachId(Long coachId);
    List<CoachAthleteEntity> findAllByAthleteId(Long athleteId);
    void deleteByCoachIdAndAthleteId(Long coachId, Long athleteId);
    boolean existsByCoachIdAndAthleteId(Long coachId, Long athleteId);
}
```

- [ ] **Step 7: Crear mapper y adapter** (igual que Task 10 pasos 3-4, adaptado)

```java
// CoachAthletePersistenceMapper.java
@Mapper(componentModel = "spring")
public interface CoachAthletePersistenceMapper {
    CoachAthleteRelationship toDomain(CoachAthleteEntity entity);
    CoachAthleteEntity toEntity(CoachAthleteRelationship domain);
}

// CoachAthletePersistenceAdapter.java
@Component @RequiredArgsConstructor
public class CoachAthletePersistenceAdapter implements CoachAthleteRepositoryPort {
    private final CoachAthleteJpaRepository jpa;
    private final CoachAthletePersistenceMapper mapper;

    @Override public CoachAthleteRelationship save(CoachAthleteRelationship r) {
        return mapper.toDomain(jpa.save(mapper.toEntity(r)));
    }
    @Override public Optional<CoachAthleteRelationship> findByCoachIdAndAthleteId(Long c, Long a) {
        return jpa.findByCoachIdAndAthleteId(c, a).map(mapper::toDomain);
    }
    @Override public List<CoachAthleteRelationship> findAllByCoachId(Long coachId) {
        return jpa.findAllByCoachId(coachId).stream().map(mapper::toDomain).toList();
    }
    @Override public List<CoachAthleteRelationship> findAllByAthleteId(Long athleteId) {
        return jpa.findAllByAthleteId(athleteId).stream().map(mapper::toDomain).toList();
    }
    @Override public void deleteByCoachIdAndAthleteId(Long coachId, Long athleteId) {
        jpa.deleteByCoachIdAndAthleteId(coachId, athleteId);
    }
    @Override public boolean existsByCoachIdAndAthleteId(Long coachId, Long athleteId) {
        return jpa.existsByCoachIdAndAthleteId(coachId, athleteId);
    }
}
```

- [ ] **Step 8: Ejecutar tests**

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/microsoft-25.jdk/Contents/Home \
  mvn test -Dtest="CoachAthleteServiceTest" 2>&1 | tail -5
```

Esperado: `BUILD SUCCESS`, 4 tests en verde.

- [ ] **Step 9: Commit**

```bash
git add src/main/java/com/ossflow/coaching/relationship/ \
        src/test/java/com/ossflow/coaching/relationship/
git commit -m "feat(coaching): dominio y persistencia relationship — CoachAthleteRelationship + service"
```

---

## Task 12: `AthleteProfileComposer` + dominio `coaching/notification`

**Files:**
- Create: `src/main/java/com/ossflow/coaching/relationship/application/AthleteProfileComposer.java`
- Create: `src/main/java/com/ossflow/coaching/notification/domain/NotificationType.java`
- Create: `src/main/java/com/ossflow/coaching/notification/domain/CoachingNotification.java`
- Create: `src/main/java/com/ossflow/coaching/notification/application/port/CoachingNotificationRepositoryPort.java`
- Create: `src/main/java/com/ossflow/coaching/notification/application/CoachingNotificationService.java`

- [ ] **Step 1: Crear `NotificationType` y `CoachingNotification`**

```java
// NotificationType.java
package com.ossflow.coaching.notification.domain;

public enum NotificationType {
    ATHLETE_JOINED,
    ATHLETE_LEFT,
    COACH_REMOVED_YOU
}
```

```java
// CoachingNotification.java
package com.ossflow.coaching.notification.domain;

import lombok.Builder;
import java.time.Instant;

@Builder(toBuilder = true)
public record CoachingNotification(
        Long id,
        Long recipientAccountId,
        NotificationType type,
        String payload,
        boolean read,
        Instant createdAt
) {}
```

- [ ] **Step 2: Crear `CoachingNotificationRepositoryPort`**

```java
package com.ossflow.coaching.notification.application.port;

import com.ossflow.coaching.notification.domain.CoachingNotification;
import java.util.List;

public interface CoachingNotificationRepositoryPort {
    CoachingNotification save(CoachingNotification notification);
    List<CoachingNotification> findUnreadByRecipient(Long recipientAccountId);
    void markAllReadByRecipient(Long recipientAccountId);
}
```

- [ ] **Step 3: Crear `CoachingNotificationService`**

```java
package com.ossflow.coaching.notification.application;

import com.ossflow.coaching.notification.application.port.CoachingNotificationRepositoryPort;
import com.ossflow.coaching.notification.domain.CoachingNotification;
import com.ossflow.coaching.notification.domain.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoachingNotificationService {

    private final CoachingNotificationRepositoryPort repo;

    public void notifyAthleteJoined(Long coachId, String athleteName) {
        repo.save(build(coachId, NotificationType.ATHLETE_JOINED,
                "{\"athleteName\":\"" + athleteName + "\"}"));
    }

    public void notifyAthleteLeft(Long coachId, String athleteName) {
        repo.save(build(coachId, NotificationType.ATHLETE_LEFT,
                "{\"athleteName\":\"" + athleteName + "\"}"));
    }

    public void notifyCoachRemovedYou(Long athleteId, String coachName) {
        repo.save(build(athleteId, NotificationType.COACH_REMOVED_YOU,
                "{\"coachName\":\"" + coachName + "\"}"));
    }

    public List<CoachingNotification> getUnread(Long accountId) {
        return repo.findUnreadByRecipient(accountId);
    }

    public void markAllRead(Long accountId) {
        repo.markAllReadByRecipient(accountId);
    }

    private CoachingNotification build(Long recipientId, NotificationType type, String payload) {
        return CoachingNotification.builder()
                .recipientAccountId(recipientId)
                .type(type)
                .payload(payload)
                .read(false)
                .createdAt(Instant.now())
                .build();
    }
}
```

- [ ] **Step 4: Crear `AthleteProfileComposer`**

El composer inyecta los repositorios existentes. Verifica primero que existe la relación activa.

```java
package com.ossflow.coaching.relationship.application;

import com.ossflow.coaching.relationship.application.port.CoachAthleteRepositoryPort;
import com.ossflow.identity.injury.application.port.InjuryRepositoryPort;
import com.ossflow.identity.profile.application.port.UserProfileRepositoryPort;
import com.ossflow.journal.competitionlog.application.port.CompetitionLogRepositoryPort;
import com.ossflow.journal.trainingsession.application.port.TrainingSessionRepositoryPort;
import com.ossflow.coaching.relationship.infrastructure.web.dto.AthleteSummaryResponse;
import com.ossflow.identity.injury.domain.InjuryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AthleteProfileComposer {

    private final CoachAthleteRepositoryPort coachAthleteRepo;
    private final UserProfileRepositoryPort profileRepo;
    private final InjuryRepositoryPort injuryRepo;
    private final CompetitionLogRepositoryPort competitionRepo;
    private final TrainingSessionRepositoryPort trainingRepo;

    public AthleteSummaryResponse compose(Long coachId, Long athleteId) {
        if (!coachAthleteRepo.existsByCoachIdAndAthleteId(coachId, athleteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No vinculado");
        }

        var profile = profileRepo.findByOwnerId(athleteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var injuries = injuryRepo.findAllByOwnerId(athleteId).stream()
                .filter(i -> i.status() == InjuryStatus.ACTIVE)
                .toList();

        var competitions = competitionRepo.findAll(athleteId,
                PageRequest.of(0, 5, Sort.by("competitionDate").descending())).getContent();

        var sessions = trainingRepo.findAll(athleteId,
                PageRequest.of(0, 10, Sort.by("sessionDate").descending())).getContent();

        var lastSession = sessions.isEmpty() ? null : sessions.get(0);

        return AthleteSummaryResponse.from(profile, injuries, competitions, lastSession);
    }
}
```

- [ ] **Step 5: Crear `AthleteSummaryResponse` DTO**

```java
package com.ossflow.coaching.relationship.infrastructure.web.dto;

import com.ossflow.identity.profile.domain.UserProfile;
import com.ossflow.identity.injury.domain.Injury;
import com.ossflow.journal.competitionlog.domain.CompetitionLog;
import com.ossflow.journal.trainingsession.domain.TrainingSession;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public record AthleteSummaryResponse(
        Long athleteId,
        String displayName,
        String currentBelt,
        Long daysInBelt,
        String academy,
        List<ActiveInjuryItem> activeInjuries,
        List<CompetitionItem> recentCompetitions,
        String lastSessionDate,
        int daysSinceLastSession
) {
    public record ActiveInjuryItem(String bodyPart, String severity) {}
    public record CompetitionItem(String name, String date, String result) {}

    public static AthleteSummaryResponse from(UserProfile profile,
                                               List<Injury> injuries,
                                               List<CompetitionLog> competitions,
                                               TrainingSession lastSession) {
        long daysInBelt = profile.beltSince() != null
                ? ChronoUnit.DAYS.between(profile.beltSince(), LocalDate.now()) : 0;

        int daysSince = lastSession != null
                ? (int) ChronoUnit.DAYS.between(lastSession.sessionDate(), LocalDate.now()) : -1;

        return new AthleteSummaryResponse(
                profile.ownerId(),
                profile.displayName(),
                profile.currentBelt(),
                daysInBelt,
                profile.academy(),
                injuries.stream().map(i -> new ActiveInjuryItem(
                        i.bodyPart(), i.severity().name())).toList(),
                competitions.stream().map(c -> new CompetitionItem(
                        c.tournamentName(),
                        c.competitionDate().toString(),
                        c.result() != null ? c.result().name() : null)).toList(),
                lastSession != null ? lastSession.sessionDate().toString() : null,
                daysSince
        );
    }
}
```

- [ ] **Step 6: Compilar**

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/microsoft-25.jdk/Contents/Home \
  mvn compile -DskipTests 2>&1 | tail -5
```

Esperado: `BUILD SUCCESS`. Si hay errores de nombres de campos en los dominios existentes (bodyPart, tournamentName, etc.), ajustar según los records reales del proyecto.

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/ossflow/coaching/
git commit -m "feat(coaching): AthleteProfileComposer, dominio notification, CoachingNotificationService"
```

---

## Task 13: Infraestructura de persistencia `coaching/notification` + controllers

**Files:**
- Create: `src/main/java/com/ossflow/coaching/notification/infrastructure/persistence/` (4 ficheros)
- Create: `src/main/java/com/ossflow/coaching/invitation/infrastructure/web/CoachInvitationController.java`
- Create: `src/main/java/com/ossflow/coaching/relationship/infrastructure/web/CoachAthleteController.java`
- Create: `src/main/java/com/ossflow/coaching/notification/infrastructure/web/CoachingNotificationController.java`

- [ ] **Step 1: Crear infraestructura de persistencia de notificaciones**

```java
// CoachingNotificationEntity.java
@Entity @Table(name = "coaching_notification")
@Getter @Setter @NoArgsConstructor
public class CoachingNotificationEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "recipient_account_id", nullable = false) private Long recipientAccountId;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 40) private NotificationType type;
    @Column(name = "payload") private String payload;
    @Column(name = "read", nullable = false) private boolean read;
    @CreationTimestamp @Column(name = "created_at", updatable = false) private Instant createdAt;
}

// CoachingNotificationJpaRepository.java
public interface CoachingNotificationJpaRepository extends JpaRepository<CoachingNotificationEntity, Long> {
    List<CoachingNotificationEntity> findByRecipientAccountIdAndReadFalseOrderByCreatedAtDesc(Long id);
    @Modifying @Query("UPDATE CoachingNotificationEntity n SET n.read = true WHERE n.recipientAccountId = :id AND n.read = false")
    void markAllReadByRecipientAccountId(@Param("id") Long id);
}

// CoachingNotificationPersistenceMapper.java
@Mapper(componentModel = "spring")
public interface CoachingNotificationPersistenceMapper {
    CoachingNotification toDomain(CoachingNotificationEntity entity);
    CoachingNotificationEntity toEntity(CoachingNotification domain);
}

// CoachingNotificationPersistenceAdapter.java
@Component @RequiredArgsConstructor
public class CoachingNotificationPersistenceAdapter implements CoachingNotificationRepositoryPort {
    private final CoachingNotificationJpaRepository jpa;
    private final CoachingNotificationPersistenceMapper mapper;

    @Override public CoachingNotification save(CoachingNotification n) {
        return mapper.toDomain(jpa.save(mapper.toEntity(n)));
    }
    @Override public List<CoachingNotification> findUnreadByRecipient(Long id) {
        return jpa.findByRecipientAccountIdAndReadFalseOrderByCreatedAtDesc(id)
                .stream().map(mapper::toDomain).toList();
    }
    @Override @Transactional public void markAllReadByRecipient(Long id) {
        jpa.markAllReadByRecipientAccountId(id);
    }
}
```

- [ ] **Step 2: Crear `CoachInvitationController`**

```java
@RestController
@RequestMapping("/api/v1/coaching/invitations")
@RequiredArgsConstructor
public class CoachInvitationController {

    private final CoachInvitationService invitationService;

    @PostMapping
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<InvitationCodeResponse> generate(
            @AuthenticationPrincipal AccountPrincipal principal) {
        CoachInvitation inv = invitationService.generate(principal.id());
        return ResponseEntity.ok(InvitationCodeResponse.from(inv));
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<InvitationCodeResponse> getActive(
            @AuthenticationPrincipal AccountPrincipal principal) {
        CoachInvitation inv = invitationService.getActive(principal.id());
        if (inv == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(InvitationCodeResponse.from(inv));
    }

    @DeleteMapping("/active")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revoke(@AuthenticationPrincipal AccountPrincipal principal) {
        invitationService.revoke(principal.id());
    }
}
```

```java
// InvitationCodeResponse.java
public record InvitationCodeResponse(String code, Instant expiresAt, int usedCount) {
    public static InvitationCodeResponse from(CoachInvitation inv) {
        return new InvitationCodeResponse(inv.code(), inv.expiresAt(), inv.usedCount());
    }
}
```

- [ ] **Step 3: Crear `CoachAthleteController`**

```java
@RestController
@RequestMapping("/api/v1/coaching")
@RequiredArgsConstructor
public class CoachAthleteController {

    private final CoachAthleteService coachAthleteService;
    private final CoachInvitationService invitationService;
    private final AthleteProfileComposer composer;
    private final CoachingNotificationService notificationService;
    private final EmailOutboxService emailOutboxService;
    private final EmailService emailService;
    private final UserProfileRepositoryPort profileRepo;
    private final AccountRepositoryPort accountRepo;

    @PostMapping("/memberships/redeem")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void redeem(@AuthenticationPrincipal AccountPrincipal principal,
                       @RequestBody @Valid RedeemInvitationRequest request) {
        CoachInvitation inv = invitationService.validateCode(request.code());
        if (inv == null) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_CODE");

        coachAthleteService.link(inv.coachId(), principal.id(), inv.id());
        invitationService.incrementUsedCount(inv);

        // Notificación in-app + email al maestro
        var athleteProfile = profileRepo.findByOwnerId(principal.id()).orElse(null);
        String athleteName = athleteProfile != null ? athleteProfile.displayName() : "Un atleta";
        notificationService.notifyAthleteJoined(inv.coachId(), athleteName);

        accountRepo.findById(inv.coachId()).ifPresent(coachAccount ->
            emailOutboxService.enqueueCoachingEmail(
                inv.coachId(), coachAccount.email(),
                emailService.athleteJoinedSubject(),
                emailService.athleteJoinedBody(athleteName))
        );
    }

    @DeleteMapping("/memberships/{athleteId}")
    @PreAuthorize("hasRole('COACH')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAthlete(@AuthenticationPrincipal AccountPrincipal principal,
                               @PathVariable Long athleteId) {
        coachAthleteService.unlinkByCoach(principal.id(), athleteId);

        var athleteProfile = profileRepo.findByOwnerId(athleteId).orElse(null);
        String athleteName = athleteProfile != null ? athleteProfile.displayName() : "El atleta";
        var coachProfile = profileRepo.findByOwnerId(principal.id()).orElse(null);
        String coachName = coachProfile != null ? coachProfile.displayName() : "Tu maestro";

        notificationService.notifyCoachRemovedYou(athleteId, coachName);
        accountRepo.findById(athleteId).ifPresent(athleteAccount ->
            emailOutboxService.enqueueCoachingEmail(
                athleteId, athleteAccount.email(),
                emailService.coachRemovedYouSubject(),
                emailService.coachRemovedYouBody(coachName))
        );

        notificationService.notifyAthleteLeft(principal.id(), athleteName);
    }

    @DeleteMapping("/memberships/leave/{coachId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveCoach(@AuthenticationPrincipal AccountPrincipal principal,
                            @PathVariable Long coachId) {
        coachAthleteService.unlinkByAthlete(principal.id(), coachId);

        var athleteProfile = profileRepo.findByOwnerId(principal.id()).orElse(null);
        String athleteName = athleteProfile != null ? athleteProfile.displayName() : "Un atleta";

        notificationService.notifyAthleteLeft(coachId, athleteName);
        accountRepo.findById(coachId).ifPresent(coachAccount ->
            emailOutboxService.enqueueCoachingEmail(
                coachId, coachAccount.email(),
                emailService.athleteLeftSubject(),
                emailService.athleteLeftBody(athleteName))
        );
    }

    @GetMapping("/athletes")
    @PreAuthorize("hasRole('COACH')")
    public List<AthleteListItemResponse> getAthletes(@AuthenticationPrincipal AccountPrincipal principal) {
        return coachAthleteService.getAthletes(principal.id()).stream()
                .map(r -> {
                    var profile = profileRepo.findByOwnerId(r.athleteId()).orElse(null);
                    return AthleteListItemResponse.from(r, profile);
                }).toList();
    }

    @GetMapping("/athletes/{athleteId}/summary")
    @PreAuthorize("hasRole('COACH')")
    public AthleteSummaryResponse getAthleteSummary(
            @AuthenticationPrincipal AccountPrincipal principal,
            @PathVariable Long athleteId) {
        return composer.compose(principal.id(), athleteId);
    }

    @GetMapping("/coaches")
    public List<CoachListItemResponse> getCoaches(@AuthenticationPrincipal AccountPrincipal principal) {
        return coachAthleteService.getCoaches(principal.id()).stream()
                .map(r -> {
                    var profile = profileRepo.findByOwnerId(r.coachId()).orElse(null);
                    return CoachListItemResponse.from(r, profile);
                }).toList();
    }
}
```

- [ ] **Step 4: Crear DTOs restantes**

```java
// RedeemInvitationRequest.java
public record RedeemInvitationRequest(@NotBlank @Size(min=6, max=6) String code) {}

// AthleteListItemResponse.java
public record AthleteListItemResponse(Long athleteId, String displayName, String currentBelt, String lastSeen) {
    public static AthleteListItemResponse from(CoachAthleteRelationship r, UserProfile profile) {
        return new AthleteListItemResponse(
            r.athleteId(),
            profile != null ? profile.displayName() : "—",
            profile != null ? profile.currentBelt() : "—",
            r.linkedAt().toString()
        );
    }
}

// CoachListItemResponse.java
public record CoachListItemResponse(Long coachId, String displayName, String academy) {
    public static CoachListItemResponse from(CoachAthleteRelationship r, UserProfile profile) {
        return new CoachListItemResponse(
            r.coachId(),
            profile != null ? profile.displayName() : "—",
            profile != null ? profile.academy() : null
        );
    }
}
```

- [ ] **Step 5: Crear `CoachingNotificationController`**

```java
@RestController
@RequestMapping("/api/v1/coaching/notifications")
@RequiredArgsConstructor
public class CoachingNotificationController {

    private final CoachingNotificationService notificationService;

    @GetMapping
    public List<NotificationResponse> getUnread(@AuthenticationPrincipal AccountPrincipal principal) {
        return notificationService.getUnread(principal.id())
                .stream().map(NotificationResponse::from).toList();
    }

    @PatchMapping("/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAllRead(@AuthenticationPrincipal AccountPrincipal principal) {
        notificationService.markAllRead(principal.id());
    }
}
```

```java
// NotificationResponse.java
public record NotificationResponse(Long id, String type, String payload, Instant createdAt) {
    public static NotificationResponse from(CoachingNotification n) {
        return new NotificationResponse(n.id(), n.type().name(), n.payload(), n.createdAt());
    }
}
```

- [ ] **Step 6: Añadir rate limiting en `RateLimitingFilter` para `/memberships/redeem`**

En `RateLimitingFilter.java`, añadir un bucket específico por IP para el endpoint de redención. Busca la lógica de creación de buckets y añade:

```java
// Bucket especial para redención de códigos: 10 intentos por IP por hora
if (request.getRequestURI().contains("/memberships/redeem")) {
    bucket = redeemBuckets.computeIfAbsent(clientIp, ip ->
        Bucket.builder()
            .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofHours(1))))
            .build());
}
```

- [ ] **Step 7: Ejecutar suite completa de tests del backend**

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/microsoft-25.jdk/Contents/Home \
  mvn test 2>&1 | tail -20
```

Esperado: `BUILD SUCCESS`.

- [ ] **Step 8: Commit**

```bash
git add src/main/java/com/ossflow/coaching/
git commit -m "feat(coaching): controllers, DTOs, persistencia notification — API coaching completa"
```

---

## Task 14: Frontend — tipos, API client y onboarding con selección de rol

**Files (frontend):**
- Modify: `src/features/identity/profile/types.ts`
- Create: `src/features/coaching/types.ts`
- Create: `src/features/coaching/api.ts`
- Create: `src/features/coaching/hooks.ts`
- Modify: `src/features/identity/profile/pages/ProfilePage.tsx` (onboarding con rol)
- Modify: `src/shared/api/client.ts` (si necesita ajustes)

- [ ] **Step 1: Añadir `role` al tipo `UserProfile` del frontend**

En `src/features/identity/profile/types.ts`, añadir campo `role`:

```typescript
export type AccountRole = 'ATHLETE' | 'ATHLETE_COACH'

export type UserProfile = {
  id: number
  ownerId: number
  displayName: string
  firstName?: string
  lastName?: string
  alias?: string
  currentBelt: string
  beltSince?: string
  academy?: string
  preferredModality: string
  onboardingCompleted: boolean
  federations: ProfileFederationEntry[]
  createdAt: string
  updatedAt: string
  version: number
  role: AccountRole   // ← nuevo
}
```

- [ ] **Step 2: Crear tipos de coaching**

```typescript
// src/features/coaching/types.ts
export type InvitationCode = {
  code: string
  expiresAt: string
  usedCount: number
}

export type CoachAthleteListItem = {
  athleteId: number
  displayName: string
  currentBelt: string
  lastSeen: string
}

export type CoachListItem = {
  coachId: number
  displayName: string
  academy: string | null
}

export type ActiveInjuryItem = {
  bodyPart: string
  severity: string
}

export type CompetitionItem = {
  name: string
  date: string
  result: string | null
}

export type AthleteSummary = {
  athleteId: number
  displayName: string
  currentBelt: string
  daysInBelt: number
  academy: string | null
  activeInjuries: ActiveInjuryItem[]
  recentCompetitions: CompetitionItem[]
  lastSessionDate: string | null
  daysSinceLastSession: number
}

export type CoachingNotification = {
  id: number
  type: 'ATHLETE_JOINED' | 'ATHLETE_LEFT' | 'COACH_REMOVED_YOU'
  payload: string
  createdAt: string
}
```

- [ ] **Step 3: Crear API client de coaching**

```typescript
// src/features/coaching/api.ts
import { client } from '@/shared/api/client'
import type {
  InvitationCode, CoachAthleteListItem, CoachListItem,
  AthleteSummary, CoachingNotification
} from './types'

export const coachingApi = {
  generateInvitation: () =>
    client.post('api/v1/coaching/invitations').json<InvitationCode>(),

  getActiveInvitation: () =>
    client.get('api/v1/coaching/invitations/active').json<InvitationCode>(),

  revokeInvitation: () =>
    client.delete('api/v1/coaching/invitations/active'),

  redeemCode: (code: string) =>
    client.post('api/v1/coaching/memberships/redeem', { json: { code } }),

  removeAthlete: (athleteId: number) =>
    client.delete(`api/v1/coaching/memberships/${athleteId}`),

  leaveCoach: (coachId: number) =>
    client.delete(`api/v1/coaching/memberships/leave/${coachId}`),

  getAthletes: () =>
    client.get('api/v1/coaching/athletes').json<CoachAthleteListItem[]>(),

  getAthleteSummary: (athleteId: number) =>
    client.get(`api/v1/coaching/athletes/${athleteId}/summary`).json<AthleteSummary>(),

  getCoaches: () =>
    client.get('api/v1/coaching/coaches').json<CoachListItem[]>(),

  getNotifications: () =>
    client.get('api/v1/coaching/notifications').json<CoachingNotification[]>(),

  markNotificationsRead: () =>
    client.patch('api/v1/coaching/notifications/read'),

  changeRole: (role: 'ATHLETE' | 'ATHLETE_COACH') =>
    client.patch('api/v1/me/role', { json: { role } }),
}
```

- [ ] **Step 4: Crear hooks de coaching**

```typescript
// src/features/coaching/hooks.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { coachingApi } from './api'

export const COACHING_KEYS = {
  invitation: ['coaching', 'invitation'] as const,
  athletes: ['coaching', 'athletes'] as const,
  athlete: (id: number) => ['coaching', 'athletes', id] as const,
  coaches: ['coaching', 'coaches'] as const,
  notifications: ['coaching', 'notifications'] as const,
}

export function useActiveInvitation() {
  return useQuery({
    queryKey: COACHING_KEYS.invitation,
    queryFn: coachingApi.getActiveInvitation,
  })
}

export function useGenerateInvitation() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: coachingApi.generateInvitation,
    onSuccess: () => qc.invalidateQueries({ queryKey: COACHING_KEYS.invitation }),
  })
}

export function useRevokeInvitation() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: coachingApi.revokeInvitation,
    onSuccess: () => qc.invalidateQueries({ queryKey: COACHING_KEYS.invitation }),
  })
}

export function useAthletes() {
  return useQuery({ queryKey: COACHING_KEYS.athletes, queryFn: coachingApi.getAthletes })
}

export function useAthleteSummary(athleteId: number) {
  return useQuery({
    queryKey: COACHING_KEYS.athlete(athleteId),
    queryFn: () => coachingApi.getAthleteSummary(athleteId),
  })
}

export function useCoaches() {
  return useQuery({ queryKey: COACHING_KEYS.coaches, queryFn: coachingApi.getCoaches })
}

export function useRedeemCode() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (code: string) => coachingApi.redeemCode(code),
    onSuccess: () => qc.invalidateQueries({ queryKey: COACHING_KEYS.coaches }),
  })
}

export function useNotifications() {
  return useQuery({
    queryKey: COACHING_KEYS.notifications,
    queryFn: coachingApi.getNotifications,
    refetchInterval: 30_000,
  })
}

export function useMarkNotificationsRead() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: coachingApi.markNotificationsRead,
    onSuccess: () => qc.invalidateQueries({ queryKey: COACHING_KEYS.notifications }),
  })
}
```

- [ ] **Step 5: Actualizar onboarding — añadir paso de selección de rol**

En el flujo de onboarding (busca `onboardingCompleted` en el codebase para localizar el componente), añadir un paso de selección de rol. El paso aparece ANTES de completar el perfil si `onboardingCompleted === false` y el usuario no tiene rol definido (cuenta OAuth2 nueva).

El componente de selección de rol sigue el diseño visual de OssFlow:

```tsx
// src/features/identity/profile/components/RoleSelector.tsx
import { useState } from 'react'
import { coachingApi } from '@/features/coaching/api'
import { useQueryClient } from '@tanstack/react-query'

type Props = { onComplete: () => void }

export function RoleSelector({ onComplete }: Props) {
  const [selected, setSelected] = useState<'ATHLETE' | 'ATHLETE_COACH' | null>(null)
  const [loading, setLoading] = useState(false)
  const qc = useQueryClient()

  async function handleConfirm() {
    if (!selected) return
    setLoading(true)
    await coachingApi.changeRole(selected)
    qc.invalidateQueries({ queryKey: ['profile'] })
    onComplete()
  }

  return (
    <div className="min-h-screen bg-background flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <p className="font-mono text-xs text-muted-foreground uppercase tracking-widest mb-2">
          Bienvenido a OssFlow
        </p>
        <h1 className="font-serif text-3xl font-black text-foreground mb-2">
          ¿Cómo usarás la app?
        </h1>
        <p className="text-muted-foreground mb-8">
          Esto determina las herramientas que verás. Puedes cambiarlo después.
        </p>

        <div className="space-y-3 mb-8">
          {([
            {
              value: 'ATHLETE' as const,
              label: 'Soy atleta',
              desc: 'Registro mi entrenamiento, analizo mi progreso y gestiono mi game.',
            },
            {
              value: 'ATHLETE_COACH' as const,
              label: 'Soy maestro',
              desc: 'Llevo a mis alumnos y también registro mi propio entrenamiento.',
            },
          ] as const).map(opt => (
            <button
              key={opt.value}
              onClick={() => setSelected(opt.value)}
              className={`w-full text-left p-4 rounded-xl border transition-all ${
                selected === opt.value
                  ? 'border-purple-500 bg-purple-500/10'
                  : 'border-border bg-card hover:border-purple-500/50'
              }`}
            >
              <div className="font-semibold text-foreground">{opt.label}</div>
              <div className="text-sm text-muted-foreground mt-0.5">{opt.desc}</div>
            </button>
          ))}
        </div>

        <button
          disabled={!selected || loading}
          onClick={handleConfirm}
          className="w-full py-3 bg-purple-600 hover:bg-purple-700 disabled:opacity-40
                     text-white font-semibold rounded-xl transition-colors"
        >
          {loading ? 'Guardando...' : 'Continuar'}
        </button>
      </div>
    </div>
  )
}
```

- [ ] **Step 6: Commit frontend base**

```bash
cd /ruta/OssFlow-frontend
git add src/features/coaching/ src/features/identity/profile/types.ts \
        src/features/identity/profile/components/RoleSelector.tsx
git commit -m "feat(coaching): tipos, API client, hooks y RoleSelector"
```

---

## Task 15: Frontend — Panel de coaching (vista del maestro)

**Files:**
- Create: `src/features/coaching/pages/CoachingDashboardPage.tsx`
- Create: `src/features/coaching/components/InvitationCard.tsx`
- Create: `src/features/coaching/components/AthleteRoster.tsx`
- Create: `src/features/coaching/components/AthleteSummaryDrawer.tsx`
- Modify: `src/app/router.tsx` (o donde vivan las rutas)

- [ ] **Step 1: Crear `InvitationCard`**

```tsx
// src/features/coaching/components/InvitationCard.tsx
import { useActiveInvitation, useGenerateInvitation, useRevokeInvitation } from '../hooks'
import { formatDistanceToNow } from 'date-fns'
import { es } from 'date-fns/locale'

export function InvitationCard() {
  const { data: inv, isLoading } = useActiveInvitation()
  const generate = useGenerateInvitation()
  const revoke = useRevokeInvitation()

  if (isLoading) return <div className="h-32 bg-card rounded-xl animate-pulse" />

  return (
    <div className="bg-card border border-border rounded-xl p-5">
      <div className="flex items-center justify-between mb-4">
        <h3 className="font-semibold text-foreground">Código de invitación</h3>
        {inv && (
          <span className="text-xs font-mono text-muted-foreground">
            {inv.usedCount} alumno{inv.usedCount !== 1 ? 's' : ''}
          </span>
        )}
      </div>

      {inv ? (
        <>
          <div className="font-mono text-4xl font-black text-purple-400 tracking-[0.3em] mb-2">
            {inv.code}
          </div>
          <p className="text-xs text-muted-foreground mb-4">
            Expira {formatDistanceToNow(new Date(inv.expiresAt), { addSuffix: true, locale: es })}
          </p>
          <div className="flex gap-2">
            <button
              onClick={() => generate.mutate()}
              className="flex-1 py-2 text-sm font-medium bg-purple-600 hover:bg-purple-700
                         text-white rounded-lg transition-colors"
            >
              Generar nuevo
            </button>
            <button
              onClick={() => revoke.mutate()}
              className="px-4 py-2 text-sm font-medium border border-border
                         text-muted-foreground hover:text-foreground rounded-lg transition-colors"
            >
              Revocar
            </button>
          </div>
        </>
      ) : (
        <button
          onClick={() => generate.mutate()}
          className="w-full py-3 text-sm font-semibold bg-purple-600 hover:bg-purple-700
                     text-white rounded-lg transition-colors"
        >
          Generar código de invitación
        </button>
      )}
    </div>
  )
}
```

- [ ] **Step 2: Crear `AthleteRoster`**

```tsx
// src/features/coaching/components/AthleteRoster.tsx
import { useAthletes } from '../hooks'
import type { CoachAthleteListItem } from '../types'

const BELT_COLORS: Record<string, string> = {
  WHITE: '#d1d5db', BLUE: '#3b82f6', PURPLE: '#9333ea',
  BROWN: '#92400e', BLACK: '#111827',
}

type Props = { onSelectAthlete: (id: number) => void }

export function AthleteRoster({ onSelectAthlete }: Props) {
  const { data: athletes = [], isLoading } = useAthletes()

  if (isLoading) return (
    <div className="space-y-2">
      {[1,2,3].map(i => <div key={i} className="h-16 bg-card rounded-xl animate-pulse" />)}
    </div>
  )

  if (athletes.length === 0) return (
    <div className="text-center py-12 text-muted-foreground">
      <p className="text-4xl mb-3">🥋</p>
      <p className="font-medium">Aún no tienes alumnos vinculados.</p>
      <p className="text-sm mt-1">Comparte tu código de invitación para empezar.</p>
    </div>
  )

  return (
    <div className="space-y-2">
      {athletes.map(athlete => (
        <AthleteRow key={athlete.athleteId} athlete={athlete} onSelect={onSelectAthlete} />
      ))}
    </div>
  )
}

function AthleteRow({ athlete, onSelect }: { athlete: CoachAthleteListItem; onSelect: (id: number) => void }) {
  const beltColor = BELT_COLORS[athlete.currentBelt] ?? '#d1d5db'

  return (
    <button
      onClick={() => onSelect(athlete.athleteId)}
      className="w-full flex items-center gap-3 p-3 bg-card border border-border
                 rounded-xl hover:border-purple-500/50 transition-all text-left"
    >
      <div className="w-1 h-10 rounded-full flex-shrink-0" style={{ backgroundColor: beltColor }} />
      <div className="flex-1 min-w-0">
        <div className="font-medium text-foreground truncate">{athlete.displayName}</div>
        <div className="text-xs text-muted-foreground capitalize">
          {athlete.currentBelt.toLowerCase()}
        </div>
      </div>
      <svg className="w-4 h-4 text-muted-foreground" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
      </svg>
    </button>
  )
}
```

- [ ] **Step 3: Crear `AthleteSummaryDrawer`**

```tsx
// src/features/coaching/components/AthleteSummaryDrawer.tsx
import { useAthleteSummary } from '../hooks'

type Props = { athleteId: number | null; onClose: () => void }

export function AthleteSummaryDrawer({ athleteId, onClose }: Props) {
  const { data, isLoading } = useAthleteSummary(athleteId ?? 0)

  if (!athleteId) return null

  const activityColor = data
    ? data.daysSinceLastSession < 0 ? '#6b7280'
      : data.daysSinceLastSession <= 3 ? '#10b981'
      : data.daysSinceLastSession <= 7 ? '#f59e0b'
      : '#ef4444'
    : '#6b7280'

  return (
    <div className="fixed inset-0 z-50 flex justify-end">
      <div className="absolute inset-0 bg-black/60" onClick={onClose} />
      <div className="relative w-full max-w-sm bg-background border-l border-border
                      h-full overflow-y-auto p-5 space-y-5">
        <div className="flex items-center justify-between">
          <h2 className="font-serif text-xl font-bold text-foreground">
            {isLoading ? '…' : data?.displayName}
          </h2>
          <button onClick={onClose} className="text-muted-foreground hover:text-foreground">
            ✕
          </button>
        </div>

        {isLoading && <div className="space-y-3">
          {[1,2,3].map(i => <div key={i} className="h-20 bg-card rounded-xl animate-pulse" />)}
        </div>}

        {data && <>
          {/* Cinturón y actividad */}
          <div className="grid grid-cols-2 gap-3">
            <div className="bg-card border border-border rounded-xl p-3">
              <div className="text-xs text-muted-foreground mb-1">Cinturón</div>
              <div className="font-semibold text-foreground capitalize">
                {data.currentBelt.toLowerCase()}
              </div>
              <div className="text-xs text-muted-foreground">{data.daysInBelt} días</div>
            </div>
            <div className="bg-card border border-border rounded-xl p-3">
              <div className="text-xs text-muted-foreground mb-1">Actividad</div>
              <div className="w-2.5 h-2.5 rounded-full mb-1" style={{ backgroundColor: activityColor }} />
              <div className="text-xs text-muted-foreground">
                {data.daysSinceLastSession >= 0
                  ? `Hace ${data.daysSinceLastSession} días`
                  : 'Sin sesiones'}
              </div>
            </div>
          </div>

          {/* Lesiones activas */}
          {data.activeInjuries.length > 0 && (
            <div>
              <h3 className="text-xs font-mono uppercase tracking-widest text-muted-foreground mb-2">
                Lesiones activas
              </h3>
              <div className="space-y-1">
                {data.activeInjuries.map((inj, i) => (
                  <div key={i} className="flex items-center gap-2 text-sm">
                    <span className="w-1.5 h-1.5 rounded-full bg-red-500 flex-shrink-0" />
                    <span className="text-foreground">{inj.bodyPart}</span>
                    <span className="text-muted-foreground text-xs">({inj.severity})</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Competiciones */}
          {data.recentCompetitions.length > 0 && (
            <div>
              <h3 className="text-xs font-mono uppercase tracking-widest text-muted-foreground mb-2">
                Últimas competiciones
              </h3>
              <div className="space-y-1">
                {data.recentCompetitions.map((c, i) => (
                  <div key={i} className="flex items-center justify-between text-sm">
                    <span className="text-foreground truncate">{c.name}</span>
                    <span className="text-muted-foreground text-xs ml-2 flex-shrink-0">{c.date}</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </>}
      </div>
    </div>
  )
}
```

- [ ] **Step 4: Crear `CoachingDashboardPage`**

```tsx
// src/features/coaching/pages/CoachingDashboardPage.tsx
import { useState } from 'react'
import { InvitationCard } from '../components/InvitationCard'
import { AthleteRoster } from '../components/AthleteRoster'
import { AthleteSummaryDrawer } from '../components/AthleteSummaryDrawer'

export function CoachingDashboardPage() {
  const [selectedAthleteId, setSelectedAthleteId] = useState<number | null>(null)

  return (
    <div className="max-w-2xl mx-auto px-4 py-6 space-y-6">
      <div>
        <p className="font-mono text-xs text-muted-foreground uppercase tracking-widest mb-1">
          Mi gimnasio
        </p>
        <h1 className="font-serif text-3xl font-black text-foreground">Panel de coaching</h1>
      </div>

      <InvitationCard />

      <div>
        <h2 className="font-semibold text-foreground mb-3">
          Mis alumnos
        </h2>
        <AthleteRoster onSelectAthlete={setSelectedAthleteId} />
      </div>

      <AthleteSummaryDrawer
        athleteId={selectedAthleteId}
        onClose={() => setSelectedAthleteId(null)}
      />
    </div>
  )
}
```

- [ ] **Step 5: Añadir ruta `/gimnasio` al router**

Busca el fichero de rutas del proyecto y añade:

```tsx
{ path: '/gimnasio', element: <CoachingDashboardPage /> }
```

- [ ] **Step 6: Commit**

```bash
git add src/features/coaching/pages/ src/features/coaching/components/
git commit -m "feat(coaching): panel de coaching — InvitationCard, AthleteRoster, AthleteSummaryDrawer"
```

---

## Task 16: Frontend — sección "Mis maestros" + campana de notificaciones

**Files:**
- Create: `src/features/coaching/components/MyCoachesSection.tsx`
- Create: `src/features/coaching/components/NotificationBell.tsx`
- Modify: `src/shared/components/TopNavBar.tsx` (o equivalente)
- Modify: `src/shared/components/BottomTabBar.tsx` (o equivalente)

- [ ] **Step 1: Crear `MyCoachesSection`**

```tsx
// src/features/coaching/components/MyCoachesSection.tsx
import { useState } from 'react'
import { useCoaches, useRedeemCode } from '../hooks'
import { coachingApi } from '../api'
import { useQueryClient } from '@tanstack/react-query'
import { COACHING_KEYS } from '../hooks'

export function MyCoachesSection() {
  const { data: coaches = [] } = useCoaches()
  const redeemCode = useRedeemCode()
  const [code, setCode] = useState('')
  const [error, setError] = useState<string | null>(null)
  const qc = useQueryClient()

  async function handleRedeem() {
    if (code.length !== 6) return
    setError(null)
    try {
      await redeemCode.mutateAsync(code.toUpperCase())
      setCode('')
    } catch {
      setError('Código inválido o expirado. Comprueba el código con tu maestro.')
    }
  }

  async function handleLeave(coachId: number) {
    await coachingApi.leaveCoach(coachId)
    qc.invalidateQueries({ queryKey: COACHING_KEYS.coaches })
  }

  return (
    <div className="space-y-5">
      {/* Redimir código */}
      <div className="bg-card border border-border rounded-xl p-4">
        <h3 className="font-semibold text-foreground mb-3">Vincularme a un maestro</h3>
        <div className="flex gap-2">
          <input
            type="text"
            value={code}
            onChange={e => setCode(e.target.value.toUpperCase().slice(0, 6))}
            placeholder="Código de 6 caracteres"
            className="flex-1 font-mono tracking-widest uppercase bg-background border border-border
                       rounded-lg px-3 py-2 text-foreground placeholder:text-muted-foreground
                       focus:outline-none focus:border-purple-500"
          />
          <button
            onClick={handleRedeem}
            disabled={code.length !== 6 || redeemCode.isPending}
            className="px-4 py-2 bg-purple-600 hover:bg-purple-700 disabled:opacity-40
                       text-white font-medium rounded-lg transition-colors text-sm"
          >
            Vincular
          </button>
        </div>
        {error && <p className="text-red-400 text-xs mt-2">{error}</p>}
      </div>

      {/* Lista de maestros */}
      {coaches.length > 0 && (
        <div>
          <h3 className="font-semibold text-foreground mb-2">Mis maestros</h3>
          <div className="space-y-2">
            {coaches.map(coach => (
              <div key={coach.coachId}
                   className="flex items-center justify-between p-3 bg-card border border-border rounded-xl">
                <div>
                  <div className="font-medium text-foreground">{coach.displayName}</div>
                  {coach.academy && (
                    <div className="text-xs text-muted-foreground">{coach.academy}</div>
                  )}
                </div>
                <button
                  onClick={() => handleLeave(coach.coachId)}
                  className="text-xs text-muted-foreground hover:text-red-400 transition-colors"
                >
                  Desvincular
                </button>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
```

- [ ] **Step 2: Crear `NotificationBell`**

```tsx
// src/features/coaching/components/NotificationBell.tsx
import { useState } from 'react'
import { useNotifications, useMarkNotificationsRead } from '../hooks'
import type { CoachingNotification } from '../types'
import { formatDistanceToNow } from 'date-fns'
import { es } from 'date-fns/locale'

const NOTIFICATION_LABELS: Record<CoachingNotification['type'], string> = {
  ATHLETE_JOINED: '🥋 Nuevo alumno',
  ATHLETE_LEFT: 'Alumno desvinculado',
  COACH_REMOVED_YOU: 'Desvinculado de maestro',
}

function parsePayload(payload: string): Record<string, string> {
  try { return JSON.parse(payload) } catch { return {} }
}

export function NotificationBell() {
  const [open, setOpen] = useState(false)
  const { data: notifications = [] } = useNotifications()
  const markRead = useMarkNotificationsRead()

  function handleOpen() {
    setOpen(true)
    if (notifications.length > 0) markRead.mutate()
  }

  return (
    <div className="relative">
      <button
        onClick={handleOpen}
        className="relative p-2 text-muted-foreground hover:text-foreground transition-colors"
      >
        <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6 6 0 10-12 0v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
        </svg>
        {notifications.length > 0 && (
          <span className="absolute top-1 right-1 w-2 h-2 bg-purple-500 rounded-full" />
        )}
      </button>

      {open && (
        <>
          <div className="fixed inset-0 z-40" onClick={() => setOpen(false)} />
          <div className="absolute right-0 top-10 z-50 w-72 bg-card border border-border
                          rounded-xl shadow-xl overflow-hidden">
            <div className="p-3 border-b border-border">
              <span className="text-sm font-semibold text-foreground">Notificaciones</span>
            </div>
            {notifications.length === 0 ? (
              <div className="p-4 text-sm text-muted-foreground text-center">
                Sin notificaciones nuevas
              </div>
            ) : (
              <div className="max-h-64 overflow-y-auto">
                {notifications.map(n => {
                  const p = parsePayload(n.payload)
                  return (
                    <div key={n.id} className="p-3 border-b border-border/50 last:border-0">
                      <div className="text-xs font-medium text-foreground">
                        {NOTIFICATION_LABELS[n.type]}
                      </div>
                      <div className="text-xs text-muted-foreground mt-0.5">
                        {p.athleteName ?? p.coachName ?? ''}
                      </div>
                      <div className="text-xs text-muted-foreground/60 mt-0.5">
                        {formatDistanceToNow(new Date(n.createdAt), { addSuffix: true, locale: es })}
                      </div>
                    </div>
                  )
                })}
              </div>
            )}
          </div>
        </>
      )}
    </div>
  )
}
```

- [ ] **Step 3: Añadir `NotificationBell` al navbar**

En `TopNavBar.tsx` (o equivalente), añadir el componente junto a los controles de la derecha. Solo renderizarlo si el usuario está autenticado.

- [ ] **Step 4: Añadir "Mi gimnasio" a la navegación para ATHLETE_COACH**

En `TopNavBar` y `BottomTabBar`, mostrar el enlace `/gimnasio` solo si `profile.role === 'ATHLETE_COACH'`. Ejemplo:

```tsx
{profile?.role === 'ATHLETE_COACH' && (
  <NavLink to="/gimnasio">Mi gimnasio</NavLink>
)}
```

- [ ] **Step 5: Integrar `MyCoachesSection` en la página de configuración o perfil**

Busca `ConfiguracionPage` o `ProfilePage` y añade la sección `<MyCoachesSection />` dentro de una pestaña o sección de "Mis maestros".

- [ ] **Step 6: Verificar que tsc pasa**

```bash
cd /ruta/OssFlow-frontend && npx tsc -b 2>&1 | head -20
```

Esperado: sin errores.

- [ ] **Step 7: Commit**

```bash
git add src/features/coaching/components/MyCoachesSection.tsx \
        src/features/coaching/components/NotificationBell.tsx \
        src/shared/components/
git commit -m "feat(coaching): MyCoachesSection, NotificationBell, navegación Mi gimnasio"
```

---

## Task 17: Tests de integración backend + build final

**Files:**
- Create: `src/test/java/com/ossflow/coaching/CoachingIntegrationTest.java`

- [ ] **Step 1: Escribir test de integración del flujo completo**

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class CoachingIntegrationTest {

    @Autowired CoachInvitationService invitationService;
    @Autowired CoachAthleteService coachAthleteService;

    @Test
    void full_invite_and_link_flow() {
        Long coachId = 1L; // cuenta seed
        Long athleteId = 2L;

        CoachInvitation inv = invitationService.generate(coachId);
        assertThat(inv.code()).hasSize(6);
        assertThat(inv.status()).isEqualTo(InvitationStatus.PENDING);

        CoachInvitation validated = invitationService.validateCode(inv.code());
        assertThat(validated).isNotNull();

        coachAthleteService.link(coachId, athleteId, inv.id());
        assertThat(coachAthleteService.isLinked(coachId, athleteId)).isTrue();

        coachAthleteService.unlinkByCoach(coachId, athleteId);
        assertThat(coachAthleteService.isLinked(coachId, athleteId)).isFalse();
    }

    @Test
    void generate_revokes_previous_invitation() {
        Long coachId = 1L;
        CoachInvitation first = invitationService.generate(coachId);
        CoachInvitation second = invitationService.generate(coachId);

        assertThat(second.status()).isEqualTo(InvitationStatus.PENDING);
        assertThat(invitationService.validateCode(first.code())).isNull(); // fue revocado
    }
}
```

- [ ] **Step 2: Ejecutar suite completa**

```bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/microsoft-25.jdk/Contents/Home \
  mvn verify 2>&1 | tail -20
```

Esperado: `BUILD SUCCESS`.

- [ ] **Step 3: Frontend — lint y build**

```bash
cd /ruta/OssFlow-frontend
npm run lint && npx tsc -b && npm run build 2>&1 | tail -10
```

Esperado: sin errores.

- [ ] **Step 4: Commit final backend + tag**

```bash
cd /ruta/OssFlow
git add -p
git commit -m "feat(coaching): tests de integración flujo completo maestro-atleta"
```

---

## Task 18: Deploy en LXC (NO producción)

**Target:** LXC `10.10.100.15` — rama `feature/maestro-atleta`

- [ ] **Step 1: Push de ambas ramas al remoto**

```bash
cd /ruta/OssFlow && git push origin feature/maestro-atleta
cd /ruta/OssFlow-frontend && git push origin feature/maestro-atleta
```

- [ ] **Step 2: Conectar al LXC**

```bash
sshpass -p 'J0n1n4p4l1' ssh -o StrictHostKeyChecking=no ossflow@10.10.100.15
```

- [ ] **Step 3: En el LXC, cambiar a la rama feature en ambos repos y hacer deploy**

```bash
# Backend
cd ~/OssFlow && git fetch origin && git checkout feature/maestro-atleta && git pull

# Frontend
cd ~/OssFlow-frontend && git fetch origin && git checkout feature/maestro-atleta && git pull

# Deploy
cd ~/ossflow-deploy && ./deploy.sh
```

- [ ] **Step 4: Verificar que el backend arranca correctamente**

```bash
docker compose logs backend --tail=50 | grep -E "Started|ERROR|Flyway"
```

Esperado: `Started OssFlowApplication` y `Successfully applied N migrations`.

- [ ] **Step 5: Smoke test manual**

Desde el navegador en `http://10.10.100.15`:
1. Registrar cuenta nueva como ATHLETE — verificar que el rol se guarda
2. Registrar cuenta nueva como COACH (→ ATHLETE_COACH) — verificar que aparece "Mi gimnasio" en el nav
3. Generar código de invitación desde el panel de coaching
4. Con la cuenta de atleta, vincular usando el código
5. Verificar que el maestro ve al atleta en su roster
6. Verificar que llega notificación in-app al maestro

---

## Resumen de ficheros

### Backend — nuevos (40+)
- `V256-V259` Flyway migrations
- `AccountRole.java`
- `coaching/invitation/` — 9 ficheros
- `coaching/relationship/` — 10 ficheros + `AthleteProfileComposer`
- `coaching/notification/` — 9 ficheros

### Backend — modificados (12)
- `Account.java`, `AccountEntity.java`, `AccountPersistenceMapper.java`
- `AuthService.java`, `OAuth2UserService.java`, `RegisterRequest.java`
- `AccountPrincipal.java`, `JwtAuthenticationFilter.java`
- `AccountController.java`, `EmailService.java`, `EmailOutboxService.java`
- 19 ficheros de test con instancias de `Account`

### Frontend — nuevos (10)
- `coaching/types.ts`, `coaching/api.ts`, `coaching/hooks.ts`
- `coaching/pages/CoachingDashboardPage.tsx`
- `coaching/components/InvitationCard.tsx`
- `coaching/components/AthleteRoster.tsx`
- `coaching/components/AthleteSummaryDrawer.tsx`
- `coaching/components/MyCoachesSection.tsx`
- `coaching/components/NotificationBell.tsx`
- `identity/profile/components/RoleSelector.tsx`

### Frontend — modificados (4)
- `identity/profile/types.ts`
- `router.tsx`
- `TopNavBar.tsx`, `BottomTabBar.tsx`
