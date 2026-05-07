# Plan B — Backend PhysicalSession + WeeklyStats Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Añadir el bounded context `journal/physicalsession` con CRUD completo y un endpoint `GET /api/v1/dashboard/weekly-stats` que devuelve estadísticas combinadas de la semana actual.

**Architecture:** Sigue el patrón hexagonal existente (domain record → service → port → JPA adapter → controller), idéntico a `TrainingSession`. La migración Flyway V7 crea la tabla `physical_session`. El endpoint `weekly-stats` es un controller independiente en `dashboard/` que consulta ambos repositorios.

**Tech Stack:** Java 25, Spring Boot 4, Hibernate/JPA, SQLite, Lombok, Flyway, JUnit 5.

---

## Mapa de archivos

### Nuevos (backend)
| Archivo | Propósito |
|---------|-----------|
| `src/main/resources/db/migration/V7__init_physical_session.sql` | Tabla `physical_session` |
| `src/main/java/com/ossflow/journal/physicalsession/domain/PhysicalSession.java` | Record de dominio |
| `src/main/java/com/ossflow/journal/physicalsession/domain/PhysicalSessionType.java` | Enum |
| `src/main/java/com/ossflow/journal/physicalsession/application/port/PhysicalSessionRepositoryPort.java` | Puerto de repositorio |
| `src/main/java/com/ossflow/journal/physicalsession/application/PhysicalSessionService.java` | Servicio |
| `src/main/java/com/ossflow/journal/physicalsession/infrastructure/persistence/PhysicalSessionEntity.java` | Entidad JPA |
| `src/main/java/com/ossflow/journal/physicalsession/infrastructure/persistence/PhysicalSessionJpaRepository.java` | Spring Data JPA |
| `src/main/java/com/ossflow/journal/physicalsession/infrastructure/persistence/PhysicalSessionPersistenceMapper.java` | Mapper entity↔domain |
| `src/main/java/com/ossflow/journal/physicalsession/infrastructure/persistence/PhysicalSessionPersistenceAdapter.java` | Implementación del port |
| `src/main/java/com/ossflow/journal/physicalsession/infrastructure/web/dto/CreatePhysicalSessionRequest.java` | DTO de creación |
| `src/main/java/com/ossflow/journal/physicalsession/infrastructure/web/dto/PhysicalSessionResponse.java` | DTO de respuesta |
| `src/main/java/com/ossflow/journal/physicalsession/infrastructure/web/PhysicalSessionWebMapper.java` | Mapper DTO↔domain |
| `src/main/java/com/ossflow/journal/physicalsession/infrastructure/web/PhysicalSessionController.java` | REST controller |
| `src/main/java/com/ossflow/dashboard/infrastructure/web/WeeklyStatsController.java` | Endpoint stats |
| `src/main/java/com/ossflow/dashboard/infrastructure/web/dto/WeeklyStatsResponse.java` | DTO stats |
| `src/test/java/com/ossflow/journal/physicalsession/PhysicalSessionControllerTest.java` | Tests del controller |
| `src/test/java/com/ossflow/dashboard/WeeklyStatsControllerTest.java` | Tests del stats endpoint |

---

### Task 1: Migración Flyway V7

**Files:**
- Create: `src/main/resources/db/migration/V7__init_physical_session.sql`

- [ ] **Step 1: Crear la migración**

```sql
-- V7__init_physical_session.sql
CREATE TABLE physical_session (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    owner_id        INTEGER NOT NULL,
    session_date    TEXT    NOT NULL,
    session_type    TEXT    NOT NULL CHECK (session_type IN ('STRENGTH','CARDIO','FLEXIBILITY','HIIT','OTHER')),
    title           TEXT    NOT NULL,
    duration_minutes INTEGER,
    notes           TEXT,
    created_at      TEXT    NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ','now')),
    updated_at      TEXT    NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ','now')),
    version         INTEGER NOT NULL DEFAULT 0,
    deleted_at      TEXT,
    purge_at        TEXT
);

CREATE INDEX idx_physical_session_owner_date ON physical_session(owner_id, session_date DESC);
```

- [ ] **Step 2: Arrancar el backend para verificar que la migración aplica**

