# Tabla de códigos de error de OssFlow

Códigos estables que el backend devuelve en el campo `code` de `ApiError`. El frontend reacciona por código (estable) y muestra `message` (humano, fallback). Cuando se añade un código nuevo, se documenta aquí.

## Estructura de la respuesta de error

```json
{
  "timestamp": "2026-05-06T12:30:00Z",
  "status": 409,
  "error": "Conflict",
  "code": "POSITION_NAME_DUPLICATE",
  "message": "Ya existe una posición con el nombre 'Guardia Cerrada'",
  "path": "/api/v1/catalog/positions",
  "traceId": "550e8400-e29b-41d4-a716-446655440000",
  "fieldErrors": null,
  "details": {
    "positionName": "Guardia Cerrada"
  }
}
```

## Códigos por contexto

### Globales

| code | HTTP | Significado |
|---|---|---|
| `VALIDATION_FAILED` | 400 | DTO inválido. `fieldErrors` rellenado. |
| `BAD_REQUEST` | 400 | JSON malformado o tipo incorrecto. |
| `INTERNAL_ERROR` | 500 | Error inesperado. `traceId` para correlacionar logs. |
| `RESOURCE_ALREADY_RESTORED` | 409 | Restore sobre algo que no estaba en papelera. |
| `RESOURCE_PURGED` | 409 | Restore sobre algo que ya pasó la ventana de 30 días. |

### Catalog — Position

| code | HTTP | Significado |
|---|---|---|
| `POSITION_NOT_FOUND` | 404 | Posición inexistente o purgada. |
| `POSITION_NAME_DUPLICATE` | 409 | UNIQUE violado al crear/actualizar. |
| `POSITION_IN_USE` | 409 | No se puede hard-delete: hay técnicas/sistemas que la referencian. |

### Catalog — Technique

| code | HTTP | Significado |
|---|---|---|
| `TECHNIQUE_NOT_FOUND` | 404 | Técnica inexistente o purgada. |
| `TECHNIQUE_NAME_DUPLICATE` | 409 | UNIQUE violado. |
| `TECHNIQUE_IN_USE` | 409 | Hard-delete bloqueado por referencias en sistemas/notas/sesiones/items/matches. |

### Catalog — System

| code | HTTP | Significado |
|---|---|---|
| `SYSTEM_NOT_FOUND` | 404 | Sistema inexistente. |
| `SYSTEM_NAME_DUPLICATE` | 409 | UNIQUE violado. |
| `SYSTEM_FLOW_SCHEMA_INVALID` | 422 | El `flowDefinition` no cumple el JSON schema. |
| `SYSTEM_FLOW_SEMANTIC_INVALID` | 422 | Schema OK pero hay nodos huérfanos / edges colgantes / ciclos sin trigger. |
| `SYSTEM_FLOW_REF_NOT_FOUND` | 422 | Un `refId` apunta a posición/técnica inexistente o purgada. |

### Catalog — Import

| code | HTTP | Significado |
|---|---|---|
| `IMPORT_VALIDATION_FAILED` | 422 | El JSON de import falló alguna fase del pipeline. `details.validatorStep` indica cuál. |
| `IMPORT_MODE_INVALID` | 400 | `mode` distinto a `MERGE` / `REPLACE`. |

### Catalog — Federation / Ruleset

| code | HTTP | Significado |
|---|---|---|
| `FEDERATION_NOT_FOUND` | 404 | |
| `FEDERATION_CODE_DUPLICATE` | 409 | |
| `RULESET_NOT_FOUND` | 404 | |
| `RULESET_DUPLICATE` | 409 | Combinación `(federation, belt, modality, effective_from)` ya existe. |

### Journal — Note

| code | HTTP | Significado |
|---|---|---|
| `NOTE_NOT_FOUND` | 404 | |
| `NOTE_TARGET_INVALID` | 422 | `targetType + targetId` apuntan a algo inexistente. |
| `TAG_IN_USE` | 409 | Hard-delete de tag con notas asociadas. |

### Journal — TrainingSession

| code | HTTP | Significado |
|---|---|---|
| `TRAINING_SESSION_NOT_FOUND` | 404 | |
| `WORKED_TECHNIQUE_DUPLICATE` | 409 | Misma técnica añadida dos veces a la misma sesión. |

### Journal — CompetitionLog / Match

| code | HTTP | Significado |
|---|---|---|
| `COMPETITION_LOG_NOT_FOUND` | 404 | |
| `MATCH_NOT_FOUND` | 404 | |
| `MATCH_ORDER_DUPLICATE` | 409 | Dos combates con el mismo `matchOrder` en una competición. |

### Planning — StudyPlan / Block / Item

| code | HTTP | Significado |
|---|---|---|
| `STUDY_PLAN_NOT_FOUND` | 404 | |
| `STUDY_BLOCK_NOT_FOUND` | 404 | |
| `STUDY_ITEM_NOT_FOUND` | 404 | |
| `INVALID_STATE_TRANSITION` | 409 | StudyItem.status no permite el cambio pedido. `details.from`, `details.to`, `details.allowed` rellenados. |
| `BLOCK_DATES_OUT_OF_PLAN` | 422 | Fechas del block fuera del rango del plan. |
| `ITEM_DUE_OUT_OF_BLOCK` | 422 | `dueDate` del item fuera del rango del block. |

### Identity — UserProfile

| code | HTTP | Significado |
|---|---|---|
| `PROFILE_NOT_FOUND` | 404 | El owner no ha completado onboarding. |
| `PROFILE_ALREADY_EXISTS` | 409 | Intento de crear un segundo perfil para el mismo owner. |
| `PRIMARY_FEDERATION_REQUIRED` | 422 | Se intentó dejar el set de federaciones sin ninguna marcada `is_primary`. |
| `MULTIPLE_PRIMARY_FEDERATIONS` | 422 | Más de una federación marcada `is_primary`. |

## Reglas para añadir códigos

- UPPER_SNAKE_CASE.
- Prefijo por entidad/contexto (`POSITION_`, `STUDY_ITEM_`, ...).
- Sufijo describe la condición (`_NOT_FOUND`, `_DUPLICATE`, `_IN_USE`, `_INVALID`).
- Siempre con HTTP status asociado consistente.
- Documentar aquí antes de mergear el código que lo lanza.
