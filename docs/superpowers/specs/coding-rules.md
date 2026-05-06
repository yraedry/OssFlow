# Reglas de codificación de OssFlow

Reglas no negociables que aplican a todo el código del proyecto (backend Java, frontend TypeScript) y que se enforzan en CI.

## Regla 1 — No god files

Ningún archivo del proyecto puede convertirse en un "dios" que concentre demasiada responsabilidad. Si un archivo crece más de la cuenta, casi siempre es señal de que está haciendo dos cosas distintas y debe trocearse.

### Límites por tipo de archivo

| Tipo | Blando (warning CI) | Duro (falla CI) | Origen del número |
|---|---|---|---|
| Clase Java (controller, service, etc.) | 400 líneas | 600 líneas | Punto medio entre Sonar (750) y Clean Code |
| Componente React `.tsx` | 250 líneas | 400 líneas | ESLint default (300) ajustado a JSX |
| Hook / utilidad TS | 150 líneas | 250 líneas | ESLint per-function 50 → archivo coherente |
| Test file | 500 líneas | 800 líneas | Más permisivo: tests crecen orgánicamente |
| Método / función | 40 líneas | 80 líneas | Punto medio Airbnb 50 / SonarQube 100 |
| Cyclomatic complexity por método | 10 | 15 | SonarQube/PMD default |

### Excepciones

- Migraciones Flyway `.sql`: sin límite (un seed puede ser largo por naturaleza).
- Archivos de configuración (Spring `application.yml`, JSON schemas, plantillas): sin límite.
- Tipos generados automáticamente desde OpenAPI (`generated.ts`): excluidos del lint.

### Cómo se aplica

- **Backend (Java)**: Checkstyle con reglas `FileLength`, `MethodLength`, `CyclomaticComplexity` configuradas a los valores de la tabla. Build de Maven falla si se supera el límite duro; emite warning si se supera el blando.
- **Frontend (TypeScript)**: ESLint con `max-lines`, `max-lines-per-function`, `complexity` configuradas igual. CI falla con `--max-warnings=0` para los duros.
- **Métrica visible en CI**: cada workflow imprime los 10 archivos más grandes del proyecto, para detectar drift antes del límite.

### Refactor proactivo

Cuando un archivo se acerca al límite blando, se aplican estas estrategias antes de mergear:

- **Controller crece** → extraer subcontroller por sub-recurso (ej: `CompetitionMatchController` aparte de `CompetitionLogController`).
- **Service crece** → dividir por caso de uso (`PositionQueryService` + `PositionCommandService`) o extraer colaboradores (`StateMachine`, `Validator`, `Mapper`).
- **Componente React crece** → extraer presentational components, mover lógica a custom hook, partir el archivo por dominio visual.

## Regla 2 — Una responsabilidad por clase / componente (SRP)

Cada clase Java y cada componente React debe poder describirse con una frase. Si la frase necesita "y", probablemente sean dos clases.

## Regla 3 — Inversión de dependencia donde aporta

Solo en la frontera de persistencia (puertos `*RepositoryPort` ← adapters JPA). En el resto, los servicios son clases concretas sin interfaz redundante. Aplicamos hexagonal-lite: hexagonal donde aporta valor demostrable, capas planas donde la abstracción extra solo añade archivos.

## Regla 4 — Validar en la frontera, confiar en el interior

- Validación de DTOs con `jakarta.validation` (`@Valid`, `@NotBlank`, etc.) en la capa web.
- Invariantes de dominio se validan al construir objetos del dominio.
- Validación semántica compleja (JSON schemas, integridad referencial) en pipeline Chain of Responsibility.
- Servicios y adapters internos confían en sus parámetros (no validan dos veces lo mismo).

## Regla 5 — Sin comentarios obvios

- No comentar lo que el código ya dice.
- Solo se comenta el **por qué** cuando no es obvio: una restricción oculta, un workaround para un bug específico, una invariante sutil.
- Sin docstrings de varios párrafos.
- Sin referencias a tickets, autores o "added for X" — eso vive en git.

## Regla 6 — Logs estructurados con `traceId`

- Todo log usa `@Slf4j` (Lombok) o `LoggerFactory.getLogger(Class)`.
- Cada request tiene un `traceId` UUID en MDC propagado por `RequestTracingFilter`.
- Niveles:
  - `debug`: información de flujo en happy path. Solo activo en dev.
  - `info`: operaciones de negocio relevantes (import completado, plan creado).
  - `warn`: errores 4xx (validación, conflicto, no encontrado).
  - `error`: solo 5xx y excepciones inesperadas, con stack trace completo.

## Regla 7 — Idioma

- **Código**: identificadores en inglés (`Position`, `findByOwnerId`, `validateFlow`).
- **Mensajes al usuario**: en español (`ValidationMessages.properties`, mensajes de error de dominio).
- **Códigos de error técnicos**: en inglés UPPER_SNAKE_CASE (`POSITION_NOT_FOUND`).
- **Comentarios y commits**: en español.
- **Documentación interna del proyecto**: en español.

## Regla 8 — Tests

- Cobertura mínima global 75%, con énfasis en services y validadores (≥ 90%).
- Tres niveles: unit (Mockito puro), slice (`@WebMvcTest` / `@DataJpaTest`), integration (`@SpringBootTest` con SQLite `:memory:`).
- Cada test es independiente: crea sus propios datos, no comparte fixtures globales.
- Nombres de test descriptivos: `should_<expected>_when_<condition>`.

## Regla 9 — Sin features que no resuelven un problema real

- No añadir patrones, abstracciones o tecnología por demostrar conocimiento.
- Cada decisión arquitectónica debe poder defenderse con un problema concreto que resuelve.
- Patrones aplicados en este proyecto: Strategy, Chain of Responsibility, Template Method, State, Observer (gancho), Builder. Cualquier otro requiere justificación.