```bash
cd /Users/adrian/Programacion/repositorio/OssFlow
./mvnw spring-boot:run -q 2>&1 | grep -E "(Flyway|ERROR|Started)" | head -10
```

Expected: `Successfully applied 1 migration to schema ... (execution time ...)`

- [ ] **Step 3: Parar el backend** (Ctrl+C)

---

### Task 2: Dominio — PhysicalSessionType enum y PhysicalSession record

**Files:**
- Create: `src/main/java/com/ossflow/journal/physicalsession/domain/PhysicalSessionType.java`
- Create: `src/main/java/com/ossflow/journal/physicalsession/domain/PhysicalSession.java`

- [ ] **Step 1: Crear el enum**

```java
// com/ossflow/journal/physicalsession/domain/PhysicalSessionType.java
package com.ossflow.journal.physicalsession.domain;

public enum PhysicalSessionType {
    STRENGTH, CARDIO, FLEXIBILITY, HIIT, OTHER
}
```

- [ ] **Step 2: Crear el record de dominio**

```java
// com/ossflow/journal/physicalsession/domain/PhysicalSession.java
package com.ossflow.journal.physicalsession.domain;

import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;

@Builder(toBuilder = true)
public record PhysicalSession(
        Long id,
        Long ownerId,
        LocalDate sessionDate,
        PhysicalSessionType sessionType,
        String title,
        Integer durationMinutes,
        String notes,
        Instant createdAt,
        Instant updatedAt,
        Long version,
        Instant deletedAt,
        Instant purgeAt
) {}
```

- [ ] **Step 3: Verificar que compila**

```bash
cd /Users/adrian/Programacion/repositorio/OssFlow
./mvnw compile -q 2>&1 | tail -5
```

Expected: `BUILD SUCCESS`

---

### Task 3: Puerto de repositorio + Servicio

**Files:**
- Create: `src/main/java/com/ossflow/journal/physicalsession/application/port/PhysicalSessionRepositoryPort.java`
- Create: `src/main/java/com/ossflow/journal/physicalsession/application/PhysicalSessionService.java`

- [ ] **Step 1: Crear el puerto**

```java
// com/ossflow/journal/physicalsession/application/port/PhysicalSessionRepositoryPort.java
package com.ossflow.journal.physicalsession.application.port;

import com.ossflow.journal.physicalsession.domain.PhysicalSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface PhysicalSessionRepositoryPort {
    PhysicalSession save(PhysicalSession session);
    Optional<PhysicalSession> findById(Long id, Long ownerId);
    Page<PhysicalSession> findAll(Long ownerId, Pageable pageable);
    void softDelete(Long id, Long ownerId);
    long countByOwnerAndWeek(Long ownerId, LocalDate weekStart, LocalDate weekEnd);
}
```

- [ ] **Step 2: Crear el servicio**

```java
// com/ossflow/journal/physicalsession/application/PhysicalSessionService.java
package com.ossflow.journal.physicalsession.application;

import com.ossflow.journal.physicalsession.application.port.PhysicalSessionRepositoryPort;
import com.ossflow.journal.physicalsession.domain.PhysicalSession;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhysicalSessionService {

    private final PhysicalSessionRepositoryPort repository;

    public PhysicalSession create(PhysicalSession session) {
        PhysicalSession saved = repository.save(session);
        log.info("PhysicalSession creada id={}", saved.id());
        return saved;
    }

    public PhysicalSession findById(Long id, Long ownerId) {
        return repository.findById(id, ownerId)
                .orElseThrow(() -> new NotFoundException(
                        "PHYSICAL_SESSION_NOT_FOUND",
                        "No existe la sesión física con id %d".formatted(id),
                        Map.of("sessionId", id)));
    }

    public Page<PhysicalSession> list(Long ownerId, Pageable pageable) {
        return repository.findAll(ownerId, pageable);
    }

    public PhysicalSession replace(Long id, Long ownerId, PhysicalSession replacement) {
        PhysicalSession existing = findById(id, ownerId);
        return repository.save(replacement.toBuilder()
                .id(existing.id())
                .ownerId(existing.ownerId())
                .createdAt(existing.createdAt())
                .version(existing.version())
                .build());
    }

    public void softDelete(Long id, Long ownerId) {
        repository.softDelete(id, ownerId);
        log.info("PhysicalSession soft-deleted id={}", id);
    }

    public long countByWeek(Long ownerId, LocalDate weekStart, LocalDate weekEnd) {
        return repository.countByOwnerAndWeek(ownerId, weekStart, weekEnd);
    }
}
```

