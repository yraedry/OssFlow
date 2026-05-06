# OssFlow — Rediseño completo: spec técnico

| Campo | Valor |
|---|---|
| Proyecto | OssFlow — Segundo cerebro para BJJ |
| Autor | Adrian Nuñez Sanchez |
| Curso | Desarrollo de Aplicaciones Multiplataforma (DAM) |
| Fecha del diseño | 2026-05-06 |
| Estado | Aprobado por usuario, pendiente de implementación |
| Anexos | [coding-rules.md](./coding-rules.md), [error-codes.md](./error-codes.md) |

---

## Resumen ejecutivo

OssFlow es una aplicación web monousuario (con modelo de datos preparado para futuro multiusuario) que sirve como **segundo cerebro técnico** para practicantes de Brazilian Jiu-Jitsu. Combina:

- Un **catálogo grafo-relacional** de posiciones, técnicas y sistemas (árboles de decisión).
- Un **diario personal** (notas estilo Obsidian, sesiones de entrenamiento, log de competiciones).
- Un **plan de estudio jerárquico** a meses vista (plan → bloques → ítems con state machine).
- **Identidad de usuario** con cinturón actual y federaciones preferidas.
- Avisos de **legalidad técnica** según federación y cinturón (warning, no bloqueo).
- **Importación y exportación** masiva en JSON con validación por schema.

El stack es Spring Boot 4 (Java 25) + SQLite + React/Vite + Cloudflare Tunnel sobre Proxmox para la futura puesta en producción.

---

## 1. Arquitectura general

### 1.1 Topología del sistema

```
┌──────────────────────────────────────────────────────────────┐
│                   Cloudflare Tunnel                           │
│              (cloudflared en Proxmox, opcional)               │
└────────────────────────┬─────────────────────────────────────┘
                         │  HTTPS público
                         ▼
              ┌───────────────────────┐
              │  Nginx (frontend)     │  contenedor Docker
              │  · sirve estáticos    │
              │  · proxy /api → :8080 │
              └──────────┬────────────┘
                         │ HTTP red interna
                         ▼
              ┌───────────────────────┐
              │  Spring Boot 4.x      │  contenedor Docker
              │  Java 25              │
              │  · API REST /api/v1   │
              │  · Actuator /actuator │
              │  · Flyway en arranque │
              └──────────┬────────────┘
                         │ JDBC
                         ▼
              ┌───────────────────────┐
              │  SQLite (volumen)     │  ./data/ossflow.db (prod)
              │  H2 in-memory (dev)   │
              └───────────────────────┘
```

### 1.2 Bounded contexts

El backend es un **monolito modular**, no microservicios. Se divide en cinco contextos dentro del mismo módulo Maven:

| Contexto | Contenido | Justificación |
|---|---|---|
| `catalog` | Position, Technique, System, Federation, Ruleset, RulesetTechnique, importadores | Conocimiento objetivo del BJJ; potencialmente compartible |
| `journal` | Note, Tag, NoteTag, TrainingSession, TrainingSessionTechnique, CompetitionLog, CompetitionMatch | Contenido subjetivo del usuario |
| `planning` | StudyPlan, StudyBlock, StudyItem, StudyItemStateMachine | Mirada hacia el futuro: qué quiere trabajar |
| `identity` | UserProfile, UserProfileFederation | Datos de identidad del usuario; futuro home de auth |
| `shared` | Config, GlobalExceptionHandler, RequestTracingFilter, JSON schema validator, BaseEntity, scheduled jobs | Infraestructura transversal sin lógica de negocio |

Cada contexto **gestiona su propia portabilidad** (importadores y exportadores propios). El endpoint `GET /api/v1/export/full` actúa como orquestador llamando a los exporters de cada contexto.

### 1.3 Regla de dependencias entre contextos

```
shared       (sin dependencias de negocio)
   ▲
   │
catalog      (no depende de nadie del negocio)
   ▲
   │
journal      (puede referenciar catalog: una nota habla de una técnica)
planning     (puede referenciar catalog: un item apunta a una técnica)
identity     (independiente; el resto referencia identity por ownerId)
```

Las flechas van en una sola dirección. Se prohíben ciclos.

### 1.4 Capas dentro de cada feature (hexagonal-lite)

```
catalog/position/
├─ domain/                            POJOs/records puros, sin Spring/JPA
│   ├─ Position.java
│   └─ PositionType.java (enum)
├─ application/
│   ├─ PositionService.java           clase concreta, SIN interfaz UseCase
│   └─ port/
│       └─ PositionRepositoryPort.java  interfaz (puerto de salida)
└─ infrastructure/
    ├─ web/
    │   ├─ PositionController.java
    │   ├─ PositionWebMapper.java     MapStruct
    │   └─ dto/
    │       ├─ CreatePositionRequest.java
    │       ├─ UpdatePositionRequest.java
    │       ├─ PatchPositionRequest.java
    │       └─ PositionResponse.java
    └─ persistence/
        ├─ PositionEntity.java        @Entity (JPA)
        ├─ PositionJpaRepository.java extends JpaRepository
        ├─ PositionPersistenceAdapter.java   implementa PositionRepositoryPort
        └─ PositionPersistenceMapper.java    MapStruct
```

**Decisión arquitectónica clave**: hexagonal-lite mantiene puertos solo de salida (persistencia), elimina los puertos de entrada (`*UseCase`), y los servicios son clases concretas. El razonamiento es que los puertos de entrada con un único implementador y un único consumidor son ceremonia sin pago. Esto se desvía del hexagonal puro original del proyecto pero conserva la inversión de dependencia donde sí aporta: testabilidad de persistencia y posibilidad de cambiar de motor sin tocar dominio o servicios.

### 1.5 Patrones de diseño aplicados

| Patrón | Categoría | Dónde | Justificación |
|---|---|---|---|
| **Strategy** | Comportamiento | `Importer<T>` + `CatalogImporter`, `SystemImporter`, `RulesetImporter` | OCP: nuevo formato = nueva estrategia, sin tocar las existentes |
| **Chain of Responsibility** | Comportamiento | Pipeline de validación de JSON (schema → semantic → referential) | SRP por validador, fail-fast, fácil de extender |
| **Template Method** | Comportamiento | `AbstractImporter` con `validate→parse→checkDuplicates→persist→buildReport` | DRY entre importadores |
| **State** | Comportamiento | `StudyItemStateMachine` para `StudyItem.status` | Invariantes de dominio: no permite transiciones inválidas |
| **Observer** | Comportamiento | Spring `ApplicationEvent` (`StudyItemStatusChangedEvent`) | Gancho preparado para futuros listeners (IA, notificaciones) |
| **Builder** | Creacional | Lombok `@Builder` en records de dominio | Construcción legible sin telescoping constructors |
| **Singleton** | Creacional | Implícito en Spring (`@Service`, `@Component`) | Estándar del framework, no se aplica a mano |

**Patrones descartados con justificación:**

- **Circuit Breaker** (Resilience4j): no hay servicios externos inestables. Se incorporará cuando se integre IA externa.
- **Memento**: el undo/redo del editor visual de flow lo gestiona React Flow en cliente.
- **Mediator, Iterator, Visitor, Command**: no resuelven problemas reales del proyecto actual.

---

## 2. Modelo de datos

