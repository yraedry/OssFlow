<div align="center">

# OssFlow — Backend

**Segundo cerebro técnico para Brazilian Jiu-Jitsu**

[![CI](https://github.com/yraedry/OssFlow/actions/workflows/ci.yml/badge.svg)](https://github.com/yraedry/OssFlow/actions/workflows/ci.yml)
![Java 25](https://img.shields.io/badge/Java-25-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-6db33f?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169e1?logo=postgresql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-V200--V265-cc0200?logo=flyway&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue)

API REST construida con Spring Boot 4, arquitectura hexagonal por bounded contexts y PostgreSQL 17.

</div>

---

## Stack

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 25 |
| Framework | Spring Boot 4.x (Spring Framework 7) |
| Base de datos | PostgreSQL 17 |
| Migraciones | Flyway (V200–V265) |
| ORM | JPA / Hibernate 7 |
| Mapping | MapStruct + Lombok |
| Tests | JUnit 5, AssertJ, JaCoCo |
| API Docs | springdoc-openapi (Swagger UI) |
| Seguridad | Spring Security 7, JWT RS256, OAuth2 Google |
| CI/CD | GitHub Actions + Docker + GHCR |

---

## Bounded Contexts

```
com.ossflow/
├── catalog/        # Técnicas, posiciones, ejercicios, reglamentos
├── journal/        # Sesiones BJJ y físicas, notas, competiciones
├── planning/       # Planes de estudio, plantilla semanal, rutinas
├── coaching/       # Relación maestro-atleta, observaciones, notas, sesiones privadas
├── dashboard/      # Radares de análisis técnico y físico
├── identity/       # Perfil, cinturón, federaciones
└── shared/         # Seguridad, excepciones, configuración
```

---

## Requisitos

- JDK 25 (`sdk install java 25.0.3-tem` con SDKMAN)
- PostgreSQL 17 corriendo localmente (o vía Docker)
- Maven 3.9+

---

## Arrancar en local

### 1. Base de datos con Docker

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
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

La API arranca en `http://localhost:8080`.  
Swagger UI disponible en `http://localhost:8080/swagger-ui.html`.

### 3. Build y tests

```bash
./mvnw clean verify    # build + tests + jacoco
./mvnw test            # solo tests
```

---

## Perfiles

| Perfil | Base de datos | Uso |
|--------|--------------|-----|
| `dev` | PostgreSQL local (`ossflow_dev`) | Desarrollo local, Swagger habilitado |
| `test` | PostgreSQL (CI) | Tests de integración |
| `prod` | PostgreSQL externo (env vars) | Producción |

Variables de entorno para prod: `POSTGRES_HOST`, `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, `AUTH_JWT_PRIVATE_KEY_B64`, `AUTH_JWT_PUBLIC_KEY_B64`.

---

## Docker

```bash
docker build -t ossflow-backend .

docker run -d \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e POSTGRES_HOST=<host> \
  -e POSTGRES_DB=ossflow \
  -e POSTGRES_USER=<user> \
  -e POSTGRES_PASSWORD=<pass> \
  -p 8080:8080 \
  ossflow-backend
```