- [ ] **Step 3: Verificar que compila**

```bash
cd /Users/adrian/Programacion/repositorio/OssFlow
./mvnw compile -q 2>&1 | tail -5
```

Expected: `BUILD SUCCESS`

---

### Task 4: Capa de persistencia JPA

**Files:**
- Create: `src/main/java/com/ossflow/journal/physicalsession/infrastructure/persistence/PhysicalSessionEntity.java`
- Create: `src/main/java/com/ossflow/journal/physicalsession/infrastructure/persistence/PhysicalSessionJpaRepository.java`
- Create: `src/main/java/com/ossflow/journal/physicalsession/infrastructure/persistence/PhysicalSessionPersistenceMapper.java`
- Create: `src/main/java/com/ossflow/journal/physicalsession/infrastructure/persistence/PhysicalSessionPersistenceAdapter.java`

- [ ] **Step 1: Crear la entidad JPA**

```java
// com/ossflow/journal/physicalsession/infrastructure/persistence/PhysicalSessionEntity.java
package com.ossflow.journal.physicalsession.infrastructure.persistence;

import com.ossflow.journal.physicalsession.domain.PhysicalSessionType;
import com.ossflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "physical_session")
@SQLRestriction("deleted_at IS NULL")
public class PhysicalSessionEntity extends BaseEntity {

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false, length = 20)
    private PhysicalSessionType sessionType;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
```

- [ ] **Step 2: Crear el JPA Repository**

```java
// com/ossflow/journal/physicalsession/infrastructure/persistence/PhysicalSessionJpaRepository.java
package com.ossflow.journal.physicalsession.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface PhysicalSessionJpaRepository extends JpaRepository<PhysicalSessionEntity, Long> {

    Page<PhysicalSessionEntity> findByOwnerId(Long ownerId, Pageable pageable);

    Optional<PhysicalSessionEntity> findByIdAndOwnerId(Long id, Long ownerId);

    @Query("SELECT COUNT(p) FROM PhysicalSessionEntity p WHERE p.ownerId = :ownerId AND p.sessionDate >= :start AND p.sessionDate <= :end")
    long countByOwnerIdAndSessionDateBetween(
            @Param("ownerId") Long ownerId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}
```

- [ ] **Step 3: Crear el mapper de persistencia**

```java
// com/ossflow/journal/physicalsession/infrastructure/persistence/PhysicalSessionPersistenceMapper.java
package com.ossflow.journal.physicalsession.infrastructure.persistence;

import com.ossflow.journal.physicalsession.domain.PhysicalSession;
import org.springframework.stereotype.Component;

@Component
public class PhysicalSessionPersistenceMapper {

    public PhysicalSession toDomain(PhysicalSessionEntity e) {
        return PhysicalSession.builder()
                .id(e.getId())
                .ownerId(e.getOwnerId())
                .sessionDate(e.getSessionDate())
                .sessionType(e.getSessionType())
                .title(e.getTitle())
                .durationMinutes(e.getDurationMinutes())
                .notes(e.getNotes())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .version(e.getVersion())
                .deletedAt(e.getDeletedAt())
                .purgeAt(e.getPurgeAt())
                .build();
    }

    public PhysicalSessionEntity toEntity(PhysicalSession d) {
        PhysicalSessionEntity e = new PhysicalSessionEntity();
        if (d.id() != null) e.setId(d.id());
        e.setOwnerId(d.ownerId());
        e.setSessionDate(d.sessionDate());
        e.setSessionType(d.sessionType());
        e.setTitle(d.title());
        e.setDurationMinutes(d.durationMinutes());
        e.setNotes(d.notes());
        if (d.version() != null) e.setVersion(d.version());
        return e;
    }
}
```

- [ ] **Step 4: Crear el adapter de persistencia**

