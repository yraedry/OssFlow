# OssFlow — Spec Técnico: Sistema de Roles y Vinculación Maestro-Atleta (Fase 1+2)

**Fecha:** 2026-05-13
**Estado:** Aprobado — listo para implementación
**Alcance:** Fase 1 (roles) + Fase 2 (vinculación coach-atleta, invitaciones, notificaciones básicas)

---

## 1. Principio rector

El rol define qué puede ver y hacer el usuario. La vinculación coach-atleta es una relación explícita y bidireccional con ciclo de vida propio. Los datos personales del atleta (journal, notas, sesiones) permanecen privados — el maestro solo accede a una ficha básica de sus alumnos vinculados.

---

## 2. Modelo de roles

### 2.1 Enum `AccountRole`

```java
public enum AccountRole { ATHLETE, ATHLETE_COACH }
```

- **ATHLETE**: luchador que solo entrena. Puede vincularse a múltiples maestros.
- **ATHLETE_COACH**: usuario que entrena y también tiene alumnos. Es el rol asignado cuando el usuario elige "Maestro" en el onboarding. Un maestro en OssFlow siempre es también atleta — no existe "maestro puro".

### 2.2 Dónde vive el rol

El rol vive en `Account` (identidad), no en `UserProfile`. Se persiste como `VARCHAR(20)` con CHECK constraint en PostgreSQL. Sin tabla `role` separada.

### 2.3 Cómo se resuelve el rol en cada request

El rol se lee desde el **cache de Account** en `JwtAuthenticationFilter` (TTL 60s). No viaja como claim en el JWT. Al cambiar el rol se hace bump de `tokenVersion` para invalidar la cache y forzar re-lectura inmediata.

`AccountPrincipal.getAuthorities()` devuelve:
- `ATHLETE` → `[ROLE_ATHLETE]`
- `ATHLETE_COACH` → `[ROLE_ATHLETE, ROLE_COACH]`

### 2.4 Asignación de rol

- **Registro con email/password**: `RegisterRequest` incluye campo `role` (ATHLETE | COACH). Si elige COACH, el sistema persiste `ATHLETE_COACH`.
- **Registro con OAuth2 Google/Apple**: se asigna `ATHLETE` por defecto. El onboarding pregunta el rol como parte del flujo de completar perfil.
- **Cambio de rol posterior**: endpoint `PATCH /api/v1/me/role` disponible desde configuración. Hace bump de `tokenVersion`.

---

## 3. Cambios en `identity/auth`

### 3.1 Ficheros a crear

- `identity/auth/domain/AccountRole.java` — enum

### 3.2 Ficheros a modificar

| Fichero | Cambio |
|---------|--------|
| `Account.java` | Añadir `AccountRole role`. Añadir `@Builder(toBuilder = true)`. |
| `AccountEntity.java` | Añadir `@Enumerated(STRING) AccountRole role` |
| `AccountPersistenceMapper.java` | Propagar `role` en `toDomain()` y `toEntity()` |
| `AuthService.java` | En `register()` asignar rol. Si COACH → persistir ATHLETE_COACH. Bump tokenVersion al cambiar rol. |
| `OAuth2UserService.java` | Asignar `ATHLETE` por defecto en cuentas nuevas OAuth2 |
| `AccountPrincipal.java` | Añadir campo `AccountRole role`. `getAuthorities()` devuelve roles correctos. |
| `JwtAuthenticationFilter.java` | Pasar `account.role()` al construir `AccountPrincipal` |
| `RegisterRequest.java` | Añadir campo `AccountRole role` (nullable, default ATHLETE en service) |
| `SecurityConfig.java` | Exponer `/api/v1/coaching/**` como autenticado |

### 3.3 Tests a actualizar

27 callsites que instancian `Account` con constructor canónico deben actualizarse. Ver lista completa en validación del code-architect.

---

## 4. Bounded context `coaching/`

Nuevo contexto hermano de `identity/`, `journal/`, `planning/`. Paquete raíz: `com.ossflow.coaching`.

```
coaching/
├── invitation/
│   ├── domain/
│   │   ├── CoachInvitation.java
│   │   └── InvitationStatus.java
│   ├── application/
│   │   ├── CoachInvitationService.java
│   │   └── port/CoachInvitationRepositoryPort.java
│   └── infrastructure/
│       ├── persistence/
│       │   ├── CoachInvitationEntity.java
│       │   ├── CoachInvitationJpaRepository.java
│       │   ├── CoachInvitationPersistenceAdapter.java
│       │   └── CoachInvitationPersistenceMapper.java
│       └── web/
│           ├── CoachInvitationController.java
│           └── dto/
│               ├── InvitationCodeResponse.java
│               └── RedeemInvitationRequest.java
├── relationship/
│   ├── domain/
│   │   └── CoachAthleteRelationship.java
│   ├── application/
│   │   ├── CoachAthleteService.java
│   │   ├── AthleteProfileComposer.java        ← servicio de composición
│   │   └── port/CoachAthleteRepositoryPort.java
│   └── infrastructure/
│       ├── persistence/
│       │   ├── CoachAthleteEntity.java
│       │   ├── CoachAthleteJpaRepository.java
│       │   ├── CoachAthletePersistenceAdapter.java
│       │   └── CoachAthletePersistenceMapper.java
│       └── web/
│           ├── CoachAthleteController.java
│           └── dto/
│               ├── AthleteListItemResponse.java
│               └── AthleteSummaryResponse.java
└── notification/
    ├── domain/
    │   ├── CoachingNotification.java
    │   └── NotificationType.java
    ├── application/
    │   ├── CoachingNotificationService.java
    │   └── port/CoachingNotificationRepositoryPort.java
    └── infrastructure/
        ├── persistence/
        │   ├── CoachingNotificationEntity.java
        │   ├── CoachingNotificationJpaRepository.java
        │   ├── CoachingNotificationPersistenceAdapter.java
        │   └── CoachingNotificationPersistenceMapper.java
        └── web/
            ├── CoachingNotificationController.java
            └── dto/NotificationResponse.java
```