### 2.1 Convenciones globales

- **`BaseEntity`** abstracta común a todas: `id` BIGINT, `ownerId` BIGINT (default 1), `createdAt`, `updatedAt`, `version` (optimistic locking), `deletedAt` nullable, `purgeAt` nullable.
- **Soft delete** mediante `deletedAt`. Operaciones `DELETE` ponen el timestamp; `@Where(clause = "deleted_at IS NULL")` filtra por defecto.
- **Ventana de recuperación 30 días**: `purgeAt = deletedAt + 30 días`. Después → hard delete por job programado.
- **Auditoría JPA** con `@CreatedDate` / `@LastModifiedDate` automática.
- **Naming SQL**: `snake_case` en tablas/columnas; `camelCase` en Java; `PhysicalNamingStrategy` configurada.
- **Índices** explícitos en columnas de búsqueda frecuente.

### 2.2 Tablas — contexto `catalog`

#### `position`

| Columna | Tipo | Notas |
|---|---|---|
| `id` | BIGINT PK | autoincrement |
| `name` | VARCHAR(120) | UNIQUE per owner (`(owner_id, name)` WHERE `deleted_at IS NULL`) |
| `type` | VARCHAR(30) | enum `PositionType`: TOP / BOTTOM / STANDING / GROUND_NEUTRAL / SUBMITTED |
| `description` | TEXT | nullable, markdown |
| `visibility` | VARCHAR(10) | enum: PRIVATE / PUBLIC, default PRIVATE |
| `owner_id`, `created_at`, `updated_at`, `version`, `deleted_at`, `purge_at` | base |

#### `technique`

| Columna | Tipo | Notas |
|---|---|---|
| `id` | BIGINT PK | |
| `name` | VARCHAR(120) | UNIQUE per owner |
| `category` | VARCHAR(30) | enum `TechniqueCategory`: SUBMISSION / SWEEP / PASS / TAKEDOWN / ESCAPE / TRANSITION |
| `description` | TEXT | markdown |
| `youtube_url` | VARCHAR(500) | nullable, validado con regex |
| `minimum_belt` | VARCHAR(15) | enum `Belt`: WHITE / BLUE / PURPLE / BROWN / BLACK |
| `modality` | VARCHAR(10) | enum `Modality`: GI / NOGI / BOTH |
| `start_position_id` | BIGINT FK → position | NOT NULL |
| `end_position_id` | BIGINT FK → position | nullable |
| `visibility`, base fields | | |

Índices: `(owner_id, name)`, `(start_position_id)`, `(end_position_id)`, `(category)`.

#### `system`

| Columna | Tipo | Notas |
|---|---|---|
| `id` | BIGINT PK | |
| `name` | VARCHAR(120) | UNIQUE per owner |
| `description` | TEXT | markdown |
| `anchor_position_id` | BIGINT FK → position | nullable, posición central |
| `flow_definition` | TEXT (JSON) | validado contra `system-flow.schema.v1.json` |
| `flow_schema_version` | VARCHAR(10) | default `"v1"` |
| `visibility`, base fields | | |

#### `federation`

| Columna | Tipo | Notas |
|---|---|---|
| `id` | BIGINT PK | |
| `code` | VARCHAR(20) | UNIQUE: `IBJJF`, `ADCC`, `AJP`, `NAGA`, `UAEJJF`, `FEJJB`, `AEJJ`, `SBJJ`, `CBJJE`, `GI` |
| `name` | VARCHAR(120) | |
| `official_url` | VARCHAR(500) | |

#### `ruleset`

| Columna | Tipo | Notas |
|---|---|---|
| `id` | BIGINT PK | |
| `federation_id` | BIGINT FK | |
| `belt` | VARCHAR(15) | enum Belt |
| `modality` | VARCHAR(10) | enum Modality |
| `effective_from` | DATE | |
| `effective_to` | DATE | nullable (null = vigente) |
| `source_url` | VARCHAR(500) | |

UNIQUE `(federation_id, belt, modality, effective_from)`.

#### `ruleset_technique`

| Columna | Tipo | Notas |
|---|---|---|
| `ruleset_id` | BIGINT FK | CASCADE DELETE |
| `technique_id` | BIGINT FK | RESTRICT |
| `status` | VARCHAR(20) | ALLOWED / PROHIBITED / CONDITIONAL |
| `condition_notes` | TEXT | markdown |

PK `(ruleset_id, technique_id)`.

### 2.3 Tablas — contexto `journal`

#### `note`

| Columna | Tipo | Notas |
|---|---|---|
| `id` | BIGINT PK | |
| `title` | VARCHAR(200) | |
| `body_markdown` | TEXT | |
| `target_type` | VARCHAR(20) | nullable, enum: POSITION / TECHNIQUE / SYSTEM / NONE |
| `target_id` | BIGINT | nullable, FK soft (target_type lo determina) |
| base fields | | |

Índices: `(owner_id, target_type, target_id)`, `(owner_id, created_at)`.

#### `tag` (global, sin `owner_id`)

| Columna | Tipo | Notas |
|---|---|---|
| `id` | BIGINT PK | |
| `name` | VARCHAR(60) | UNIQUE global |
| `created_at` | TIMESTAMP | |

Decisión: tags compartidos entre todos los usuarios para evitar duplicación. Job nocturno limpia tags huérfanos.

#### `note_tag` (pivot)

| Columna | Tipo | Notas |
|---|---|---|
| `note_id` | BIGINT FK | CASCADE DELETE |
| `tag_id` | BIGINT FK | CASCADE DELETE |

PK `(note_id, tag_id)`.

#### `training_session`

| Columna | Tipo | Notas |
|---|---|---|
| `id` | BIGINT PK | |
| `session_date` | DATE | |
| `duration_minutes` | INT | |
| `location` | VARCHAR(120) | nullable |
| `intensity` | VARCHAR(15) | enum: LOW / MEDIUM / HIGH / SPARRING |
| `notes_markdown` | TEXT | |
| base fields | | |

Índice: `(owner_id, session_date DESC)`.

#### `training_session_technique`

| Columna | Tipo | Notas |
|---|---|---|
| `training_session_id` | BIGINT FK | CASCADE DELETE |
| `technique_id` | BIGINT FK | RESTRICT |
| `rep_count` | INT | nullable |
| `notes_markdown` | TEXT | nullable |

PK `(training_session_id, technique_id)`.

#### `competition_log`

| Columna | Tipo | Notas |
|---|---|---|
| `id` | BIGINT PK | |
| `event_name` | VARCHAR(200) | |
| `event_date` | DATE | |
| `weight_category` | VARCHAR(30) | |
| `total_matches` | INT | |
| `result` | VARCHAR(15) | enum: GOLD / SILVER / BRONZE / OUT_QUARTERS / OUT_FIRST / DNS |
| `analysis_markdown` | TEXT | |
| base fields | | |

#### `competition_match`

| Columna | Tipo | Notas |
|---|---|---|
| `id` | BIGINT PK | |
| `competition_log_id` | BIGINT FK | CASCADE DELETE |
| `match_order` | INT | UNIQUE per competition_log |
| `opponent_name` | VARCHAR(120) | |
| `opponent_team` | VARCHAR(120) | nullable |
| `outcome` | VARCHAR(15) | enum: WIN / LOSS / DRAW / DQ |
| `method` | VARCHAR(30) | enum: POINTS / SUBMISSION / ADVANTAGE / REFEREE / DQ |
| `submission_technique_id` | BIGINT FK → technique | nullable |
| `notes_markdown` | TEXT | |