```java
// com/ossflow/journal/physicalsession/infrastructure/persistence/PhysicalSessionPersistenceAdapter.java
package com.ossflow.journal.physicalsession.infrastructure.persistence;

import com.ossflow.journal.physicalsession.application.port.PhysicalSessionRepositoryPort;
import com.ossflow.journal.physicalsession.domain.PhysicalSession;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PhysicalSessionPersistenceAdapter implements PhysicalSessionRepositoryPort {

    private final PhysicalSessionJpaRepository jpaRepository;
    private final PhysicalSessionPersistenceMapper mapper;

    @Override
    public PhysicalSession save(PhysicalSession session) {
        PhysicalSessionEntity entity = mapper.toEntity(session);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<PhysicalSession> findById(Long id, Long ownerId) {
        return jpaRepository.findByIdAndOwnerId(id, ownerId).map(mapper::toDomain);
    }

    @Override
    public Page<PhysicalSession> findAll(Long ownerId, Pageable pageable) {
        return jpaRepository.findByOwnerId(ownerId, pageable).map(mapper::toDomain);
    }

    @Override
    public void softDelete(Long id, Long ownerId) {
        PhysicalSessionEntity entity = jpaRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new NotFoundException(
                        "PHYSICAL_SESSION_NOT_FOUND",
                        "No existe la sesión física con id %d".formatted(id),
                        Map.of("sessionId", id)));
        entity.setDeletedAt(Instant.now());
        jpaRepository.save(entity);
    }

    @Override
    public long countByOwnerAndWeek(Long ownerId, LocalDate weekStart, LocalDate weekEnd) {
        return jpaRepository.countByOwnerIdAndSessionDateBetween(ownerId, weekStart, weekEnd);
    }
}
```

- [ ] **Step 5: Verificar que compila**

```bash
cd /Users/adrian/Programacion/repositorio/OssFlow
./mvnw compile -q 2>&1 | tail -5
```

Expected: `BUILD SUCCESS`

---

### Task 5: Capa web — DTOs + Mapper + Controller

**Files:**
- Create: `src/main/java/com/ossflow/journal/physicalsession/infrastructure/web/dto/CreatePhysicalSessionRequest.java`
- Create: `src/main/java/com/ossflow/journal/physicalsession/infrastructure/web/dto/PhysicalSessionResponse.java`
- Create: `src/main/java/com/ossflow/journal/physicalsession/infrastructure/web/PhysicalSessionWebMapper.java`
- Create: `src/main/java/com/ossflow/journal/physicalsession/infrastructure/web/PhysicalSessionController.java`

- [ ] **Step 1: Crear CreatePhysicalSessionRequest**

```java
// com/ossflow/journal/physicalsession/infrastructure/web/dto/CreatePhysicalSessionRequest.java
package com.ossflow.journal.physicalsession.infrastructure.web.dto;

import com.ossflow.journal.physicalsession.domain.PhysicalSessionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreatePhysicalSessionRequest(
        @NotNull LocalDate sessionDate,
        @NotNull PhysicalSessionType sessionType,
        @NotBlank @Size(max = 200) String title,
        @Positive Integer durationMinutes,
        @Size(max = 5000) String notes
) {}
```

- [ ] **Step 2: Crear PhysicalSessionResponse**

```java
// com/ossflow/journal/physicalsession/infrastructure/web/dto/PhysicalSessionResponse.java
package com.ossflow.journal.physicalsession.infrastructure.web.dto;

import com.ossflow.journal.physicalsession.domain.PhysicalSessionType;

import java.time.Instant;
import java.time.LocalDate;

public record PhysicalSessionResponse(
        Long id,
        LocalDate sessionDate,
        PhysicalSessionType sessionType,
        String title,
        Integer durationMinutes,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {}
```

- [ ] **Step 3: Crear PhysicalSessionWebMapper**

