# OssFlow — Backend

Segundo cerebro técnico para Brazilian Jiu-Jitsu. API REST construida con Spring Boot 4 (Java 25), PostgreSQL y bounded contexts.

## Stack

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 25 |
| Framework | Spring Boot 4.x (Spring Framework 7) |
| Base de datos | PostgreSQL 17 |
| Migraciones | Flyway |
| ORM | JPA / Hibernate 7 |
| Mapping | MapStruct |
| Tests | JUnit 5, AssertJ, JaCoCo |
| API Docs | springdoc-openapi (Swagger UI en dev) |

## Requisitos

- JDK 25 (`sdk install java 25.0.3-tem` con SDKMAN)
- PostgreSQL 17 corriendo localmente (o via Docker)
- Maven 3.9+

## Arrancar en local

### 1. Base de datos local con Docker

```bash
docker run -d \
  --name ossflow-pg \
  -e POSTGRES_DB=ossflow_dev \
  -e POSTGRES_USER=ossflow \
  -e POSTGRES_PASSWORD=ossflow \
  -p 5432:5432 \
  postgres:17-alpine
```

### 2. Ejecutar la aplicación

```bash
export JAVA_HOME=/ruta/a/jdk25
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

La API arranca en `http://localhost:8080`.
Swagger UI disponible en `http://localhost:8080/swagger-ui.html`.

### 3. Build y tests

```bash
./mvnw clean verify          # build + tests + jacoco
./mvnw test                  # solo tests
```

## Perfiles

| Perfil | Base de datos | Uso |
|--------|--------------|-----|
| `dev` | PostgreSQL local (`ossflow_dev`) | Desarrollo local, Swagger habilitado |
| `test` | H2 in-memory | Tests de integración en CI |
| `prod` | PostgreSQL externo (env vars) | Producción |

Variables de entorno para prod: `POSTGRES_HOST`, `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`.

## Docker

```bash
# Build imagen
docker build -t ossflow-backend .

# Ejecutar con PostgreSQL externo
docker run -d \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e POSTGRES_HOST=<host> \
  -e POSTGRES_DB=ossflow \
  -e POSTGRES_USER=<user> \
  -e POSTGRES_PASSWORD=<pass> \
  -p 8080:8080 \
  ossflow-backend
```

## Estructura del proyecto

```
src/
├── main/java/com/ossflow/
│   ├── catalog/          # Técnicas, posiciones, ejercicios
│   ├── journal/          # Sesiones BJJ y físicas, notas
│   ├── competition/      # Competiciones
│   ├── dashboard/        # Radares de análisis
│   ├── identity/         # Perfil de usuario
│   └── shared/           # BaseEntity, excepciones, configuración
└── main/resources/
    ├── db/migration/     # Migraciones Flyway (V200–V229)
    └── application*.yml  # Configuración por perfil
```

## Documentación

- Spec de diseño: `docs/superpowers/specs/`
- Reglas de código: `docs/superpowers/specs/coding-rules.md`
- Códigos de error: `docs/superpowers/specs/error-codes.md`