---

## 5. Dominio `coaching/invitation`

### 5.1 `CoachInvitation`

```java
@Builder(toBuilder = true)
public record CoachInvitation(
    Long id,
    Long coachId,
    String code,            // 6 chars alfanuméricos, SecureRandom
    InvitationStatus status,
    int usedCount,          // cuántos atletas han redimido este código
    Instant expiresAt,      // now + 48h
    Instant createdAt
) {}
```

### 5.2 `InvitationStatus`

```java
public enum InvitationStatus { PENDING, EXPIRED, REVOKED }
```

`USED` no existe — el código es reutilizable durante su TTL.

### 5.3 Máquina de estados

```
PENDING → EXPIRED   (job TTL o consulta lazy al redimir)
PENDING → REVOKED   (maestro cancela manualmente)
```

### 5.4 Reglas de negocio

- Un maestro tiene como máximo **un código activo** (PENDING) en todo momento. Si genera uno nuevo, el anterior pasa a REVOKED automáticamente.
- El código tiene TTL de **48 horas**.
- El código es **reutilizable**: N atletas pueden redimirlo mientras esté PENDING.
- UNIQUE parcial en BD: `WHERE status = 'PENDING'` por `coach_id`.

---

## 6. Dominio `coaching/relationship`

### 6.1 `CoachAthleteRelationship`

```java
@Builder(toBuilder = true)
public record CoachAthleteRelationship(
    Long id,
    Long coachId,
    Long athleteId,
    Long invitationId,      // FK a la invitación usada (auditoría)
    Instant linkedAt
) {}
```

### 6.2 Reglas de negocio

- Un atleta puede tener **múltiples maestros** simultáneamente.
- No se puede crear la relación si ya existe el par `(coachId, athleteId)` → 409 CONFLICT con código `ALREADY_LINKED`.
- Desvinculación bidireccional: el maestro puede eliminar a cualquiera de sus atletas; el atleta puede desvincularse de cualquiera de sus maestros.
- Al desvincular: acceso del maestro a la ficha del atleta cortado inmediatamente.

### 6.3 `AthleteProfileComposer`

Servicio de composición que agrega datos de varios repositorios para construir la ficha del alumno. El coach no tiene ownership sobre los datos — el composer llama a los repositorios pasando `athleteId` como `ownerId`.

```java
public AthleteSummaryResponse compose(Long coachId, Long athleteId);
```

Verifica que existe `CoachAthleteRelationship(coachId, athleteId)` antes de leer cualquier dato. Si no existe → 403.

Datos que agrega:
- `UserProfile` (cinturón, tiempo en cinturón, academy, displayName)
- `Injury` activas (status == ACTIVE)
- `CompetitionLog` (últimas 5, ordenadas por fecha)
- `TrainingSession` (última sesión + racha de días consecutivos)

---

## 7. Dominio `coaching/notification`

### 7.1 `CoachingNotification`

```java
@Builder(toBuilder = true)
public record CoachingNotification(
    Long id,
    Long recipientAccountId,
    NotificationType type,
    String payload,         // JSON con datos del evento (coachName, athleteName, etc.)
    boolean read,
    Instant createdAt
) {}
```

### 7.2 `NotificationType`

```java
public enum NotificationType {
    ATHLETE_JOINED,         // maestro: un atleta redimió tu código
    ATHLETE_LEFT,           // maestro: un atleta se desvinculó
    COACH_REMOVED_YOU,      // atleta: el maestro te ha desvinculado
    COACH_ACCEPTED_YOU      // atleta: tu vinculación fue aceptada (flujo futuro)
}
```

### 7.3 Eventos que generan notificación + email

| Evento | Notificación in-app | Email |
|--------|--------------------|----|
| Atleta redime código | → maestro (`ATHLETE_JOINED`) | Sí |
| Maestro desvincula atleta | → atleta (`COACH_REMOVED_YOU`) | Sí |
| Atleta se desvincula | → maestro (`ATHLETE_LEFT`) | Sí |

Email vía `EmailOutboxService` existente. Requiere añadir método genérico `enqueue(Long accountId, String recipient, String subject, String bodyHtml)` a `EmailOutboxService`, o nuevas plantillas en `EmailService`.