```java
// com/ossflow/journal/physicalsession/infrastructure/web/PhysicalSessionWebMapper.java
package com.ossflow.journal.physicalsession.infrastructure.web;

import com.ossflow.journal.physicalsession.domain.PhysicalSession;
import com.ossflow.journal.physicalsession.infrastructure.web.dto.CreatePhysicalSessionRequest;
import com.ossflow.journal.physicalsession.infrastructure.web.dto.PhysicalSessionResponse;
import org.springframework.stereotype.Component;

@Component
public class PhysicalSessionWebMapper {

    public PhysicalSession fromCreate(CreatePhysicalSessionRequest req) {
        return PhysicalSession.builder()
                .sessionDate(req.sessionDate())
                .sessionType(req.sessionType())
                .title(req.title())
                .durationMinutes(req.durationMinutes())
                .notes(req.notes())
                .build();
    }

    public PhysicalSessionResponse toResponse(PhysicalSession d) {
        return new PhysicalSessionResponse(
                d.id(),
                d.sessionDate(),
                d.sessionType(),
                d.title(),
                d.durationMinutes(),
                d.notes(),
                d.createdAt(),
                d.updatedAt()
        );
    }
}
```

- [ ] **Step 4: Crear PhysicalSessionController**

```java
// com/ossflow/journal/physicalsession/infrastructure/web/PhysicalSessionController.java
package com.ossflow.journal.physicalsession.infrastructure.web;

import com.ossflow.journal.physicalsession.application.PhysicalSessionService;
import com.ossflow.journal.physicalsession.domain.PhysicalSession;
import com.ossflow.journal.physicalsession.infrastructure.web.dto.CreatePhysicalSessionRequest;
import com.ossflow.journal.physicalsession.infrastructure.web.dto.PhysicalSessionResponse;
import com.ossflow.shared.web.CurrentOwner;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/journal/physical-sessions")
@Validated
@RequiredArgsConstructor
public class PhysicalSessionController {

    private final PhysicalSessionService service;
    private final PhysicalSessionWebMapper mapper;
    private final CurrentOwner currentOwner;

    @GetMapping
    public Page<PhysicalSessionResponse> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        return service.list(
                currentOwner.id(),
                PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "sessionDate"))
        ).map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    public PhysicalSessionResponse get(@PathVariable @Positive Long id) {
        return mapper.toResponse(service.findById(id, currentOwner.id()));
    }

    @PostMapping
    public ResponseEntity<PhysicalSessionResponse> create(@Valid @RequestBody CreatePhysicalSessionRequest req) {
        PhysicalSession created = service.create(
                mapper.fromCreate(req).toBuilder().ownerId(currentOwner.id()).build()
        );
        return ResponseEntity
                .created(URI.create("/api/v1/journal/physical-sessions/" + created.id()))
                .body(mapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public PhysicalSessionResponse replace(
            @PathVariable @Positive Long id,
            @Valid @RequestBody CreatePhysicalSessionRequest req) {
        return mapper.toResponse(
                service.replace(id, currentOwner.id(), mapper.fromCreate(req))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.softDelete(id, currentOwner.id());
        return ResponseEntity.noContent().build();
    }
}
```

- [ ] **Step 5: Verificar que compila**

```bash
cd /Users/adrian/Programacion/repositorio/OssFlow
./mvnw compile -q 2>&1 | tail -5
```

Expected: `BUILD SUCCESS`

---

### Task 6: Endpoint WeeklyStats

**Files:**
- Create: `src/main/java/com/ossflow/dashboard/infrastructure/web/dto/WeeklyStatsResponse.java`
- Create: `src/main/java/com/ossflow/dashboard/infrastructure/web/WeeklyStatsController.java`

- [ ] **Step 1: Crear WeeklyStatsResponse DTO**

```java
// com/ossflow/dashboard/infrastructure/web/dto/WeeklyStatsResponse.java
package com.ossflow.dashboard.infrastructure.web.dto;

import java.time.LocalDate;

public record WeeklyStatsResponse(
        int weekNumber,
        LocalDate weekStart,
        LocalDate weekEnd,
        long bjjSessions,
        long physicalSessions,
        int bjjGoal,
        int physicalGoal,
        long streakDays,
        long techniquesThisMonth
) {}
```

- [ ] **Step 2: Crear WeeklyStatsController**

