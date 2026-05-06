# OssFlow

Segundo cerebro técnico para Brazilian Jiu-Jitsu. Backend Spring Boot 4 (Java 25) con bounded contexts, SQLite/H2 y CRUD completo.

## Stack

- Java 25, Spring Boot 4.x (Spring Framework 7)
- SQLite (prod) + H2 (dev) + Flyway
- JPA/Hibernate 7, MapStruct, Lombok
- JUnit 5, Mockito, AssertJ, JaCoCo
- springdoc-openapi (Swagger UI en dev)

## Perfiles

- `dev` (default local): H2 in-memory, Swagger habilitado, DDL update
- `test`: SQLite :memory:, Flyway aplicado
- `prod`: SQLite fichero, Flyway + ddl-auto validate

## Comandos

```bash
mvn clean verify          # build + tests + jacoco
mvn spring-boot:run       # arrancar con perfil dev
```

## Java 25

Este proyecto requiere JDK 25. Si no lo tienes instalado:

```bash
# Instalar con SDKMAN
sdk install java 25.0.3-tem
```

Configurar JAVA_HOME antes de ejecutar Maven:

```bash
export JAVA_HOME=/ruta/a/jdk25
```

## Documentación

- Spec: `docs/superpowers/specs/2026-05-06-ossflow-rediseno-design.md`
- Reglas: `docs/superpowers/specs/coding-rules.md`
- Códigos error: `docs/superpowers/specs/error-codes.md`
- Plan de implementación: `docs/superpowers/plans/2026-05-06-backend-ossflow.md`