### 2.4 Tablas — contexto `planning`

#### `study_plan`

| Columna | Tipo | Notas |
|---|---|---|
| `id` | BIGINT PK | |
| `title` | VARCHAR(200) | |
| `goal_markdown` | TEXT | |
| `start_date`, `end_date` | DATE | |
| `status` | VARCHAR(15) | enum: DRAFT / ACTIVE / COMPLETED / ARCHIVED |
| base fields | | |

#### `study_block`

| Columna | Tipo | Notas |
|---|---|---|
| `id` | BIGINT PK | |
| `study_plan_id` | BIGINT FK | CASCADE DELETE |
| `title` | VARCHAR(200) | |
| `start_date`, `end_date` | DATE | |
| `block_order` | INT | |
| `notes_markdown` | TEXT | |
| `focus_entities` | TEXT (JSON) | excepción JSON: array polimórfico de `{type, id}` |

Decisión: `focus_entities` es la única columna JSON del modelo, justificada por su naturaleza polimórfica hacia tres tipos distintos, baja cardinalidad por block, y ausencia de joins sobre ella.

#### `study_item`

| Columna | Tipo | Notas |
|---|---|---|
| `id` | BIGINT PK | |
| `study_block_id` | BIGINT FK | CASCADE DELETE |
| `description` | VARCHAR(500) | |
| `status` | VARCHAR(15) | enum: TODO / DOING / DONE / SKIPPED — gobernado por State |
| `target_type` | VARCHAR(20) | nullable: POSITION / TECHNIQUE / SYSTEM |
| `target_id` | BIGINT | nullable |
| `due_date` | DATE | nullable |
| `ai_generated` | BOOLEAN | default false |
| `completed_at` | TIMESTAMP | nullable, set al pasar a DONE |
| base fields | | |

#### State machine de `study_item.status`

```
       TODO ──→ DOING ──→ DONE
        │         │         │
        ├────────→├────→ SKIPPED   (reabrir SKIPPED → TODO)
        │                  │
        └─→ DONE (atajo)   ↓
                         TODO

Transiciones permitidas:
TODO    → DOING, DONE, SKIPPED
DOING   → TODO, DONE, SKIPPED
DONE    → TODO  (reabrir)
SKIPPED → TODO  (reabrir)
```

Cualquier otra transición devuelve 409 con código `INVALID_STATE_TRANSITION`.

### 2.5 Tablas — contexto `identity`

#### `user_profile`

| Columna | Tipo | Notas |
|---|---|---|
| `id` | BIGINT PK | |
| `owner_id` | BIGINT | UNIQUE; en mono = 1 |
| `display_name` | VARCHAR(120) | |
| `current_belt` | VARCHAR(15) | enum Belt |
| `belt_since` | DATE | |
| `academy` | VARCHAR(200) | nullable |
| `preferred_modality` | VARCHAR(10) | GI / NOGI / BOTH |
| `onboarding_completed` | BOOLEAN | default false |
| base fields | | |

#### `user_profile_federation`

| Columna | Tipo | Notas |
|---|---|---|
| `user_profile_id` | BIGINT FK | CASCADE DELETE |
| `federation_id` | BIGINT FK | RESTRICT |
| `is_primary` | BOOLEAN | máximo una TRUE por user_profile (constraint) |

PK `(user_profile_id, federation_id)`.

### 2.6 Resumen de tablas

Total: **18 tablas**.

- catalog: 6 (position, technique, system, federation, ruleset, ruleset_technique)
- journal: 7 (note, tag, note_tag, training_session, training_session_technique, competition_log, competition_match)
- planning: 3 (study_plan, study_block, study_item)
- identity: 2 (user_profile, user_profile_federation)

### 2.7 JSON Schema `system-flow.schema.v1.json`

```jsonc
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "type": "object",
  "required": ["nodes", "edges"],
  "properties": {
    "nodes": {
      "type": "array",
      "items": {
        "type": "object",
        "required": ["id", "kind", "refId"],
        "properties": {
          "id": { "type": "string" },
          "kind": { "enum": ["POSITION", "TECHNIQUE"] },
          "refId": { "type": "integer" },
          "label": { "type": "string" },
          "x": { "type": "number" },
          "y": { "type": "number" }
        }
      }
    },
    "edges": {
      "type": "array",
      "items": {
        "type": "object",
        "required": ["from", "to", "trigger"],
        "properties": {
          "from": { "type": "string" },
          "to": { "type": "string" },
          "trigger": { "enum": ["ATTACK", "DEFENSE", "PASS", "ESCAPE", "TRANSITION"] },
          "condition": { "type": "string" }
        }
      }
    }
  }
}
```

### 2.8 Soft delete y job de purga

- `DELETE /api/v1/{recurso}/{id}` → `deletedAt = now()`, `purgeAt = now() + 30 días`.
- `POST /api/v1/{recurso}/{id}/restore` → válido si `now() < purgeAt`. Resetea ambos a null.
- `GET /api/v1/{contexto}/trash` → lista entidades soft-deleted del contexto con `purgeAt` para mostrar "quedan X días".
- Job `@Scheduled(cron = "0 0 3 * * *")` → cada día a las 3:00 ejecuta `DELETE FROM <tabla> WHERE purge_at < now()` sobre todas las tablas con BaseEntity. Si hay referencias activas, salta la fila y emite warning en logs; reintenta al día siguiente.
- Endpoint `DELETE /api/v1/{recurso}/{id}/hard` para purga manual inmediata (admin/debug).

---

## 3. API REST

### 3.1 Convenciones globales

- Base path: `/api/v1`.
- Versionado en URL.
- JSON; fechas ISO-8601.
- Paginación: `?page=0&size=20&sort=name,asc` (Spring Data Page).
- Filtros: query params explícitos por endpoint.
- Códigos HTTP:
  - 200 — GET / PUT / restore exitosos
  - 201 — POST exitoso, con header `Location`
  - 204 — DELETE exitoso
  - 400 — validación DTO falló o JSON malformado
  - 404 — recurso no existe
  - 409 — UNIQUE violado, transición de estado inválida, FK violada al borrar
  - 422 — JSON sintácticamente válido pero semánticamente inválido (schema falla)
  - 500 — error inesperado, con `traceId`

### 3.2 Estructura de URLs

```
/api/v1/catalog/positions
/api/v1/catalog/techniques
/api/v1/catalog/systems
/api/v1/catalog/federations
/api/v1/catalog/rulesets
/api/v1/catalog/rulesets/{id}/techniques
/api/v1/catalog/import/catalog          import masivo positions+techniques
/api/v1/catalog/import/system           import de un system con su flow
/api/v1/catalog/import/rulesets         import de rulesets
/api/v1/catalog/trash

/api/v1/journal/notes
/api/v1/journal/tags
/api/v1/journal/training-sessions
/api/v1/journal/training-sessions/{id}/worked-techniques
/api/v1/journal/competition-logs
/api/v1/journal/competition-logs/{id}/matches
/api/v1/journal/trash

/api/v1/planning/study-plans
/api/v1/planning/study-plans/{id}/blocks
/api/v1/planning/study-plans/{id}/blocks/{bid}/items
/api/v1/planning/study-plans/{id}/blocks/{bid}/items/{iid}/transition
/api/v1/planning/trash

/api/v1/identity/profile
/api/v1/identity/profile/federations

/api/v1/export/full

/actuator/health, /actuator/info
/swagger-ui.html (solo perfil dev/local)
```