```java
// com/ossflow/dashboard/infrastructure/web/WeeklyStatsController.java
package com.ossflow.dashboard.infrastructure.web;

import com.ossflow.dashboard.infrastructure.web.dto.WeeklyStatsResponse;
import com.ossflow.journal.physicalsession.application.PhysicalSessionService;
import com.ossflow.journal.trainingsession.application.TrainingSessionService;
import com.ossflow.shared.web.CurrentOwner;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class WeeklyStatsController {

    private static final int BJJ_GOAL = 4;
    private static final int PHYSICAL_GOAL = 3;

    private final TrainingSessionService trainingSessionService;
    private final PhysicalSessionService physicalSessionService;
    private final CurrentOwner currentOwner;

    @GetMapping("/weekly-stats")
    public WeeklyStatsResponse weeklyStats() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);
        int weekNumber = today.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        Long ownerId = currentOwner.id();

        long bjjSessions = trainingSessionService.countByWeek(ownerId, weekStart, weekEnd);
        long physicalSessions = physicalSessionService.countByWeek(ownerId, weekStart, weekEnd);

        return new WeeklyStatsResponse(
                weekNumber,
                weekStart,
                weekEnd,
                bjjSessions,
                physicalSessions,
                BJJ_GOAL,
                PHYSICAL_GOAL,
                0L,          // streak — fase 2
                0L           // techniquesThisMonth — fase 2
        );
    }
}
```

- [ ] **Step 3: Añadir `countByWeek` a `TrainingSessionService`**

En `src/main/java/com/ossflow/journal/trainingsession/application/TrainingSessionService.java`, añadir al final de la clase (antes del último `}`):

```java
public long countByWeek(Long ownerId, LocalDate weekStart, LocalDate weekEnd) {
    return repository.countByOwnerAndWeek(ownerId, weekStart, weekEnd);
}
```

- [ ] **Step 4: Añadir `countByOwnerAndWeek` a `TrainingSessionRepositoryPort`**

En `src/main/java/com/ossflow/journal/trainingsession/application/port/TrainingSessionRepositoryPort.java`, añadir:

```java
import java.time.LocalDate;

long countByOwnerAndWeek(Long ownerId, LocalDate weekStart, LocalDate weekEnd);
```

- [ ] **Step 5: Implementar en `TrainingSessionPersistenceAdapter`**

En `src/main/java/com/ossflow/journal/trainingsession/infrastructure/persistence/TrainingSessionPersistenceAdapter.java`, añadir el método:

```java
@Override
public long countByOwnerAndWeek(Long ownerId, LocalDate weekStart, LocalDate weekEnd) {
    return jpaRepository.countByOwnerIdAndSessionDateBetween(ownerId, weekStart, weekEnd);
}
```

Y en `TrainingSessionJpaRepository`, añadir:

```java
@Query("SELECT COUNT(t) FROM TrainingSessionEntity t WHERE t.ownerId = :ownerId AND t.sessionDate >= :start AND t.sessionDate <= :end")
long countByOwnerIdAndSessionDateBetween(
        @Param("ownerId") Long ownerId,
        @Param("start") LocalDate start,
        @Param("end") LocalDate end);
```

- [ ] **Step 6: Verificar que compila**

```bash
cd /Users/adrian/Programacion/repositorio/OssFlow
./mvnw compile -q 2>&1 | tail -5
```

Expected: `BUILD SUCCESS`

---

### Task 7: Tests del controller PhysicalSession

**Files:**
- Create: `src/test/java/com/ossflow/journal/physicalsession/PhysicalSessionControllerTest.java`

- [ ] **Step 1: Escribir el test**