---

## 8. API Endpoints

Todos bajo `/api/v1/coaching/`, requieren autenticación.

### Invitaciones (solo ROLE_COACH)

```
POST   /api/v1/coaching/invitations          → genera/regenera código activo
DELETE /api/v1/coaching/invitations/active   → revoca código activo
GET    /api/v1/coaching/invitations/active   → consulta código activo + TTL + usedCount
```

### Membresías

```
POST   /api/v1/coaching/memberships/redeem            → atleta redime código (ROLE_ATHLETE o ROLE_COACH)
DELETE /api/v1/coaching/memberships/{athleteId}       → maestro desvincula atleta (ROLE_COACH)
DELETE /api/v1/coaching/memberships/leave/{coachId}   → atleta se desvincula (ROLE_ATHLETE o ROLE_COACH)
```

### Vista del maestro

```
GET    /api/v1/coaching/athletes                      → lista de alumnos del maestro (ROLE_COACH)
GET    /api/v1/coaching/athletes/{athleteId}/summary  → ficha básica del alumno (ROLE_COACH)
GET    /api/v1/coaching/coaches                       → lista de maestros del atleta (ROLE_ATHLETE)
```

### Notificaciones

```
GET    /api/v1/coaching/notifications        → notificaciones no leídas del usuario
PATCH  /api/v1/coaching/notifications/read  → marcar todas como leídas
```

### Rol

```
PATCH  /api/v1/me/role                       → cambiar rol (activa ATHLETE_COACH desde configuración)
```

---

## 9. Migraciones Flyway

```sql
-- V256__add_role_to_account.sql
ALTER TABLE account
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'ATHLETE';
ALTER TABLE account
    ADD CONSTRAINT ck_account_role CHECK (role IN ('ATHLETE','ATHLETE_COACH'));

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
CREATE INDEX ix_coach_invitation_code ON coach_invitation(code);

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

---

## 10. Seguridad

- Endpoint de redención de código (`POST /memberships/redeem`) protegido por rate limit de Bucket4j **por IP** (no por usuario — el atleta puede no estar registrado aún al recibir el código). Añadir a `RateLimitingFilter`.
- El endpoint de redención siempre completa el lookup completo antes de responder (sin short-circuit). Evita timing oracle para enumeración de códigos.
- `AthleteProfileComposer` verifica `CoachAthleteRelationship` activa en cada llamada — sin cache. Al desvincular, el acceso se corta inmediatamente.
- Al cambiar rol: bump de `tokenVersion` + invalidar cache de Caffeine del usuario.

---

## 11. Frontend — cambios principales

### Onboarding actualizado

- Paso nuevo: selección de rol — "Soy atleta" / "Soy maestro"
- Si elige maestro → rol `ATHLETE_COACH` (siempre atleta también)
- Cuentas OAuth2 nuevas: el onboarding pregunta el rol antes de continuar

### Navegación para `ATHLETE_COACH`

- Sección "Mi entrenamiento" — misma navegación actual del atleta
- Sección "Mi gimnasio" — panel de coaching (lista alumnos, código de invitación, notificaciones)
- Sin toggle ni selector de modo. Ambas secciones siempre visibles.

### Panel de coaching (nuevo)

- **Código de invitación**: mostrar código activo + TTL restante + número de alumnos que lo han usado. Botón "Generar nuevo código" / "Revocar".
- **Lista de alumnos**: cinturón, última sesión, semáforo de actividad (verde/amarillo/rojo según días sin sesión).
- **Ficha del alumno**: cinturón + tiempo, lesiones activas, últimas competiciones, actividad reciente.

### Atleta — vinculación

- Sección "Mis maestros" en perfil/configuración: introducir código de 6 chars, ver maestros vinculados, opción de desvincularse.

### Notificaciones

- Campana en navbar con badge de no leídas.
- Panel desplegable con lista de notificaciones recientes.

---

## 12. Fuera de scope (Fase 1+2)

- Radar subjetivo del maestro sobre el alumno (Fase 3)
- Cuaderno privado de notas del maestro (Fase 3)
- Planes asignados por el maestro al atleta (Fase 4)
- Sugerencias de técnicas (Fase 4)
- Requisitos de cinturón (Fase 8)
- Gamificación: desafíos del mes (Fase 9)
- Notificaciones push móvil
- Chat bidireccional

---

## 13. Orden de implementación

1. Migración V256 + enum `AccountRole` + modificar `Account` (con `@Builder(toBuilder=true)`) + actualizar 27 callsites
2. Modificar `AccountPrincipal`, `JwtAuthenticationFilter`, `AuthService`, `OAuth2UserService`
3. Endpoint `PATCH /api/v1/me/role`
4. Onboarding frontend actualizado con selección de rol
5. Migraciones V257-V259 + dominio `coaching/` completo (invitation + relationship + notification)
6. API endpoints de coaching + `AthleteProfileComposer`
7. Frontend: panel de coaching + sección "Mis maestros" en atleta + campana de notificaciones