### 3.3 Operaciones CRUD por entidad (resumen)

Todas las entidades soportan: GET list (paginado y filtrable), GET by id, POST, PUT (reemplazo completo), PATCH (Merge Patch parcial), DELETE (soft), POST `/restore`. Detalles completos en sección 3.4 al 3.10.

### 3.4 catalog/positions

| Método | URL | Códigos |
|---|---|---|
| GET | /positions?name=guard&type=BOTTOM&page=0&size=20 | 200 |
| GET | /positions/{id} | 200, 404 |
| POST | /positions | 201, 400, 409 |
| PUT | /positions/{id} | 200, 400, 404, 409 |
| PATCH | /positions/{id} | 200, 400, 404, 409 |
| DELETE | /positions/{id} | 204, 404 |
| POST | /positions/{id}/restore | 200, 404, 409 |

### 3.5 catalog/techniques

Filtros: `?name=&category=&belt=&modality=&startPositionId=&endPositionId=`. Resto idéntico al patrón anterior.

### 3.6 catalog/systems

POST y PUT validan `flowDefinition` con pipeline Chain of Responsibility:

1. **FlowSchemaValidationStep**: estructura JSON contra schema v1.
2. **FlowSemanticValidationStep**: nodos sin huérfanos, edges referencian nodos existentes, no ciclos sin trigger.
3. **FlowReferentialValidationStep**: cada `refId` existe en position/technique y no está soft-deleted.

Devuelve 422 con código específico si falla cualquier paso.

### 3.7 catalog/import

| Método | URL | Body | Mode |
|---|---|---|---|
| POST | /catalog/import/catalog | CatalogImportPayload | ?mode=MERGE / REPLACE |
| POST | /catalog/import/system | SystemImportPayload | |
| POST | /catalog/import/rulesets | RulesetImportPayload | |

Respuesta `ImportReport` con summary (creados, ignorados, warnings, errors) y `createdEntities`. Transaccional `@Transactional`: o se aplica todo o nada.

### 3.8 journal/notes con tags y target

`CreateNoteRequest`:

```json
{
  "title": "Detalles kimura desde guardia cerrada",
  "bodyMarkdown": "...",
  "tags": ["guardia-cerrada", "submission"],
  "target": { "type": "TECHNIQUE", "id": 12 }
}
```

El servicio busca tags existentes por nombre, crea los que no existen (tabla global `tag`), y enlaza vía `note_tag`.

### 3.9 planning con state machine

Cambio de estado de `StudyItem` es endpoint dedicado:

```
POST /study-plans/{id}/blocks/{bid}/items/{iid}/transition
Body: { "targetStatus": "DOING" }
```

Razones para no usar PATCH:
- Hace explícita la operación de dominio.
- 409 con `INVALID_STATE_TRANSITION` si la state machine lo rechaza, con `details.allowed` listando transiciones válidas.
- Dispara `StudyItemStatusChangedEvent` (Observer).

### 3.10 identity/profile

| Método | URL | Códigos |
|---|---|---|
| GET | /profile | 200, 404 (no onboarded) |
| POST | /profile | 201, 409 (ya existe) |
| PUT | /profile | 200, 400, 404 |
| PATCH | /profile | 200, 400, 404 |
| PUT | /profile/federations | 200, 422 (multiple primary, no primary) |

### 3.11 export/full

```
GET /api/v1/export/full
Content-Disposition: attachment; filename="ossflow-backup-2026-05-06.json"
```

JSON con secciones `catalog`, `journal`, `planning`, `identity`. Streaming: no carga todo en memoria.

### 3.12 Documentación OpenAPI

- **springdoc-openapi** expone Swagger UI en `/swagger-ui.html` (solo perfil `dev`/`local`).
- OpenAPI 3 spec en `/v3/api-docs`.
- Cada controller anotado con `@Operation`, `@ApiResponse`, `@Tag`.
- En releases del backend, el `openapi.json` se publica como artifact de GitHub Release. El frontend lo consume para generar tipos TypeScript.

---

## 4. Manejo de errores y validación

### 4.1 Filosofía

Tres capas de validación, cada una con su responsabilidad:

```
1. DTO/Bean Validation (jakarta.validation)         → 400
2. Validación de dominio / state machine            → 409
3. Validación semántica (Chain of Responsibility)   → 422
```

Cada error sale del lugar correcto. No se mezclan.

### 4.2 Jerarquía de excepciones

```
OssFlowException (abstract)
├─ getErrorCode(): String
├─ getHttpStatus(): HttpStatus
└─ getDetails(): Map<String, Object>

NotFoundException                → 404
ConflictException                → 409
   ├─ DuplicateNameException
   ├─ ReferenceInUseException
   └─ InvalidStateTransitionException
UnprocessableException           → 422
   ├─ JsonSchemaViolationException
   ├─ SemanticValidationException
   └─ ReferentialIntegrityException
BadRequestException              → 400
```

### 4.3 GlobalExceptionHandler

Una sola clase `shared/exception/GlobalExceptionHandler.java` con `@RestControllerAdvice`:

- `OssFlowException` → mapea según subclase, log nivel `warn`.
- `MethodArgumentNotValidException` → 400 con `fieldErrors[]`.
- `HttpMessageNotReadableException` / `MethodArgumentTypeMismatchException` → 400.
- `DataIntegrityViolationException` → 409 con código traducido (constraint UNIQUE).
- `Exception` (catch-all) → 500 con `traceId`. Log nivel `error` con stack trace completo.

**El cliente nunca recibe stack traces**.

### 4.4 ApiError (respuesta uniforme)

```java
record ApiError(
    Instant timestamp,
    int status,
    String error,
    String code,
    String message,
    String path,
    String traceId,
    List<FieldError> fieldErrors,
    Map<String, Object> details
) {
    record FieldError(String field, Object rejectedValue, String message) {}
}
```

Tabla de códigos completa en [error-codes.md](./error-codes.md).

### 4.5 Bean Validation

DTOs en `infrastructure/web/dto/` con `jakarta.validation`:

```java
record CreatePositionRequest(
    @NotBlank @Size(max = 120) String name,
    @NotNull PositionType type,
    @Size(max = 10000) String description,
    @NotNull Visibility visibility
) {}
```

Mensajes en `ValidationMessages.properties` en español:

```properties
jakarta.validation.constraints.NotBlank.message=no puede estar vacío
jakarta.validation.constraints.Size.message=debe tener entre {min} y {max} caracteres
```

**Política A+C**: el frontend reacciona por `code` (estable). El `message` es fallback humano. El frontend traduce por código con react-i18next; usa `message` solo si no tiene traducción para ese código.

### 4.6 Pipeline de validación con Chain of Responsibility

```java
shared/validation/
├─ ValidationStep.java
├─ ValidationChain.java
├─ ValidationContext.java
└─ ValidationResult.java
```