```java
// src/test/java/com/ossflow/journal/physicalsession/PhysicalSessionControllerTest.java
package com.ossflow.journal.physicalsession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.journal.physicalsession.application.PhysicalSessionService;
import com.ossflow.journal.physicalsession.domain.PhysicalSession;
import com.ossflow.journal.physicalsession.domain.PhysicalSessionType;
import com.ossflow.journal.physicalsession.infrastructure.web.PhysicalSessionController;
import com.ossflow.journal.physicalsession.infrastructure.web.PhysicalSessionWebMapper;
import com.ossflow.journal.physicalsession.infrastructure.web.dto.CreatePhysicalSessionRequest;
import com.ossflow.shared.web.CurrentOwner;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PhysicalSessionControllerTest {

    private final PhysicalSessionService service = mock(PhysicalSessionService.class);
    private final PhysicalSessionWebMapper mapper = new PhysicalSessionWebMapper();
    private final CurrentOwner currentOwner = mock(CurrentOwner.class);
    private final MockMvc mvc = MockMvcBuilders
            .standaloneSetup(new PhysicalSessionController(service, mapper, currentOwner))
            .build();
    private final ObjectMapper json = new ObjectMapper().findAndRegisterModules();

    private PhysicalSession sample() {
        return PhysicalSession.builder()
                .id(1L)
                .ownerId(42L)
                .sessionDate(LocalDate.of(2026, 5, 7))
                .sessionType(PhysicalSessionType.STRENGTH)
                .title("Fuerza — Empuje")
                .durationMinutes(60)
                .notes(null)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .version(0L)
                .build();
    }

    @Test
    void list_returns_200() throws Exception {
        when(currentOwner.id()).thenReturn(42L);
        when(service.list(eq(42L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sample())));

        mvc.perform(get("/api/v1/journal/physical-sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Fuerza — Empuje"));
    }

    @Test
    void create_returns_201() throws Exception {
        when(currentOwner.id()).thenReturn(42L);
        when(service.create(any())).thenReturn(sample());

        CreatePhysicalSessionRequest req = new CreatePhysicalSessionRequest(
                LocalDate.of(2026, 5, 7),
                PhysicalSessionType.STRENGTH,
                "Fuerza — Empuje",
                60,
                null
        );

        mvc.perform(post("/api/v1/journal/physical-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionType").value("STRENGTH"));
    }

    @Test
    void delete_returns_204() throws Exception {
        when(currentOwner.id()).thenReturn(42L);

        mvc.perform(delete("/api/v1/journal/physical-sessions/1"))
                .andExpect(status().isNoContent());
    }
}
```

- [ ] **Step 2: Ejecutar el test**

```bash
cd /Users/adrian/Programacion/repositorio/OssFlow
./mvnw test -pl . -Dtest=PhysicalSessionControllerTest -q 2>&1 | tail -10
```

Expected: `Tests run: 3, Failures: 0, Errors: 0`

---

### Task 8: Test WeeklyStats + commit final

**Files:**
- Create: `src/test/java/com/ossflow/dashboard/WeeklyStatsControllerTest.java`

- [ ] **Step 1: Escribir el test**

```java
// src/test/java/com/ossflow/dashboard/WeeklyStatsControllerTest.java
package com.ossflow.dashboard;

import com.ossflow.dashboard.infrastructure.web.WeeklyStatsController;
import com.ossflow.journal.physicalsession.application.PhysicalSessionService;
import com.ossflow.journal.trainingsession.application.TrainingSessionService;
import com.ossflow.shared.web.CurrentOwner;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WeeklyStatsControllerTest {

    private final TrainingSessionService trainingService = mock(TrainingSessionService.class);
    private final PhysicalSessionService physicalService = mock(PhysicalSessionService.class);
    private final CurrentOwner currentOwner = mock(CurrentOwner.class);
    private final MockMvc mvc = MockMvcBuilders
            .standaloneSetup(new WeeklyStatsController(trainingService, physicalService, currentOwner))
            .build();

    @Test
    void weekly_stats_returns_200_with_goals() throws Exception {
        when(currentOwner.id()).thenReturn(1L);
        when(trainingService.countByWeek(eq(1L), any(LocalDate.class), any(LocalDate.class))).thenReturn(3L);
        when(physicalService.countByWeek(eq(1L), any(LocalDate.class), any(LocalDate.class))).thenReturn(2L);

        mvc.perform(get("/api/v1/dashboard/weekly-stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bjjSessions").value(3))
                .andExpect(jsonPath("$.physicalSessions").value(2))
                .andExpect(jsonPath("$.bjjGoal").value(4))
                .andExpect(jsonPath("$.physicalGoal").value(3));
    }
}
```

- [ ] **Step 2: Ejecutar todos los tests**

```bash
cd /Users/adrian/Programacion/repositorio/OssFlow
./mvnw test -q 2>&1 | tail -15
```

Expected: todos los tests pasan

- [ ] **Step 3: Commit**

```bash
cd /Users/adrian/Programacion/repositorio/OssFlow
git add src/
git commit -m "feat: añadir PhysicalSession CRUD + endpoint weekly-stats dashboard"
```