Diagrama:

```
payload → [Schema] →ok→ [Semantic] →ok→ [Referential] →ok→ persist
            │ fail         │ fail            │ fail
            ▼              ▼                 ▼
         422 con código y detalles del primer fallo
```

Fail-fast. El detalle viaja en `ApiError.details`:

```json
{
  "status": 422,
  "code": "SYSTEM_FLOW_SCHEMA_INVALID",
  "details": {
    "validatorStep": "FlowSchemaValidationStep",
    "violations": [
      { "path": "/edges/2/trigger", "message": "valor no permitido: 'KIMURA'" }
    ]
  }
}
```

### 4.7 State machine como bean

```java
@Component
class StudyItemStateMachine {
    private static final Map<StudyItemStatus, Set<StudyItemStatus>> ALLOWED = Map.of(
        TODO,    Set.of(DOING, DONE, SKIPPED),
        DOING,   Set.of(TODO, DONE, SKIPPED),
        DONE,    Set.of(TODO),
        SKIPPED, Set.of(TODO)
    );

    void assertTransition(StudyItemStatus from, StudyItemStatus to) {
        if (!ALLOWED.getOrDefault(from, Set.of()).contains(to)) {
            throw new InvalidStateTransitionException(
                "INVALID_STATE_TRANSITION",
                "No se puede pasar de %s a %s".formatted(from, to),
                Map.of("from", from, "to", to, "allowed", ALLOWED.get(from))
            );
        }
    }
}
```

### 4.8 traceId y MDC

`shared/web/RequestTracingFilter.java` (`OncePerRequestFilter`):

- Lee header `X-Trace-Id` si viene del cliente; si no, genera UUID.
- Pone en MDC: `MDC.put("traceId", traceId)`.
- Devuelve el mismo header en la respuesta.
- Limpia MDC al final.

Logback pattern incluye `[traceId=%X{traceId}]`. Cualquier línea de log permite correlacionar todo un request. Compatible con OpenTelemetry/Jaeger en el futuro: basta cambiar el filtro por uno de OTel y los traceIds pasan a ser distribuidos.

### 4.9 Política de logging

| Severidad | Cuándo |
|---|---|
| `debug` | Información de flujo en happy path. Solo dev. |
| `info` | Operaciones de negocio relevantes. |
| `warn` | Errores 4xx (validación, conflicto, no encontrado). |
| `error` | Solo 5xx y excepciones inesperadas, con stack trace. |

Formato Logback:
- **Dev/local**: pattern legible con colores.
- **Prod**: JSON estructurado (`logstash-logback-encoder`) para futura integración Loki/ELK sin cambios.

---

## 5. Testing

### 5.1 Pirámide del proyecto

```
        ┌─────────────────────────┐
        │   E2E (Playwright)      │   FUERA DE ALCANCE
        ├─────────────────────────┤
        │   Integration tests     │   ~15-25 escenarios
        │   @SpringBootTest       │   SQLite :memory:
        ├─────────────────────────┤
        │   Slice tests           │   ~30-50
        │   @WebMvcTest           │   por controller
        │   @DataJpaTest          │   por repository
        ├─────────────────────────┤
        │   Unit tests            │   ~50-80
        │   Mockito puro          │   services, state machine, validators, mappers
        └─────────────────────────┘
```

Total estimado: ~100-150 tests.

### 5.2 Cobertura objetivo (JaCoCo)

| Capa | Mínimo |
|---|---|
| `domain/` | 80% |
| `application/` (services, validadores, state machine) | **90%** |
| `infrastructure/web` | 70% |
| `infrastructure/persistence` | 60% |
| `shared/` | 80% |
| **Global** | **75%** |

JaCoCo enforcer falla el build si la cobertura cae bajo 75% global. Reporte en `target/site/jacoco/`.

### 5.3 Unit tests

- Stack: JUnit 5 + Mockito + AssertJ.
- Sin Spring (cero anotaciones de framework).
- Convención: `should_<expected>_when_<condition>`.
- Mockean los `*RepositoryPort`. Esto es el pago real de la inversión de dependencia.

### 5.4 Slice tests

- `@WebMvcTest(XxxController.class)` por controller: 3-5 tests cada uno.
- `@DataJpaTest` contra H2 in-memory: 2-4 tests por repositorio.

### 5.5 Integration tests

- `@SpringBootTest(webEnvironment = RANDOM_PORT)` + `@AutoConfigureMockMvc`.
- Perfil `test` con SQLite `:memory:` (`jdbc:sqlite::memory:`).
- 15-25 escenarios cubriendo flujos completos: CRUD + validaciones + soft delete + restore + state machine + import + export + flow validation pipeline.

**Decisión técnica**: SQLite es embebido y no encaja con Testcontainers. Se usa SQLite `:memory:` directo. Si en el futuro se migra a Postgres, se introducirá Testcontainers.

### 5.6 Tests del frontend

Stack: **Vitest + React Testing Library + MSW** (Mock Service Worker).

- ~20-30 tests.
- Componentes presentational con casos vacío/datos/loading/error.
- Custom hooks con MSW interceptando.
- Forms con validación cliente (zod).
- Cobertura objetivo: 65% global, 80% en hooks.

### 5.7 Datos de fixture

Builders de test en `src/test/java/.../support/`:

```java
public class PositionTestData {
    public static Position.PositionBuilder anyPosition() {
        return Position.builder()
            .name("Guardia Cerrada")
            .type(BOTTOM)
            .visibility(PRIVATE)
            .ownerId(1L);
    }
}
```

Reglas:
- Cada test crea sus propios datos.
- Datos mínimos para el test.
- `@Sql` con scripts en `src/test/resources/sql/` para escenarios complejos.

---

## 6. Frontend

### 6.1 Stack

| Capa | Elección |
|---|---|
| Build | Vite 5 |
| Lenguaje | TypeScript 5 (strict) |
| Framework | React 18 |
| Routing | React Router v6 (data routers) |
| Estado servidor | TanStack Query v5 |
| Estado cliente | Zustand (UI state mínimo) |
| Forms | React Hook Form + Zod |
| HTTP | ky |
| Estilos | Tailwind CSS v4 |
| Componentes | shadcn/ui (copiados al repo) |
| Iconos | lucide-react |
| Editor markdown | TipTap |
| Render markdown | react-markdown + remark-gfm |
| Editor de flow | React Flow (@xyflow/react) |
| Charts | Recharts |
| i18n | react-i18next |
| Tests | Vitest + RTL + MSW |
| Lint/format | ESLint + Prettier |

### 6.2 Estructura del repositorio

Repositorios separados:

```
github.com/<user>/
├─ OssFlow                ← backend Spring Boot
├─ OssFlow-frontend       ← frontend React/Vite (este apartado)
└─ ossflow-deploy         ← Docker compose e infra
```

### 6.3 Estructura interna del frontend

```
frontend/src/
├─ app/                          bootstrap, providers, router, layout
├─ shared/                       cross-context: api client, ui (shadcn), hooks, components, lib
├─ features/
│   ├─ catalog/{position,technique,system,import,rulesets}/
│   ├─ journal/{note,trainingsession,competitionlog}/
│   ├─ planning/studyplan/
│   ├─ identity/{profile,onboarding}/
│   └─ portability/
└─ pages/                        HomePage, TrashPage, NotFoundPage, OnboardingPage
```

Cada feature autocontenida: `api.ts`, `types.ts`, `schemas.ts` (zod), `pages/`, `components/`.

### 6.4 Routing principal

```
/                                          HomePage (dashboard)
/onboarding                                guard si no hay UserProfile
/catalog/{positions,techniques,systems}    list + detail + form
/catalog/systems/:id/edit                  React Flow editor
/catalog/import                            UI de importación
/catalog/rulesets                          gestión federaciones y reglas
/journal/notes                             list, detail, editor
/journal/graph                             vista grafo de notas (force-graph)
/journal/training-sessions                 CRUD
/journal/competitions                      CRUD con sub-recurso matches
/planning/study-plans                      list + board
/planning/study-plans/:id/timeline         vista Gantt
/profile                                   perfil de usuario
/trash                                     papelera global agregada
/export                                    exportar backup
```

### 6.5 Capa de API y traceId end-to-end

`shared/api/client.ts` con ky:

- `prefixUrl: VITE_API_BASE_URL ?? '/api/v1'`.
- `beforeRequest`: genera UUID y lo pone en header `X-Trace-Id`.
- `afterResponse`: si no-ok, parsea `ApiError` y lanza `ApiClientError`.
- Backend respeta el `X-Trace-Id` del header (no genera uno nuevo). Trazado extremo a extremo.

### 6.6 Tipado generado desde OpenAPI

Script `npm run gen:api` ejecuta `openapi-typescript` contra el `openapi.json` del backend (descarga del último GitHub Release o de localhost en desarrollo). En CI: `git diff --exit-code` falla si los tipos no se regeneraron.

**Resultado**: un cambio incompatible en el backend rompe el `tsc` del frontend. Contract testing gratis.

### 6.7 Manejo de errores en cliente

Centralizado en `shared/api/errors.ts`:

```ts
const ERROR_MESSAGES_KEYS: Record<string, string> = {
  POSITION_NOT_FOUND: 'errors.position.notFound',
  POSITION_NAME_DUPLICATE: 'errors.position.duplicate',
  INVALID_STATE_TRANSITION: 'errors.studyItem.invalidTransition',
  // ...
};

export function translateApiError(err: ApiError, t: TFunction): string {
  const key = ERROR_MESSAGES_KEYS[err.code];
  return key ? t(key, err.details) : err.message;
}
```

ErrorBoundary global para errores de render. Toast (shadcn `Toaster`) para errores de mutación.

### 6.8 Editor de Systems con React Flow

Componente `SystemEditorPage`:

```
SystemEditorPage
├─ NodePalette                Position/Technique catálogo, draggables
├─ FlowEditor (React Flow)
│   ├─ Custom nodes: PositionNode, TechniqueNode
│   ├─ Custom edges: TriggerEdge (color por trigger)
│   ├─ MiniMap, Controls, Background
│   └─ Drop crea nodo, click edge abre EdgeConditionDialog
├─ EdgeConditionDialog        markdown para `condition`
└─ Toolbar                    Undo/Redo, Save, Validate
```

Mapper `flowDefinition` ↔ React Flow en `lib/flowMapper.ts`. Validación zod en cliente (precheck UX) + validación schema en servidor (autoritativa).

### 6.9 Diseño visual

- **shadcn/ui + Tailwind v4**.
- **Tema oscuro por defecto**, toggle en topbar persistido en localStorage.
- **Tipografía**: Inter (sans) + JetBrains Mono (code/markdown).
- **Paleta semántica** con accent color BJJ (gradient blanco→azul→morado→marrón→negro como visualización de progreso, no como UI principal).
- **Densidad**: dos modos (cómodo / compacto).
- **Layout**: sidebar colapsable + topbar con búsqueda global Cmd+K (cmdk). Breadcrumbs en detalles.
- **Responsive**: mobile-first, sidebar como drawer en `<md`. Tablas → cards en mobile.
- **Editor de flow** con fallback en mobile: lista editable de nodos + vista grafo read-only.

### 6.10 Componentes destacados

1. **Editor de Flow** con React Flow (drag-and-drop, edge conditions inline).
2. **Note graph view** con react-force-graph (vista tipo Obsidian).
3. **Plan timeline** con Gantt simple.
4. **Dashboard home** con stats: técnicas dominadas por cinturón, sesiones del mes (heatmap), próximas competiciones, items DOING.
5. **Federation legality badges** en TechniqueCard y vistas relacionadas. Warning visual no bloqueante.

### 6.11 Onboarding del usuario

Si `GET /api/v1/identity/profile` → 404, redirige a `/onboarding`:

1. Pantalla bienvenida.
2. Datos básicos (nombre, gym opcional).
3. Cinturón actual + fecha aproximada.
4. Modalidad preferida (GI / NOGI / BOTH).
5. Selección de federaciones (multi-select con `is_primary`).
6. POST `/profile` con `onboardingCompleted: true`.

Si ya hay perfil, va al dashboard. Display "no invasivo": chip con cinturón en topbar (gradient barra horizontal estilo cinturón real). Click → modal de perfil.

### 6.12 Variables de entorno

```
.env.development
VITE_API_BASE_URL=http://localhost:8080/api/v1

.env.production
VITE_API_BASE_URL=/api/v1
```

### 6.13 Build y bundle

- Code-splitting por ruta automático.
- Lazy load de React Flow.
- Imágenes optimizadas con vite-plugin-image-optimizer.
- Bundle objetivo: < 200KB gzip para la home.

---

## 7. Docker, CI/CD y deploy

### 7.1 Tres repositorios

```
github.com/<user>/
├─ OssFlow                    backend
│   ├─ Dockerfile             multi-stage, runtime eclipse-temurin:25-jre-noble
│   └─ .github/workflows/ci.yml, release.yml
├─ OssFlow-frontend           frontend
│   ├─ Dockerfile             multi-stage Node → Nginx
│   ├─ nginx.conf
│   └─ .github/workflows/ci.yml, release.yml
└─ ossflow-deploy             infra
    ├─ docker-compose.yml         dev local
    ├─ docker-compose.prod.yml    Proxmox + Cloudflare Tunnel
    ├─ .env.example
    ├─ DEPLOY.md
    ├─ cloudflared/config.example.yml
    └─ scripts/{backup-sqlite.sh, restore-sqlite.sh}
```

### 7.2 Backend Dockerfile

```dockerfile
# Stage 1: build
FROM maven:3.9-eclipse-temurin-25 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B
RUN java -Djarmode=layertools -jar target/*.jar extract --destination target/extracted

# Stage 2: runtime (Eclipse Temurin oficial)
FROM eclipse-temurin:25-jre-noble
RUN groupadd --system spring && useradd --system --gid spring --shell /bin/false spring
WORKDIR /app
COPY --from=builder --chown=spring:spring /build/target/extracted/dependencies/ ./
COPY --from=builder --chown=spring:spring /build/target/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=spring:spring /build/target/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=spring:spring /build/target/extracted/application/ ./
EXPOSE 8080
USER spring
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
```

Imagen base: `eclipse-temurin:25-jre-noble` (Eclipse Adoptium, oficial). Tamaño ~250MB. Se descartó distroless (`gcr.io/distroless/java25-debian12`) porque a fecha del proyecto Google aún no publica imágenes distroless para Java 25 (LTS de septiembre de 2025); el retraso histórico de distroless tras cada nueva LTS lo hace inviable. La diferencia de superficie de ataque (~100MB extra) es asumible para un despliegue monousuario tras Cloudflare Tunnel. Usuario `spring` no-root creado explícitamente. Layered jar para cache. Cuando Google publique la imagen distroless de Java 25, la migración será cambiar la línea `FROM` (un commit trivial).

### 7.3 Frontend Dockerfile y nginx.conf

```dockerfile
FROM node:22-alpine AS builder
WORKDIR /build
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:1.27-alpine
COPY --from=builder /build/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
HEALTHCHECK CMD wget -q --spider http://localhost/ || exit 1
```

nginx.conf: SPA fallback, proxy `/api → http://backend:8080`, propagación `X-Trace-Id`, `/actuator → 404` en prod, cache largo para estáticos, gzip.

### 7.4 docker-compose.yml (dev local)

```yaml
services:
  backend:
    build: ../OssFlow
    image: ossflow-backend:dev
    environment:
      SPRING_PROFILES_ACTIVE: dev
      JAVA_TOOL_OPTIONS: "-XX:MaxRAMPercentage=75"
    ports: ["8080:8080"]
    volumes: [sqlite-data:/data]
    healthcheck:
      test: ["CMD", "wget", "-q", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s

  frontend:
    build: ../OssFlow-frontend
    image: ossflow-frontend:dev
    ports: ["5173:80"]
    depends_on:
      backend: { condition: service_healthy }

volumes:
  sqlite-data:
```

### 7.5 docker-compose.prod.yml (Proxmox + Cloudflare)

- Imágenes desde `ghcr.io/<user>/ossflow-{backend,frontend}:tag`.
- Volumen SQLite en `/var/lib/ossflow/data` (configurable).
- **Sin puertos expuestos al host**: cloudflared accede vía red Docker.
- Servicio `cloudflared` con token en `.env`.
- `restart: unless-stopped` en todos los servicios.

### 7.6 GitHub Actions

**Backend `ci.yml` (W1)**:
- `setup-java@v4` Temurin 21 con cache Maven.
- `mvn -B verify` (compila + tests + JaCoCo + Checkstyle).
- Sube reporte JaCoCo como artifact.
- Imprime top-10 archivos más grandes (god file watch).

**Backend `release.yml` (W2)**:
- Trigger en tag `v*.*.*`.
- Login en `ghcr.io`.
- `docker/metadata-action`: tags `v1.2.3`, `1.2`, `1`, `latest`.
- `docker/build-push-action` con cache GHA.
- Publica `openapi.json` como GitHub Release artifact.

**Frontend `ci.yml`**:
- `setup-node@v4` Node 22 con cache npm.
- `npm ci`.
- `npm run gen:api:release` (descarga openapi del último release backend).
- `git diff --exit-code` (falla si tipos cambiaron sin commit).
- `tsc --noEmit`, `npm run lint`, `npm run test:ci`, `npm run build`.
- Top-10 archivos más grandes.

**Frontend `release.yml`**: idéntico pattern al backend.

**Sincronización de releases (W4 opcional)**: backend release.yml dispara `repository_dispatch` al frontend → workflow del frontend regenera tipos y abre PR automático.

### 7.7 Cloudflare Tunnel

Integrado como contenedor en `docker-compose.prod.yml`. Token en `.env`. En el panel Cloudflare Zero Trust:

- Crear tunnel "ossflow", copiar token.
- Public Hostname: `ossflow.tudominio.com → http://frontend:80`.

Sin abrir puertos en el router. TLS automático. IP origen oculta.

### 7.8 Backups con cron + script bash

```bash
#!/usr/bin/env bash
set -euo pipefail
TIMESTAMP=$(date -u +%Y-%m-%dT%H-%M-%SZ)
BACKUP_DIR="/var/backups/ossflow"
mkdir -p "$BACKUP_DIR"

# Hot backup nativo SQLite (no cp directo)
docker exec ossflow-backend sqlite3 /data/ossflow.db ".backup /data/ossflow-backup.db"
docker cp ossflow-backend:/data/ossflow-backup.db "$BACKUP_DIR/ossflow-${TIMESTAMP}.db"
docker exec ossflow-backend rm /data/ossflow-backup.db

gzip "$BACKUP_DIR/ossflow-${TIMESTAMP}.db"
find "$BACKUP_DIR" -name 'ossflow-*.db.gz' -mtime +30 -delete
```

Cron en host: `0 4 * * * /opt/ossflow-deploy/scripts/backup-sqlite.sh`.

### 7.9 Migraciones de BD (Flyway)

- `application-prod.yml`: `spring.jpa.hibernate.ddl-auto: validate` + `spring.flyway.enabled: true`.
- `application-dev.yml`: H2 in-memory, `ddl-auto: update`, sin Flyway.
- `application-test.yml`: SQLite `:memory:`, Flyway aplicado.
- Migraciones en `src/main/resources/db/migration/V{N}__{descripcion}.sql`:
  - `V1__init_catalog.sql`
  - `V2__init_journal.sql`
  - `V3__init_planning.sql`
  - `V4__init_identity.sql`
  - `V100__seed_federations.sql`
  - `V101__seed_belt_enums.sql`

### 7.10 Observabilidad (alcance actual)

- **Logs estructurados con `@Slf4j`** + Logback JSON en prod.
- **Spring Boot Actuator** con endpoints `/actuator/health`, `/actuator/info`. Resto deshabilitado en prod.
- **MDC `traceId`** propagado por todo el request.
- **TODO documentado**: Micrometer + Prometheus + Grafana cuando duela en producción. Loki/ELK cuando los logs locales no basten. OpenTelemetry para trazas distribuidas si se migra a microservicios.

---

## 8. Roadmap de implementación

### 8.1 Filosofía

Reescritura **incremental sobre el repo existente**. Cada fase = un PR mergeable que deja el sistema en estado funcional y con tests verdes. Trabajo en ramas `feat/phase-NN-...`. `main` siempre verde.

### 8.2 Fases

| # | Fase | Estimación | Riesgo |
|---|---|---|---|
| 0 | Bootstrap & limpieza (Java 25, alinear pom, drop graphql/postgres, seed sqlite-jdbc) | 0.5d | Bajo |
| 1 | Estructura por contextos (catalog/{position,technique}, eliminar puertos in) | 1d | Medio |
| 2 | Excepciones, validación, traceId (GlobalExceptionHandler, RequestTracingFilter, Bean Validation) | 1d | Bajo |
| 3 | DTOs y CRUD completo del catálogo (response DTOs, GET/{id}, PUT, PATCH, DELETE, restore) | 1.5d | Bajo |
| 4 | SQLite + Flyway + soft delete + purga (BaseEntity, @SQLDelete, scheduled job) | 2d | Medio |
| 5 | Bounded context journal (Note, Tag, NoteTag, TrainingSession, TrainingSessionTechnique, CompetitionLog, CompetitionMatch) | 2.5d | Medio |
| 6 | Bounded context planning (StudyPlan, StudyBlock, StudyItem, StudyItemStateMachine, Observer event) | 2d | Medio |
| 7 | Bounded context identity + Federations + Rulesets (UserProfile, multi-select, seed federations) | 2d | Medio |
| 8 | System con flowDefinition + Chain of Responsibility (3 validadores) | 2d | Medio |
| 9 | Importadores (Strategy + Template Method): Catalog, System, Ruleset | 1.5d | Medio |
| 10 | Export full streaming | 0.5d | Bajo |
| 11 | Tests integración + JaCoCo gate + Checkstyle god files | 1d | Bajo |
| 12 | OpenAPI + springdoc + Swagger UI | 0.5d | Bajo |
| 13 | Logging estructurado (Logback JSON prod) + Actuator | 0.5d | Bajo |
| 14 | Docker backend + GitHub Actions CI/Release | 1d | Medio |
| 15 | Frontend bootstrap (repo nuevo, Vite, React, Tailwind, shadcn, layout, theme dark) | 1d | Bajo |
| 16 | Frontend catalog CRUD (Position/Technique/System list+detail+form) | 2.5d | Medio |
| 17 | Frontend journal CRUD (Note, TrainingSession, CompetitionLog) | 2.5d | Medio |
| 18 | Frontend planning CRUD + state machine (board kanban + transiciones) | 2d | Medio |
| 19 | Frontend identity + onboarding + federation badges | 1.5d | Medio |
| 20 | Frontend editor System con React Flow (paleta drag-drop, condition dialog) | **3d** | **Alto** |
| 21 | Frontend import / export / dashboard / cmdk | 1.5d | Medio |
| 22 | Frontend Note graph view (force-graph) | 1d | Medio |
| 23 | Frontend Plan timeline (Gantt) | 1d | Medio |
| 24 | Frontend Docker + CI/Release + tipos OpenAPI | 1d | Medio |
| 25 | Repo ossflow-deploy (compose dev+prod, scripts backup, DEPLOY.md, cloudflared) | 1d | Bajo |
| 26 | Pulido final + memoria académica final + diagramas + slides defensa | 2d | Bajo |

**Total estimado**: ~35 días efectivos (~2-3 meses a tiempo parcial).

### 8.3 Migración del código actual

| Archivo actual | Acción |
|---|---|
| `domain/Position.java`, `Technique.java`, enums | Mantener, mover a `catalog/{position,technique}/domain/` |
| `application/port/in/*UseCase.java` | **Eliminar** (decisión hexagonal-lite) |
| `application/port/out/*Port.java` | Renombrar a `*RepositoryPort`, mover a `application/port/` |
| `application/service/*Service.java` | Mantener, reescribir sin interfaz UseCase |
| `infra/adapter/in/web/*Controller.java` | Reescribir (DTOs response, validación, sin entidades expuestas) |
| `infra/adapter/out/db/entity/*Entity.java` | Mantener, mover a `infrastructure/persistence/` |
| `infra/adapter/out/db/repository/*JpaRepository.java` | Mantener, mover |
| `infra/adapter/out/db/*DatabaseAdapter.java` | Renombrar a `*PersistenceAdapter`, mover |
| `infra/adapter/out/db/mapper/*Mapper.java` | Renombrar a `*PersistenceMapper`, mover |
| `infra/adapter/in/web/dto/CreateTechniqueRequest.java` | Mantener, añadir `@Valid` |
| Tests | Adaptar a nuevos paquetes |

Conservación estimada: ~70% del código actual.

### 8.4 TODOs documentados (fuera de alcance)

| Item | Cuándo entraría |
|---|---|
| Spring Security + JWT | Cuando haya multi-usuario real |
| Resilience4j Circuit Breaker | Cuando se integre IA externa |
| Prometheus + Grafana | Cuando duela en prod |
| Loki / ELK / Kibana | Cuando logs locales no basten |
| Testcontainers | Si se migra a Postgres |
| Litestream | Si se quiere RPO < 24h |
| OpenTelemetry distribuido | Si se migra a microservicios |
| E2E con Playwright | Cuando estabilice y haya regresiones |
| GraphQL | Si aparece query con shape variable |
| Importación de "todo" | Cuando haya export-import multiusuario |
| Workflow W3 deploy automático | Cuando moleste hacerlo a mano |

### 8.5 Criterios de "listo para defender"

- [ ] CRUD completo de las 18 entidades funciona en backend.
- [ ] Frontend levanta y permite usar las 4 features (catalog, journal, planning, identity).
- [ ] Editor de flow con React Flow funciona end-to-end.
- [ ] `docker compose up` en local arranca todo.
- [ ] Cobertura de tests ≥ 75% global, ≥ 90% en services.
- [ ] CI verde en backend y frontend (workflows W1).
- [ ] Imágenes publicadas en `ghcr.io` (workflows W2).
- [ ] README de cada repo profesional con badges y diagrama.
- [ ] Memoria académica entregada en `.docx`.
- [ ] Demo desplegada vía Cloudflare Tunnel funcional (opcional pero ideal).

---

## Decisiones arquitectónicas registradas

Resumen de las decisiones más relevantes y su justificación, para futura defensa:

1. **Hexagonal-lite en lugar de hexagonal puro**: ceremonia sin pago en puertos `in` con un único implementador. Mantenemos puertos `out` donde la inversión sí aporta (testabilidad de persistencia).
2. **Monolito modular en lugar de microservicios**: complejidad operativa de microservicios injustificada para un proyecto monousuario con un solo bounded context dominante.
3. **SQLite en producción**: la app es de un solo usuario, los datos caben holgadamente, simplifica deploy y backup.
4. **H2 en desarrollo, SQLite en test e integración**: H2 arranca en milisegundos para iteración rápida; SQLite `:memory:` da fidelidad de runtime en tests.
5. **18 tablas normalizadas**: el usuario rechaza columnas JSON salvo `study_block.focus_entities` por su naturaleza polimórfica. Tags compartidos globalmente.
6. **Soft delete con ventana 30 días**: balance entre "deshacer un error" y "no acumular basura indefinida".
7. **State machine como bean separado**: invariantes de dominio no embebidas en if/else de servicios.
8. **Chain of Responsibility para validación de imports y flows**: cada validador con su responsabilidad, fail-fast, extensible sin tocar existentes.
9. **Tags globales sin owner**: evita duplicación entre usuarios; vocabulario emergente.
10. **Repos separados (3) en lugar de monorepo**: cada pieza con responsabilidad clara, defensa más limpia.
11. **Tipos TypeScript generados desde OpenAPI**: contract testing gratis, breaking changes detectados en CI.
12. **Tema oscuro por defecto**: encaja con la estética BJJ y reduce fatiga visual en uso prolongado.
13. **Federaciones multi-select con `is_primary`**: refleja la realidad de competidores que cruzan circuitos.
14. **Legalidad técnica como warning, nunca bloqueo**: practicar técnicas prohibidas en competición sigue siendo legítimo en entrenamiento.
15. **traceId end-to-end con MDC**: debugging y compatible con OpenTelemetry/Jaeger en futuro.
16. **Cloudflare Tunnel sobre Proxmox**: TLS sin configurar, sin abrir puertos, autocontenido.
17. **Backup con cron + script bash**: Litestream sería overkill para RPO de 24h aceptable.
18. **No god files con límites enforzados en CI**: previene erosión del diseño orgánicamente.
