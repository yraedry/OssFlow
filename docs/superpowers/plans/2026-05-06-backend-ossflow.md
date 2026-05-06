# Backend OssFlow — Plan de implementación

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Reescritura completa del backend de OssFlow desde la base actual (Java 17 + Spring Boot 4.0.5 + H2) hasta un sistema en Java 25 + Spring Boot 4.x con cinco bounded contexts (catalog, journal, planning, identity, shared), arquitectura hexagonal-lite, SQLite + Flyway, validación en tres capas, traceId end-to-end, importadores con Strategy/Template Method/Chain of Responsibility, máquina de estados, CRUD completo de 18 entidades, Dockerfile multi-stage con runtime eclipse-temurin:25-jre-noble y GitHub Actions CI/Release.

**Architecture:** Monolito modular con bounded contexts y dependencias en DAG. Cada feature en `domain/`, `application/` (servicio + puerto out), `infrastructure/` (web + persistence). Hexagonal-lite: solo puertos de salida, servicios como clases concretas. Soft delete con purga 30d. Patrones GoF aplicados con justificación.

**Tech Stack:** Java 25, Spring Boot 4.x (Spring Framework 7), JPA/Hibernate 7, SQLite (prod) + H2 (dev) + SQLite :memory: (test), Flyway, MapStruct, Lombok, JUnit 5, Mockito, AssertJ, JaCoCo, Checkstyle, springdoc-openapi, Logback JSON, Docker (eclipse-temurin:25-jre-noble), GitHub Actions.

**Spec base:** `docs/superpowers/specs/2026-05-06-ossflow-rediseno-design.md`
**Reglas de codificación:** `docs/superpowers/specs/coding-rules.md`
**Códigos de error:** `docs/superpowers/specs/error-codes.md`

---

## Estructura de archivos final esperada

```
OssFlow/
├─ pom.xml                                                  Java 25, Spring Boot 4.x, sqlite-jdbc, flyway, mapstruct, lombok, jacoco, checkstyle
├─ Dockerfile                                               multi-stage, runtime eclipse-temurin:25-jre-noble
├─ checkstyle.xml                                           reglas FileLength, MethodLength, CyclomaticComplexity
├─ .github/workflows/
│   ├─ ci.yml                                               build + test + jacoco + checkstyle
│   └─ release.yml                                          docker build/push + openapi.json artifact
├─ src/main/java/com/ossflow/
│   ├─ OssflowApplication.java
│   ├─ shared/
│   │   ├─ config/
│   │   │   ├─ JpaAuditingConfig.java                       @EnableJpaAuditing
│   │   │   ├─ ScheduledTasksConfig.java                    @EnableScheduling
│   │   │   ├─ OpenApiConfig.java                           springdoc bean
│   │   │   └─ JsonSchemaValidatorConfig.java               networknt validator bean
│   │   ├─ exception/
│   │   │   ├─ OssFlowException.java                        abstracta raíz
│   │   │   ├─ NotFoundException.java                       → 404
│   │   │   ├─ ConflictException.java                       → 409
│   │   │   ├─ DuplicateNameException.java                  extends ConflictException
│   │   │   ├─ ReferenceInUseException.java                 extends ConflictException
│   │   │   ├─ InvalidStateTransitionException.java         extends ConflictException
│   │   │   ├─ UnprocessableException.java                  → 422
│   │   │   ├─ JsonSchemaViolationException.java            extends UnprocessableException
│   │   │   ├─ SemanticValidationException.java             extends UnprocessableException
│   │   │   ├─ ReferentialIntegrityException.java           extends UnprocessableException
│   │   │   ├─ BadRequestException.java                     → 400
│   │   │   ├─ ApiError.java                                record
│   │   │   ├─ FieldError.java                              record (interno de ApiError)
│   │   │   └─ GlobalExceptionHandler.java                  @RestControllerAdvice
│   │   ├─ persistence/
│   │   │   ├─ BaseEntity.java                              @MappedSuperclass
│   │   │   ├─ SoftDeletePurgeJob.java                      @Scheduled(cron="0 0 3 * * *")
│   │   │   └─ ConstraintTranslator.java                    DataIntegrityViolationException → 409 code
│   │   ├─ validation/
│   │   │   ├─ ValidationStep.java                          interfaz
│   │   │   ├─ ValidationChain.java                         orquesta cadena
│   │   │   ├─ ValidationContext.java                       acumula errores/warnings
│   │   │   └─ ValidationResult.java                        sealed: Ok | Fail
│   │   ├─ web/
│   │   │   ├─ RequestTracingFilter.java                    OncePerRequestFilter, MDC traceId
│   │   │   └─ PageResponse.java                            wrapper estándar de Page<T>
│   │   └─ json/
│   │       └─ JsonSchemaValidator.java                     wrapper sobre networknt
│   ├─ catalog/
│   │   ├─ position/
│   │   │   ├─ domain/
│   │   │   │   ├─ Position.java                            record con @Builder
│   │   │   │   ├─ PositionType.java                        enum
│   │   │   │   └─ Visibility.java                          enum compartido (vive aquí)
│   │   │   ├─ application/
│   │   │   │   ├─ PositionService.java                     clase concreta
│   │   │   │   └─ port/PositionRepositoryPort.java         interfaz
│   │   │   └─ infrastructure/
│   │   │       ├─ web/
│   │   │       │   ├─ PositionController.java
│   │   │       │   ├─ PositionWebMapper.java               MapStruct
│   │   │       │   └─ dto/
│   │   │       │       ├─ CreatePositionRequest.java
│   │   │       │       ├─ UpdatePositionRequest.java
│   │   │       │       ├─ PatchPositionRequest.java
│   │   │       │       └─ PositionResponse.java
│   │   │       └─ persistence/
│   │   │           ├─ PositionEntity.java                  @Entity
│   │   │           ├─ PositionJpaRepository.java           extends JpaRepository
│   │   │           ├─ PositionPersistenceAdapter.java      implements port
│   │   │           └─ PositionPersistenceMapper.java       MapStruct
│   │   ├─ technique/                                       (estructura idéntica a position)
│   │   │   ├─ domain/{Technique, TechniqueCategory, Belt, Modality}.java
│   │   │   ├─ application/{TechniqueService, port/TechniqueRepositoryPort}.java
│   │   │   └─ infrastructure/web/persistence/...
│   │   ├─ system/
│   │   │   ├─ domain/{System, SystemFlowDefinition (record)}.java
│   │   │   ├─ application/
│   │   │   │   ├─ SystemService.java
│   │   │   │   ├─ port/SystemRepositoryPort.java
│   │   │   │   └─ validation/
│   │   │   │       ├─ FlowSchemaValidationStep.java        valida contra system-flow.schema.v1.json
│   │   │   │       ├─ FlowSemanticValidationStep.java      no nodos huérfanos
│   │   │   │       └─ FlowReferentialValidationStep.java   refIds existen y no purgados
│   │   │   └─ infrastructure/...
│   │   ├─ federation/
│   │   │   ├─ domain/Federation.java
│   │   │   ├─ application/{FederationService, port/FederationRepositoryPort}.java
│   │   │   └─ infrastructure/...
│   │   ├─ ruleset/
│   │   │   ├─ domain/{Ruleset, RulesetTechnique, LegalityStatus (enum)}.java
│   │   │   ├─ application/{RulesetService, port/RulesetRepositoryPort}.java
│   │   │   └─ infrastructure/...
│   │   └─ portability/
│   │       ├─ application/
│   │       │   ├─ Importer.java                            interfaz Strategy
│   │       │   ├─ AbstractImporter.java                    Template Method
│   │       │   ├─ CatalogImporter.java                     Strategy concreta
│   │       │   ├─ SystemImporter.java                      Strategy concreta
│   │       │   ├─ RulesetImporter.java                     Strategy concreta
│   │       │   └─ CatalogExporter.java
│   │       └─ infrastructure/web/{ImportController, ExportController}.java
│   ├─ journal/
│   │   ├─ note/
│   │   │   ├─ domain/{Note, NoteTarget (sealed)}.java
│   │   │   ├─ application/{NoteService, port/NoteRepositoryPort}.java
│   │   │   └─ infrastructure/...
│   │   ├─ tag/                                             tabla global, sin owner
│   │   │   ├─ domain/Tag.java
│   │   │   ├─ application/{TagService, port/TagRepositoryPort}.java
│   │   │   └─ infrastructure/...
│   │   ├─ trainingsession/
│   │   │   ├─ domain/{TrainingSession, WorkedTechnique, Intensity}.java
│   │   │   ├─ application/{TrainingSessionService, port/TrainingSessionRepositoryPort}.java
│   │   │   └─ infrastructure/...
│   │   ├─ competitionlog/
│   │   │   ├─ domain/{CompetitionLog, CompetitionMatch, CompetitionResult, MatchOutcome, MatchMethod}.java
│   │   │   ├─ application/{CompetitionLogService, port/CompetitionLogRepositoryPort}.java
│   │   │   └─ infrastructure/...
│   │   └─ portability/JournalExporter.java
│   ├─ planning/
│   │   ├─ studyplan/
│   │   │   ├─ domain/{StudyPlan, StudyPlanStatus}.java
│   │   │   ├─ application/{StudyPlanService, port/StudyPlanRepositoryPort}.java
│   │   │   └─ infrastructure/...
│   │   ├─ studyblock/
│   │   │   ├─ domain/{StudyBlock, FocusEntity (record)}.java
│   │   │   ├─ application/{StudyBlockService, port/StudyBlockRepositoryPort}.java
│   │   │   └─ infrastructure/...
│   │   ├─ studyitem/
│   │   │   ├─ domain/{StudyItem, StudyItemStatus, StudyItemTarget}.java
│   │   │   ├─ application/
│   │   │   │   ├─ StudyItemService.java
│   │   │   │   ├─ StudyItemStateMachine.java               State pattern
│   │   │   │   ├─ StudyItemStatusChangedEvent.java         Observer
│   │   │   │   └─ port/StudyItemRepositoryPort.java
│   │   │   └─ infrastructure/...
│   │   └─ portability/PlanningExporter.java
│   ├─ identity/
│   │   ├─ profile/
│   │   │   ├─ domain/{UserProfile, FederationLink}.java
│   │   │   ├─ application/{UserProfileService, port/UserProfileRepositoryPort}.java
│   │   │   └─ infrastructure/...
│   │   └─ portability/IdentityExporter.java
│   └─ portability/
│       └─ infrastructure/web/FullExportController.java     orquesta todos los exporters
└─ src/main/resources/
    ├─ application.yml                                      común
    ├─ application-dev.yml                                  H2 in-memory
    ├─ application-test.yml                                 SQLite :memory: + Flyway
    ├─ application-prod.yml                                 SQLite + Flyway + ddl-auto: validate
    ├─ logback-spring.xml                                   pattern dev / JSON prod
    ├─ ValidationMessages.properties                        español
    ├─ db/migration/
    │   ├─ V1__init_catalog.sql
    │   ├─ V2__init_journal.sql
    │   ├─ V3__init_planning.sql
    │   ├─ V4__init_identity.sql
    │   ├─ V100__seed_federations.sql
    │   └─ V101__seed_belt_indexes.sql
    └─ schemas/
        ├─ system-flow.schema.v1.json
        ├─ catalog-import.schema.v1.json
        ├─ system-import.schema.v1.json
        └─ ruleset-import.schema.v1.json

src/test/java/com/ossflow/
├─ {bounded-context}/{feature}/                             unit + slice por feature
├─ integration/
│   ├─ CatalogIntegrationTest.java
│   ├─ JournalIntegrationTest.java
│   ├─ PlanningIntegrationTest.java
│   ├─ ImportIntegrationTest.java
│   ├─ SoftDeletePurgeIntegrationTest.java
│   └─ TraceIdPropagationIntegrationTest.java
└─ support/
    ├─ PositionTestData.java
    ├─ TechniqueTestData.java
    └─ ...
```

**Decomposición**: cada bounded context vive en su paquete, con subpaquetes domain/application/infrastructure. Una feature = un paquete autocontenido. La infraestructura transversal vive en `shared/`. Las dependencias entre bounded contexts forman un DAG (catalog → journal/planning, identity independiente, portability conoce a todos).

---

## Tarea 1: Bootstrap del proyecto (Java 25 + Spring Boot 4 + dependencias)

**Files:**
- Modify: `pom.xml`
- Create: `src/main/resources/application.yml` (sustituye los actuales)
- Create: `src/main/resources/application-dev.yml`
- Create: `src/main/resources/application-test.yml`
- Create: `src/main/resources/application-prod.yml`
- Modify: `src/main/resources/config/application.yml` (eliminar)
- Modify: `src/main/resources/config/application-local.yml` (eliminar)
- Modify: `README.md`

- [ ] **Step 1.1: Reemplazar `pom.xml` con la configuración objetivo**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.0.5</version>
        <relativePath/>
    </parent>

    <groupId>com.ossflow</groupId>
    <artifactId>ossflow</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>ossflow</name>
    <description>OssFlow backend - Segundo cerebro para BJJ</description>

    <properties>
        <java.version>25</java.version>
        <mapstruct.version>1.6.3</mapstruct.version>
        <lombok.version>1.18.36</lombok.version>
        <lombok-mapstruct-binding.version>0.2.0</lombok-mapstruct-binding.version>
        <sqlite-jdbc.version>3.47.1.0</sqlite-jdbc.version>
        <springdoc.version>2.7.0</springdoc.version>
        <json-schema-validator.version>1.5.4</json-schema-validator.version>
        <logstash-logback-encoder.version>8.0</logstash-logback-encoder.version>
        <jacoco.version>0.8.12</jacoco.version>
    </properties>

    <dependencies>
        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>
        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-data-jpa</artifactId></dependency>
        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-validation</artifactId></dependency>
        <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-actuator</artifactId></dependency>

        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>

        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.xerial</groupId>
            <artifactId>sqlite-jdbc</artifactId>
            <version>${sqlite-jdbc.version}</version>
        </dependency>

        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
            <version>${mapstruct.version}</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>${springdoc.version}</version>
        </dependency>

        <dependency>
            <groupId>com.networknt</groupId>
            <artifactId>json-schema-validator</artifactId>
            <version>${json-schema-validator.version}</version>
        </dependency>

        <dependency>
            <groupId>net.logstash.logback</groupId>
            <artifactId>logstash-logback-encoder</artifactId>
            <version>${logstash-logback-encoder.version}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                    <annotationProcessorPaths>
                        <path><groupId>org.projectlombok</groupId><artifactId>lombok</artifactId><version>${lombok.version}</version></path>
                        <path><groupId>org.projectlombok</groupId><artifactId>lombok-mapstruct-binding</artifactId><version>${lombok-mapstruct-binding.version}</version></path>
                        <path><groupId>org.mapstruct</groupId><artifactId>mapstruct-processor</artifactId><version>${mapstruct.version}</version></path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>

            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude><groupId>org.projectlombok</groupId><artifactId>lombok</artifactId></exclude>
                    </excludes>
                </configuration>
            </plugin>

            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>${jacoco.version}</version>
                <executions>
                    <execution><id>prepare-agent</id><goals><goal>prepare-agent</goal></goals></execution>
                    <execution><id>report</id><phase>verify</phase><goals><goal>report</goal></goals></execution>
                    <execution>
                        <id>check</id>
                        <phase>verify</phase>
                        <goals><goal>check</goal></goals>
                        <configuration>
                            <rules>
                                <rule>
                                    <element>BUNDLE</element>
                                    <limits>
                                        <limit><counter>LINE</counter><value>COVEREDRATIO</value><minimum>0.75</minimum></limit>
                                    </limits>
                                </rule>
                            </rules>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 1.2: Eliminar configuraciones viejas y crear las nuevas**

```bash
rm -f src/main/resources/config/application.yml src/main/resources/config/application-local.yml
rmdir src/main/resources/config 2>/dev/null || true
```

Crear `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: ossflow
  profiles:
    active: dev
  jpa:
    open-in-view: false
    properties:
      hibernate:
        physical_naming_strategy: org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy
server:
  port: 8080
  error:
    include-message: never
    include-stacktrace: never
management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: never
```

Crear `src/main/resources/application-dev.yml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:ossflowdb
    driver-class-name: org.h2.Driver
    username: sa
    password: ""
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  h2:
    console:
      enabled: true
      path: /h2-console
  flyway:
    enabled: false
springdoc:
  swagger-ui:
    enabled: true
logging:
  level:
    com.ossflow: DEBUG
```

Crear `src/main/resources/application-test.yml`:

```yaml
spring:
  datasource:
    url: jdbc:sqlite::memory:
    driver-class-name: org.sqlite.JDBC
    username: ""
    password: ""
  jpa:
    database-platform: org.hibernate.community.dialect.SQLiteDialect
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
    locations: classpath:db/migration
logging:
  level:
    com.ossflow: INFO
```

Crear `src/main/resources/application-prod.yml`:

```yaml
spring:
  datasource:
    url: jdbc:sqlite:${OSSFLOW_DB_PATH:/data/ossflow.db}
    driver-class-name: org.sqlite.JDBC
  jpa:
    database-platform: org.hibernate.community.dialect.SQLiteDialect
    hibernate:
      ddl-auto: validate
    show-sql: false
  flyway:
    enabled: true
springdoc:
  swagger-ui:
    enabled: false
  api-docs:
    enabled: false
```

- [ ] **Step 1.3: Verificar compilación con la nueva configuración**

Run: `mvn -B clean compile -DskipTests`
Expected: BUILD SUCCESS. Si falla por incompatibilidad del código existente con Spring Boot 4 / Java 25, **NO arreglar todavía**: el código viejo se reescribe en tareas siguientes. En su lugar, ejecutar el siguiente paso para limpiar.

- [ ] **Step 1.4: Limpiar código viejo incompatible**

```bash
rm -rf src/main/java/com/ossflow/technique
rm -rf src/test/java/com/ossflow/technique
```

Re-ejecutar: `mvn -B clean compile -DskipTests`
Expected: BUILD SUCCESS con solo `OssflowApplication.java`.

- [ ] **Step 1.5: Verificar que la app arranca con perfil dev**

Run: `mvn -B spring-boot:run -Dspring-boot.run.profiles=dev` (en background, kill tras 15s)

Verificar en logs: `Started OssflowApplication`, `H2 console available at /h2-console`.

- [ ] **Step 1.6: Actualizar README.md**

```markdown
# OssFlow

Segundo cerebro técnico para Brazilian Jiu-Jitsu. Backend Spring Boot 4 (Java 25) con bounded contexts, SQLite/H2 y CRUD completo.

## Stack
- Java 25, Spring Boot 4.x
- SQLite (prod) + H2 (dev) + Flyway
- JPA/Hibernate 6, MapStruct, Lombok
- JUnit 5, Mockito, AssertJ, JaCoCo

## Perfiles
- `dev` (default local): H2 in-memory, swagger habilitado, DDL update
- `test`: SQLite :memory:, Flyway aplicado
- `prod`: SQLite fichero, Flyway + ddl-auto validate

## Comandos
```bash
mvn clean verify          # build + tests + jacoco
mvn spring-boot:run       # arrancar con perfil dev
```

## Documentación
- Spec: `docs/superpowers/specs/2026-05-06-ossflow-rediseno-design.md`
- Reglas: `docs/superpowers/specs/coding-rules.md`
- Códigos error: `docs/superpowers/specs/error-codes.md`
```

- [ ] **Step 1.7: Commit**

```bash
git add pom.xml src/main/resources README.md
git rm -rf src/main/java/com/ossflow/technique src/test/java/com/ossflow/technique 2>/dev/null || true
git commit -m "feat(bootstrap): Java 25 + Spring Boot 4 + SQLite/H2/Flyway

- Sube Java 17 → 25 (nueva LTS, combinación canónica con Spring Boot 4)
- Mantiene Spring Boot 4 (versión inicial) actualizado a 4.0.5
- Drop dependencias graphql y postgresql (decisión spec)
- Añade sqlite-jdbc, flyway, springdoc-openapi, json-schema-validator,
  logstash-logback-encoder, jacoco
- Reorganiza configuración en application{,-dev,-test,-prod}.yml
- Limpia código viejo incompatible (se reescribe en fases siguientes)"
```

---

## Tarea 2: Infraestructura compartida — BaseEntity, JpaAuditing y soft delete

**Files:**
- Create: `src/main/java/com/ossflow/shared/persistence/BaseEntity.java`
- Create: `src/main/java/com/ossflow/shared/config/JpaAuditingConfig.java`
- Create: `src/main/resources/db/migration/V1__init_catalog.sql` (esqueleto vacío para que Flyway no falle)
- Create: `src/test/java/com/ossflow/shared/persistence/BaseEntityTest.java`

- [ ] **Step 2.1: Crear `JpaAuditingConfig`**

```java
package com.ossflow.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
```

- [ ] **Step 2.2: Crear `BaseEntity`**

```java
package com.ossflow.shared.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId = 1L;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "purge_at")
    private Instant purgeAt;

    public boolean isSoftDeleted() {
        return deletedAt != null;
    }

    public void softDelete(Instant now, java.time.Duration retention) {
        this.deletedAt = now;
        this.purgeAt = now.plus(retention);
    }

    public void restore() {
        this.deletedAt = null;
        this.purgeAt = null;
    }
}
```

- [ ] **Step 2.3: Crear migración Flyway esqueleto V1**

```sql
-- src/main/resources/db/migration/V1__init_catalog.sql
-- Esqueleto inicial; se rellena con tablas concretas en Tarea 4 y siguientes.
CREATE TABLE IF NOT EXISTS schema_marker (id INTEGER PRIMARY KEY);
```

- [ ] **Step 2.4: Escribir test unitario para `BaseEntity`**

```java
package com.ossflow.shared.persistence;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class BaseEntityTest {

    static class DummyEntity extends BaseEntity {}

    @Test
    void should_be_not_soft_deleted_by_default() {
        var entity = new DummyEntity();
        assertThat(entity.isSoftDeleted()).isFalse();
    }

    @Test
    void should_set_deleted_at_and_purge_at_on_soft_delete() {
        var entity = new DummyEntity();
        var now = Instant.parse("2026-05-06T10:00:00Z");

        entity.softDelete(now, Duration.ofDays(30));

        assertThat(entity.isSoftDeleted()).isTrue();
        assertThat(entity.getDeletedAt()).isEqualTo(now);
        assertThat(entity.getPurgeAt()).isEqualTo(now.plus(Duration.ofDays(30)));
    }

    @Test
    void should_clear_timestamps_on_restore() {
        var entity = new DummyEntity();
        entity.softDelete(Instant.now(), Duration.ofDays(30));

        entity.restore();

        assertThat(entity.isSoftDeleted()).isFalse();
        assertThat(entity.getDeletedAt()).isNull();
        assertThat(entity.getPurgeAt()).isNull();
    }
}
```

- [ ] **Step 2.5: Run test y verificar verde**

Run: `mvn -B test -Dtest=BaseEntityTest`
Expected: Tests run: 3, Failures: 0.

- [ ] **Step 2.6: Commit**

```bash
git add src/main/java/com/ossflow/shared src/main/resources/db src/test/java/com/ossflow/shared
git commit -m "feat(shared): BaseEntity con auditoría y soft delete + JpaAuditingConfig

- BaseEntity: id, ownerId (multi-ready), audit fields, version (optimistic
  locking), deletedAt + purgeAt para soft delete con ventana de 30 días
- JpaAuditingConfig habilita @CreatedDate/@LastModifiedDate
- Flyway V1 esqueleto (se rellena en Tarea 4)
- Tests unitarios cubren softDelete/restore"
```

---

## Tarea 3: Excepciones y traceId — jerarquía OssFlowException + ApiError + GlobalExceptionHandler + RequestTracingFilter

**Files:**
- Create: `src/main/java/com/ossflow/shared/exception/OssFlowException.java`
- Create: `src/main/java/com/ossflow/shared/exception/NotFoundException.java`
- Create: `src/main/java/com/ossflow/shared/exception/ConflictException.java`
- Create: `src/main/java/com/ossflow/shared/exception/DuplicateNameException.java`
- Create: `src/main/java/com/ossflow/shared/exception/ReferenceInUseException.java`
- Create: `src/main/java/com/ossflow/shared/exception/InvalidStateTransitionException.java`
- Create: `src/main/java/com/ossflow/shared/exception/UnprocessableException.java`
- Create: `src/main/java/com/ossflow/shared/exception/JsonSchemaViolationException.java`
- Create: `src/main/java/com/ossflow/shared/exception/SemanticValidationException.java`
- Create: `src/main/java/com/ossflow/shared/exception/ReferentialIntegrityException.java`
- Create: `src/main/java/com/ossflow/shared/exception/BadRequestException.java`
- Create: `src/main/java/com/ossflow/shared/exception/ApiError.java`
- Create: `src/main/java/com/ossflow/shared/exception/GlobalExceptionHandler.java`
- Create: `src/main/java/com/ossflow/shared/web/RequestTracingFilter.java`
- Create: `src/main/resources/ValidationMessages.properties`
- Create: `src/test/java/com/ossflow/shared/exception/GlobalExceptionHandlerTest.java`
- Create: `src/test/java/com/ossflow/shared/web/RequestTracingFilterTest.java`

- [ ] **Step 3.1: Crear `OssFlowException` raíz**

```java
package com.ossflow.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public abstract class OssFlowException extends RuntimeException {

    private final String errorCode;
    private final Map<String, Object> details;

    protected OssFlowException(String errorCode, String message, Map<String, Object> details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details == null ? Map.of() : Map.copyOf(details);
    }

    public abstract HttpStatus getHttpStatus();
}
```

- [ ] **Step 3.2: Crear las cinco subclases base con su HttpStatus**

`NotFoundException.java`:

```java
package com.ossflow.shared.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class NotFoundException extends OssFlowException {
    public NotFoundException(String errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
    public NotFoundException(String errorCode, String message) {
        this(errorCode, message, Map.of());
    }
    @Override public HttpStatus getHttpStatus() { return HttpStatus.NOT_FOUND; }
}
```

`ConflictException.java`:

```java
package com.ossflow.shared.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class ConflictException extends OssFlowException {
    public ConflictException(String errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
    public ConflictException(String errorCode, String message) {
        this(errorCode, message, Map.of());
    }
    @Override public HttpStatus getHttpStatus() { return HttpStatus.CONFLICT; }
}
```

`UnprocessableException.java`:

```java
package com.ossflow.shared.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class UnprocessableException extends OssFlowException {
    public UnprocessableException(String errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
    public UnprocessableException(String errorCode, String message) {
        this(errorCode, message, Map.of());
    }
    @Override public HttpStatus getHttpStatus() { return HttpStatus.UNPROCESSABLE_ENTITY; }
}
```

`BadRequestException.java`:

```java
package com.ossflow.shared.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class BadRequestException extends OssFlowException {
    public BadRequestException(String errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
    public BadRequestException(String errorCode, String message) {
        this(errorCode, message, Map.of());
    }
    @Override public HttpStatus getHttpStatus() { return HttpStatus.BAD_REQUEST; }
}
```

- [ ] **Step 3.3: Crear las subclases especializadas de Conflict y Unprocessable**

`DuplicateNameException.java`:

```java
package com.ossflow.shared.exception;

import java.util.Map;

public class DuplicateNameException extends ConflictException {
    public DuplicateNameException(String errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
}
```

`ReferenceInUseException.java`:

```java
package com.ossflow.shared.exception;

import java.util.Map;

public class ReferenceInUseException extends ConflictException {
    public ReferenceInUseException(String errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
}
```

`InvalidStateTransitionException.java`:

```java
package com.ossflow.shared.exception;

import java.util.Map;

public class InvalidStateTransitionException extends ConflictException {
    public InvalidStateTransitionException(String errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
}
```

`JsonSchemaViolationException.java`:

```java
package com.ossflow.shared.exception;

import java.util.Map;

public class JsonSchemaViolationException extends UnprocessableException {
    public JsonSchemaViolationException(String errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
}
```

`SemanticValidationException.java`:

```java
package com.ossflow.shared.exception;

import java.util.Map;

public class SemanticValidationException extends UnprocessableException {
    public SemanticValidationException(String errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
}
```

`ReferentialIntegrityException.java`:

```java
package com.ossflow.shared.exception;

import java.util.Map;

public class ReferentialIntegrityException extends UnprocessableException {
    public ReferentialIntegrityException(String errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
}
```

- [ ] **Step 3.4: Crear `ApiError` (record con shape uniforme)**

```java
package com.ossflow.shared.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
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
    public record FieldError(String field, Object rejectedValue, String message) {}
}
```

- [ ] **Step 3.5: Crear `RequestTracingFilter`**

```java
package com.ossflow.shared.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
public class RequestTracingFilter extends OncePerRequestFilter {

    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String TRACE_ID_MDC_KEY = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }
        MDC.put(TRACE_ID_MDC_KEY, traceId);
        response.setHeader(TRACE_ID_HEADER, traceId);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID_MDC_KEY);
        }
    }
}
```

- [ ] **Step 3.6: Crear `GlobalExceptionHandler`**

```java
package com.ossflow.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OssFlowException.class)
    ResponseEntity<ApiError> handleDomain(OssFlowException ex, HttpServletRequest req) {
        log.warn("Domain error [{}] at {}: {}", ex.getErrorCode(), req.getRequestURI(), ex.getMessage());
        return build(ex.getHttpStatus(), ex.getErrorCode(), ex.getMessage(), req, null, ex.getDetails());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ApiError.FieldError> fields = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ApiError.FieldError(fe.getField(), fe.getRejectedValue(), fe.getDefaultMessage()))
                .toList();
        log.warn("Validation failed at {}: {} field errors", req.getRequestURI(), fields.size());
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "La petición contiene errores de validación", req, fields, null);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ApiError> handleBadRequest(Exception ex, HttpServletRequest req) {
        log.warn("Bad request at {}: {}", req.getRequestURI(), ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Petición malformada", req, null, null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiError> handleConstraint(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("Constraint violation at {}: {}", req.getRequestURI(), ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT, "CONSTRAINT_VIOLATION", "Restricción de integridad violada", req, null, null);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest req) {
        String traceId = MDC.get("traceId");
        log.error("Unexpected error [traceId={}] at {}", traceId, req.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Error inesperado", req, null, null);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String code, String message,
                                           HttpServletRequest req,
                                           List<ApiError.FieldError> fields,
                                           Map<String, Object> details) {
        var error = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                code,
                message,
                req.getRequestURI(),
                MDC.get("traceId"),
                fields,
                details == null || details.isEmpty() ? null : details
        );
        return ResponseEntity.status(status).body(error);
    }
}
```

- [ ] **Step 3.7: Crear `ValidationMessages.properties`**

```properties
jakarta.validation.constraints.NotBlank.message=no puede estar vacío
jakarta.validation.constraints.NotNull.message=no puede ser nulo
jakarta.validation.constraints.NotEmpty.message=no puede estar vacío
jakarta.validation.constraints.Size.message=debe tener entre {min} y {max} caracteres
jakarta.validation.constraints.Min.message=debe ser al menos {value}
jakarta.validation.constraints.Max.message=debe ser como máximo {value}
jakarta.validation.constraints.Positive.message=debe ser positivo
jakarta.validation.constraints.PositiveOrZero.message=debe ser positivo o cero
jakarta.validation.constraints.Pattern.message=no cumple el formato esperado
```

- [ ] **Step 3.8: Test unitario del `RequestTracingFilter`**

```java
package com.ossflow.shared.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RequestTracingFilterTest {

    @Test
    void should_generate_trace_id_when_header_missing() throws Exception {
        var filter = new RequestTracingFilter();
        var req = mock(HttpServletRequest.class);
        var res = mock(HttpServletResponse.class);
        var chain = mock(FilterChain.class);
        when(req.getHeader("X-Trace-Id")).thenReturn(null);

        filter.doFilter(req, res, chain);

        verify(res).setHeader(eq("X-Trace-Id"), argThat(v -> v != null && !v.isBlank()));
        assertThat(MDC.get("traceId")).isNull();
    }

    @Test
    void should_propagate_existing_trace_id() throws Exception {
        var filter = new RequestTracingFilter();
        var req = mock(HttpServletRequest.class);
        var res = mock(HttpServletResponse.class);
        var chain = mock(FilterChain.class);
        when(req.getHeader("X-Trace-Id")).thenReturn("abc-123");

        filter.doFilter(req, res, chain);

        verify(res).setHeader("X-Trace-Id", "abc-123");
    }
}
```

- [ ] **Step 3.9: Run tests del filtro**

Run: `mvn -B test -Dtest=RequestTracingFilterTest`
Expected: Tests run: 2, Failures: 0.

- [ ] **Step 3.10: Test slice del `GlobalExceptionHandler`**

```java
package com.ossflow.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void should_map_NotFoundException_to_404() {
        var req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/api/v1/x");
        MDC.put("traceId", "trace-1");

        var response = handler.handleDomain(
                new NotFoundException("X_NOT_FOUND", "no existe", Map.of("id", 5)),
                req
        );

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        var body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.code()).isEqualTo("X_NOT_FOUND");
        assertThat(body.message()).isEqualTo("no existe");
        assertThat(body.traceId()).isEqualTo("trace-1");
        assertThat(body.details()).containsEntry("id", 5);
        MDC.clear();
    }

    @Test
    void should_map_DuplicateNameException_to_409() {
        var req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/x");

        var response = handler.handleDomain(
                new DuplicateNameException("X_DUP", "duplicado", Map.of()),
                req
        );

        assertThat(response.getStatusCodeValue()).isEqualTo(409);
        assertThat(response.getBody().code()).isEqualTo("X_DUP");
    }

    @Test
    void should_map_unexpected_to_500_with_trace_id() {
        var req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/x");
        MDC.put("traceId", "trace-err");

        var response = handler.handleUnexpected(new RuntimeException("boom"), req);

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        assertThat(response.getBody().code()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.getBody().traceId()).isEqualTo("trace-err");
        MDC.clear();
    }
}
```

- [ ] **Step 3.11: Run tests del handler**

Run: `mvn -B test -Dtest=GlobalExceptionHandlerTest`
Expected: Tests run: 3, Failures: 0.

- [ ] **Step 3.12: Commit**

```bash
git add src/main/java/com/ossflow/shared/exception src/main/java/com/ossflow/shared/web src/main/resources/ValidationMessages.properties src/test/java/com/ossflow/shared
git commit -m "feat(shared): jerarquía OssFlowException + GlobalExceptionHandler + traceId

- OssFlowException raíz abstracta + 4 subclases base (NotFound, Conflict,
  Unprocessable, BadRequest) + 6 especializaciones (DuplicateName,
  ReferenceInUse, InvalidStateTransition, JsonSchemaViolation,
  SemanticValidation, ReferentialIntegrity)
- ApiError record con shape uniforme (timestamp, status, code, message,
  path, traceId, fieldErrors, details)
- GlobalExceptionHandler @RestControllerAdvice mapea: domain → 4xx,
  validación → 400 con fieldErrors, constraint → 409, catch-all → 500
- RequestTracingFilter (OncePerRequestFilter): genera o respeta
  X-Trace-Id, MDC traceId, devuelve mismo header en respuesta
- ValidationMessages.properties en español
- Tests unitarios + slice cubren los caminos principales"
```

---

## Tarea 4: Migración Flyway del catálogo + entidades JPA Position y Technique

**Files:**
- Modify: `src/main/resources/db/migration/V1__init_catalog.sql` (rellenar con tablas reales)
- Create: `src/main/java/com/ossflow/catalog/position/domain/Position.java`
- Create: `src/main/java/com/ossflow/catalog/position/domain/PositionType.java`
- Create: `src/main/java/com/ossflow/catalog/position/domain/Visibility.java`
- Create: `src/main/java/com/ossflow/catalog/technique/domain/Technique.java`
- Create: `src/main/java/com/ossflow/catalog/technique/domain/TechniqueCategory.java`
- Create: `src/main/java/com/ossflow/catalog/technique/domain/Belt.java`
- Create: `src/main/java/com/ossflow/catalog/technique/domain/Modality.java`
- Create: `src/main/java/com/ossflow/catalog/position/infrastructure/persistence/PositionEntity.java`
- Create: `src/main/java/com/ossflow/catalog/position/infrastructure/persistence/PositionJpaRepository.java`
- Create: `src/main/java/com/ossflow/catalog/technique/infrastructure/persistence/TechniqueEntity.java`
- Create: `src/main/java/com/ossflow/catalog/technique/infrastructure/persistence/TechniqueJpaRepository.java`
- Create: `src/test/java/com/ossflow/catalog/position/infrastructure/persistence/PositionJpaRepositoryTest.java`

- [ ] **Step 4.1: Reescribir `V1__init_catalog.sql` con las tablas position y technique**

```sql
-- src/main/resources/db/migration/V1__init_catalog.sql

CREATE TABLE position (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    owner_id        BIGINT      NOT NULL DEFAULT 1,
    name            VARCHAR(120) NOT NULL,
    type            VARCHAR(30)  NOT NULL,
    description     TEXT,
    visibility      VARCHAR(10) NOT NULL DEFAULT 'PRIVATE',
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP    NOT NULL,
    version         BIGINT       NOT NULL DEFAULT 0,
    deleted_at      TIMESTAMP,
    purge_at        TIMESTAMP
);

CREATE UNIQUE INDEX ux_position_owner_name_active
    ON position(owner_id, name) WHERE deleted_at IS NULL;
CREATE INDEX ix_position_owner_deleted ON position(owner_id, deleted_at);

CREATE TABLE technique (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    owner_id            BIGINT       NOT NULL DEFAULT 1,
    name                VARCHAR(120) NOT NULL,
    category            VARCHAR(30)  NOT NULL,
    description         TEXT,
    youtube_url         VARCHAR(500),
    minimum_belt        VARCHAR(15)  NOT NULL,
    modality            VARCHAR(10)  NOT NULL,
    start_position_id   BIGINT       NOT NULL,
    end_position_id     BIGINT,
    visibility          VARCHAR(10)  NOT NULL DEFAULT 'PRIVATE',
    created_at          TIMESTAMP    NOT NULL,
    updated_at          TIMESTAMP    NOT NULL,
    version             BIGINT       NOT NULL DEFAULT 0,
    deleted_at          TIMESTAMP,
    purge_at            TIMESTAMP,
    FOREIGN KEY (start_position_id) REFERENCES position(id),
    FOREIGN KEY (end_position_id)   REFERENCES position(id)
);

CREATE UNIQUE INDEX ux_technique_owner_name_active
    ON technique(owner_id, name) WHERE deleted_at IS NULL;
CREATE INDEX ix_technique_start_position ON technique(start_position_id);
CREATE INDEX ix_technique_end_position   ON technique(end_position_id);
CREATE INDEX ix_technique_category       ON technique(category);
```

- [ ] **Step 4.2: Crear enums de dominio**

`Visibility.java`:

```java
package com.ossflow.catalog.position.domain;

public enum Visibility { PRIVATE, PUBLIC }
```

`PositionType.java`:

```java
package com.ossflow.catalog.position.domain;

public enum PositionType { TOP, BOTTOM, STANDING, GROUND_NEUTRAL, SUBMITTED }
```

`TechniqueCategory.java`:

```java
package com.ossflow.catalog.technique.domain;

public enum TechniqueCategory { SUBMISSION, SWEEP, PASS, TAKEDOWN, ESCAPE, TRANSITION }
```

`Belt.java`:

```java
package com.ossflow.catalog.technique.domain;

public enum Belt { WHITE, BLUE, PURPLE, BROWN, BLACK }
```

`Modality.java`:

```java
package com.ossflow.catalog.technique.domain;

public enum Modality { GI, NOGI, BOTH }
```

- [ ] **Step 4.3: Crear records de dominio**

`Position.java`:

```java
package com.ossflow.catalog.position.domain;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record Position(
        Long id,
        Long ownerId,
        String name,
        PositionType type,
        String description,
        Visibility visibility,
        Instant createdAt,
        Instant updatedAt,
        Long version,
        Instant deletedAt,
        Instant purgeAt
) {}
```

`Technique.java`:

```java
package com.ossflow.catalog.technique.domain;

import com.ossflow.catalog.position.domain.Visibility;
import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record Technique(
        Long id,
        Long ownerId,
        String name,
        TechniqueCategory category,
        String description,
        String youtubeUrl,
        Belt minimumBelt,
        Modality modality,
        Long startPositionId,
        Long endPositionId,
        Visibility visibility,
        Instant createdAt,
        Instant updatedAt,
        Long version,
        Instant deletedAt,
        Instant purgeAt
) {}
```

- [ ] **Step 4.4: Crear `PositionEntity` con `@SQLDelete` y `@Where`**

```java
package com.ossflow.catalog.position.infrastructure.persistence;

import com.ossflow.catalog.position.domain.PositionType;
import com.ossflow.catalog.position.domain.Visibility;
import com.ossflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "position")
@SQLDelete(sql = "UPDATE position SET deleted_at = CURRENT_TIMESTAMP, purge_at = datetime('now', '+30 days') WHERE id = ? AND version = ?")
@SQLRestriction("deleted_at IS NULL")
public class PositionEntity extends BaseEntity {

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private PositionType type;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 10)
    private Visibility visibility;
}
```

- [ ] **Step 4.5: Crear `PositionJpaRepository`**

```java
package com.ossflow.catalog.position.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PositionJpaRepository extends JpaRepository<PositionEntity, Long> {

    Optional<PositionEntity> findByIdAndOwnerId(Long id, Long ownerId);

    Page<PositionEntity> findByOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT p FROM PositionEntity p WHERE p.ownerId = :ownerId AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<PositionEntity> findByOwnerIdAndNameContainingIgnoreCase(Long ownerId, String name, Pageable pageable);

    boolean existsByOwnerIdAndName(Long ownerId, String name);
}
```

- [ ] **Step 4.6: Crear `TechniqueEntity` y `TechniqueJpaRepository`**

`TechniqueEntity.java`:

```java
package com.ossflow.catalog.technique.infrastructure.persistence;

import com.ossflow.catalog.position.domain.Visibility;
import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import com.ossflow.catalog.technique.domain.TechniqueCategory;
import com.ossflow.shared.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "technique")
@SQLDelete(sql = "UPDATE technique SET deleted_at = CURRENT_TIMESTAMP, purge_at = datetime('now', '+30 days') WHERE id = ? AND version = ?")
@SQLRestriction("deleted_at IS NULL")
public class TechniqueEntity extends BaseEntity {

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private TechniqueCategory category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "youtube_url", length = 500)
    private String youtubeUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "minimum_belt", nullable = false, length = 15)
    private Belt minimumBelt;

    @Enumerated(EnumType.STRING)
    @Column(name = "modality", nullable = false, length = 10)
    private Modality modality;

    @Column(name = "start_position_id", nullable = false)
    private Long startPositionId;

    @Column(name = "end_position_id")
    private Long endPositionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 10)
    private Visibility visibility;
}
```

`TechniqueJpaRepository.java`:

```java
package com.ossflow.catalog.technique.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TechniqueJpaRepository extends JpaRepository<TechniqueEntity, Long> {

    Optional<TechniqueEntity> findByIdAndOwnerId(Long id, Long ownerId);

    Page<TechniqueEntity> findByOwnerId(Long ownerId, Pageable pageable);

    boolean existsByOwnerIdAndName(Long ownerId, String name);

    long countByStartPositionIdOrEndPositionId(Long startId, Long endId);
}
```

- [ ] **Step 4.7: Test de integración del repositorio Position contra SQLite**

```java
package com.ossflow.catalog.position.infrastructure.persistence;

import com.ossflow.catalog.position.domain.PositionType;
import com.ossflow.catalog.position.domain.Visibility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class PositionJpaRepositoryTest {

    @Autowired PositionJpaRepository repository;

    @Test
    void should_persist_and_retrieve_position() {
        var entity = PositionEntity.builder()
                .name("Guardia Cerrada")
                .type(PositionType.BOTTOM)
                .visibility(Visibility.PRIVATE)
                .build();
        entity.setOwnerId(1L);

        var saved = repository.save(entity);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getVersion()).isZero();
    }

    @Test
    void should_filter_by_name_case_insensitive() {
        var a = PositionEntity.builder().name("Guardia Cerrada").type(PositionType.BOTTOM).visibility(Visibility.PRIVATE).build();
        a.setOwnerId(1L);
        var b = PositionEntity.builder().name("Montada").type(PositionType.TOP).visibility(Visibility.PRIVATE).build();
        b.setOwnerId(1L);
        repository.save(a);
        repository.save(b);

        var page = repository.findByOwnerIdAndNameContainingIgnoreCase(1L, "guard", PageRequest.of(0, 10));

        assertThat(page.getContent()).extracting(PositionEntity::getName).containsExactly("Guardia Cerrada");
    }
}
```

- [ ] **Step 4.8: Run repository test**

Run: `mvn -B test -Dtest=PositionJpaRepositoryTest -Dspring.profiles.active=test`
Expected: Tests run: 2, Failures: 0. Verifica que Flyway aplica V1 contra SQLite `:memory:` correctamente.

- [ ] **Step 4.9: Commit**

```bash
git add src/main/resources/db src/main/java/com/ossflow/catalog src/test/java/com/ossflow/catalog
git commit -m "feat(catalog): migración V1 + entidades Position y Technique

- V1__init_catalog.sql: tablas position y technique con UNIQUE per owner,
  índices, FKs end/start position; soft delete columnas
- Domain records Position y Technique con @Builder
- Enums: PositionType, Visibility (compartido), TechniqueCategory, Belt, Modality
- @Entity con @SQLDelete (UPDATE en lugar de DELETE) y @SQLRestriction
  filtra deleted_at IS NULL
- JpaRepositories con queries derivadas y custom @Query case-insensitive
- @DataJpaTest contra SQLite :memory: verde"
```

---

## Tarea 5: Puerto de salida + adapter + mapper de persistencia para Position

**Files:**
- Create: `src/main/java/com/ossflow/catalog/position/application/port/PositionRepositoryPort.java`
- Create: `src/main/java/com/ossflow/catalog/position/infrastructure/persistence/PositionPersistenceMapper.java`
- Create: `src/main/java/com/ossflow/catalog/position/infrastructure/persistence/PositionPersistenceAdapter.java`
- Create: `src/test/java/com/ossflow/catalog/position/infrastructure/persistence/PositionPersistenceAdapterTest.java`

- [ ] **Step 5.1: Crear puerto de salida**

```java
package com.ossflow.catalog.position.application.port;

import com.ossflow.catalog.position.domain.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PositionRepositoryPort {
    Position save(Position position);
    Optional<Position> findById(Long id, Long ownerId);
    Page<Position> findAll(Long ownerId, String nameFilter, Pageable pageable);
    boolean existsByName(Long ownerId, String name);
    void softDelete(Long id, Long ownerId);
    Optional<Position> findInTrashById(Long id, Long ownerId);
    Position restore(Long id, Long ownerId);
    Page<Position> findTrash(Long ownerId, Pageable pageable);
}
```

- [ ] **Step 5.2: Crear `PositionPersistenceMapper` con MapStruct**

```java
package com.ossflow.catalog.position.infrastructure.persistence;

import com.ossflow.catalog.position.domain.Position;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PositionPersistenceMapper {

    Position toDomain(PositionEntity entity);

    PositionEntity toEntity(Position domain);

    void updateEntity(Position domain, @MappingTarget PositionEntity entity);
}
```

- [ ] **Step 5.3: Crear `PositionPersistenceAdapter`**

```java
package com.ossflow.catalog.position.infrastructure.persistence;

import com.ossflow.catalog.position.application.port.PositionRepositoryPort;
import com.ossflow.catalog.position.domain.Position;
import com.ossflow.shared.exception.NotFoundException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PositionPersistenceAdapter implements PositionRepositoryPort {

    private final PositionJpaRepository repository;
    private final PositionPersistenceMapper mapper;
    private final EntityManager em;

    @Override
    public Position save(Position position) {
        PositionEntity entity = position.id() == null
                ? mapper.toEntity(position)
                : repository.findByIdAndOwnerId(position.id(), position.ownerId())
                    .orElseThrow(() -> new NotFoundException("POSITION_NOT_FOUND",
                            "No existe la posición con id %d".formatted(position.id()),
                            Map.of("positionId", position.id())));
        if (position.id() != null) {
            mapper.updateEntity(position, entity);
        }
        if (entity.getOwnerId() == null) entity.setOwnerId(position.ownerId());
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Position> findById(Long id, Long ownerId) {
        return repository.findByIdAndOwnerId(id, ownerId).map(mapper::toDomain);
    }

    @Override
    public Page<Position> findAll(Long ownerId, String nameFilter, Pageable pageable) {
        Page<PositionEntity> page = (nameFilter == null || nameFilter.isBlank())
                ? repository.findByOwnerId(ownerId, pageable)
                : repository.findByOwnerIdAndNameContainingIgnoreCase(ownerId, nameFilter, pageable);
        return page.map(mapper::toDomain);
    }

    @Override
    public boolean existsByName(Long ownerId, String name) {
        return repository.existsByOwnerIdAndName(ownerId, name);
    }

    @Override
    @Transactional
    public void softDelete(Long id, Long ownerId) {
        var entity = repository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new NotFoundException("POSITION_NOT_FOUND",
                        "No existe la posición con id %d".formatted(id), Map.of("positionId", id)));
        repository.delete(entity); // dispara @SQLDelete
    }

    @Override
    public Optional<Position> findInTrashById(Long id, Long ownerId) {
        var query = em.createNativeQuery(
                "SELECT * FROM position WHERE id = ?1 AND owner_id = ?2 AND deleted_at IS NOT NULL",
                PositionEntity.class);
        query.setParameter(1, id);
        query.setParameter(2, ownerId);
        var list = query.getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(mapper.toDomain((PositionEntity) list.get(0)));
    }

    @Override
    @Transactional
    public Position restore(Long id, Long ownerId) {
        var query = em.createNativeQuery(
                "UPDATE position SET deleted_at = NULL, purge_at = NULL, updated_at = ?1 WHERE id = ?2 AND owner_id = ?3 AND deleted_at IS NOT NULL");
        query.setParameter(1, Instant.now());
        query.setParameter(2, id);
        query.setParameter(3, ownerId);
        int updated = query.executeUpdate();
        if (updated == 0) {
            throw new NotFoundException("POSITION_NOT_FOUND",
                    "Posición no encontrada en papelera", Map.of("positionId", id));
        }
        em.clear();
        return findById(id, ownerId).orElseThrow();
    }

    @Override
    public Page<Position> findTrash(Long ownerId, Pageable pageable) {
        // Para el listado de trash desactivamos el @SQLRestriction usando native query
        var count = em.createNativeQuery(
                "SELECT COUNT(*) FROM position WHERE owner_id = ?1 AND deleted_at IS NOT NULL");
        count.setParameter(1, ownerId);
        long total = ((Number) count.getSingleResult()).longValue();

        var query = em.createNativeQuery(
                "SELECT * FROM position WHERE owner_id = ?1 AND deleted_at IS NOT NULL ORDER BY deleted_at DESC LIMIT ?2 OFFSET ?3",
                PositionEntity.class);
        query.setParameter(1, ownerId);
        query.setParameter(2, pageable.getPageSize());
        query.setParameter(3, pageable.getOffset());
        @SuppressWarnings("unchecked")
        var list = (java.util.List<PositionEntity>) query.getResultList();
        return new org.springframework.data.domain.PageImpl<>(
                list.stream().map(mapper::toDomain).toList(),
                pageable,
                total);
    }
}
```

- [ ] **Step 5.4: Test integración del adapter**

```java
package com.ossflow.catalog.position.infrastructure.persistence;

import com.ossflow.catalog.position.domain.Position;
import com.ossflow.catalog.position.domain.PositionType;
import com.ossflow.catalog.position.domain.Visibility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PositionPersistenceAdapterTest {

    @Autowired PositionPersistenceAdapter adapter;

    @Test
    void should_save_find_and_softDelete_position() {
        var saved = adapter.save(Position.builder()
                .ownerId(1L)
                .name("Guardia Cerrada")
                .type(PositionType.BOTTOM)
                .visibility(Visibility.PRIVATE)
                .build());

        assertThat(saved.id()).isNotNull();
        assertThat(adapter.findById(saved.id(), 1L)).isPresent();

        adapter.softDelete(saved.id(), 1L);

        assertThat(adapter.findById(saved.id(), 1L)).isEmpty();
        assertThat(adapter.findInTrashById(saved.id(), 1L)).isPresent();
    }

    @Test
    void should_restore_position_from_trash() {
        var saved = adapter.save(Position.builder()
                .ownerId(1L).name("Montada").type(PositionType.TOP).visibility(Visibility.PRIVATE).build());
        adapter.softDelete(saved.id(), 1L);

        var restored = adapter.restore(saved.id(), 1L);

        assertThat(restored.deletedAt()).isNull();
        assertThat(adapter.findById(saved.id(), 1L)).isPresent();
    }

    @Test
    void should_filter_trash_by_owner() {
        var saved = adapter.save(Position.builder().ownerId(1L).name("Espalda").type(PositionType.TOP).visibility(Visibility.PRIVATE).build());
        adapter.softDelete(saved.id(), 1L);

        var trash = adapter.findTrash(1L, PageRequest.of(0, 10));

        assertThat(trash.getTotalElements()).isEqualTo(1);
    }
}
```

- [ ] **Step 5.5: Run test**

Run: `mvn -B test -Dtest=PositionPersistenceAdapterTest -Dspring.profiles.active=test`
Expected: Tests run: 3, Failures: 0.

- [ ] **Step 5.6: Commit**

```bash
git add src/main/java/com/ossflow/catalog/position/application src/main/java/com/ossflow/catalog/position/infrastructure/persistence src/test/java/com/ossflow/catalog/position/infrastructure/persistence/PositionPersistenceAdapterTest.java
git commit -m "feat(catalog/position): puerto + adapter + mapper persistencia

- PositionRepositoryPort interfaz (puerto out)
- PositionPersistenceMapper MapStruct domain ↔ entity
- PositionPersistenceAdapter implementa puerto, delega en JPA repo,
  gestiona soft delete (delete dispara @SQLDelete) y restore con
  native query que ignora @SQLRestriction
- @SpringBootTest verifica save/find/softDelete/restore/trash listing"
```

---

## Tarea 6: Servicio de aplicación + DTOs + Mapper web + Controller para Position (CRUD completo)

**Files:**
- Create: `src/main/java/com/ossflow/catalog/position/application/PositionService.java`
- Create: `src/main/java/com/ossflow/catalog/position/infrastructure/web/dto/CreatePositionRequest.java`
- Create: `src/main/java/com/ossflow/catalog/position/infrastructure/web/dto/UpdatePositionRequest.java`
- Create: `src/main/java/com/ossflow/catalog/position/infrastructure/web/dto/PatchPositionRequest.java`
- Create: `src/main/java/com/ossflow/catalog/position/infrastructure/web/dto/PositionResponse.java`
- Create: `src/main/java/com/ossflow/catalog/position/infrastructure/web/PositionWebMapper.java`
- Create: `src/main/java/com/ossflow/catalog/position/infrastructure/web/PositionController.java`
- Create: `src/main/java/com/ossflow/shared/web/CurrentOwner.java`
- Create: `src/test/java/com/ossflow/catalog/position/application/PositionServiceTest.java`
- Create: `src/test/java/com/ossflow/catalog/position/infrastructure/web/PositionControllerTest.java`

- [ ] **Step 6.1: Crear helper `CurrentOwner` (resolver del owner activo, hardcoded a 1 hasta que entre auth)**

```java
package com.ossflow.shared.web;

import org.springframework.stereotype.Component;

@Component
public class CurrentOwner {
    public Long id() {
        return 1L; // mono-usuario; cuando entre Spring Security, lee del SecurityContext
    }
}
```

- [ ] **Step 6.2: Crear DTOs**

`CreatePositionRequest.java`:

```java
package com.ossflow.catalog.position.infrastructure.web.dto;

import com.ossflow.catalog.position.domain.PositionType;
import com.ossflow.catalog.position.domain.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePositionRequest(
        @NotBlank @Size(max = 120) String name,
        @NotNull PositionType type,
        @Size(max = 10000) String description,
        @NotNull Visibility visibility
) {}
```

`UpdatePositionRequest.java`:

```java
package com.ossflow.catalog.position.infrastructure.web.dto;

import com.ossflow.catalog.position.domain.PositionType;
import com.ossflow.catalog.position.domain.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdatePositionRequest(
        @NotBlank @Size(max = 120) String name,
        @NotNull PositionType type,
        @Size(max = 10000) String description,
        @NotNull Visibility visibility
) {}
```

`PatchPositionRequest.java`:

```java
package com.ossflow.catalog.position.infrastructure.web.dto;

import com.ossflow.catalog.position.domain.PositionType;
import com.ossflow.catalog.position.domain.Visibility;
import jakarta.validation.constraints.Size;

public record PatchPositionRequest(
        @Size(max = 120) String name,
        PositionType type,
        @Size(max = 10000) String description,
        Visibility visibility
) {}
```

`PositionResponse.java`:

```java
package com.ossflow.catalog.position.infrastructure.web.dto;

import com.ossflow.catalog.position.domain.PositionType;
import com.ossflow.catalog.position.domain.Visibility;

import java.time.Instant;

public record PositionResponse(
        Long id,
        String name,
        PositionType type,
        String description,
        Visibility visibility,
        Instant createdAt,
        Instant updatedAt
) {}
```

- [ ] **Step 6.3: Crear `PositionWebMapper`**

```java
package com.ossflow.catalog.position.infrastructure.web;

import com.ossflow.catalog.position.domain.Position;
import com.ossflow.catalog.position.infrastructure.web.dto.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PositionWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "purgeAt", ignore = true)
    Position fromCreate(CreatePositionRequest req);

    PositionResponse toResponse(Position position);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Position applyPatch(PatchPositionRequest req, @MappingTarget Position position);
}
```

- [ ] **Step 6.4: Crear `PositionService`**

```java
package com.ossflow.catalog.position.application;

import com.ossflow.catalog.position.application.port.PositionRepositoryPort;
import com.ossflow.catalog.position.domain.Position;
import com.ossflow.shared.exception.DuplicateNameException;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepositoryPort repository;

    public Position create(Position position) {
        if (repository.existsByName(position.ownerId(), position.name())) {
            throw new DuplicateNameException("POSITION_NAME_DUPLICATE",
                    "Ya existe una posición con el nombre '%s'".formatted(position.name()),
                    Map.of("name", position.name()));
        }
        Position saved = repository.save(position);
        log.info("Posición creada id={} name={}", saved.id(), saved.name());
        return saved;
    }

    public Position findById(Long id, Long ownerId) {
        return repository.findById(id, ownerId)
                .orElseThrow(() -> new NotFoundException("POSITION_NOT_FOUND",
                        "No existe la posición con id %d".formatted(id),
                        Map.of("positionId", id)));
    }

    public Page<Position> list(Long ownerId, String nameFilter, Pageable pageable) {
        return repository.findAll(ownerId, nameFilter, pageable);
    }

    public Position replace(Long id, Long ownerId, Position replacement) {
        Position existing = findById(id, ownerId);
        if (!existing.name().equals(replacement.name())
                && repository.existsByName(ownerId, replacement.name())) {
            throw new DuplicateNameException("POSITION_NAME_DUPLICATE",
                    "Ya existe una posición con el nombre '%s'".formatted(replacement.name()),
                    Map.of("name", replacement.name()));
        }
        Position toSave = replacement.toBuilder()
                .id(existing.id())
                .ownerId(existing.ownerId())
                .createdAt(existing.createdAt())
                .version(existing.version())
                .build();
        return repository.save(toSave);
    }

    public Position patch(Long id, Long ownerId, Position patched) {
        return repository.save(patched.toBuilder()
                .id(id)
                .ownerId(ownerId)
                .build());
    }

    public void softDelete(Long id, Long ownerId) {
        repository.softDelete(id, ownerId);
        log.info("Posición soft-deleted id={}", id);
    }

    public Position restore(Long id, Long ownerId) {
        Position restored = repository.restore(id, ownerId);
        log.info("Posición restaurada id={}", id);
        return restored;
    }

    public Page<Position> trash(Long ownerId, Pageable pageable) {
        return repository.findTrash(ownerId, pageable);
    }
}
```

- [ ] **Step 6.5: Crear `PositionController`**

```java
package com.ossflow.catalog.position.infrastructure.web;

import com.ossflow.catalog.position.application.PositionService;
import com.ossflow.catalog.position.domain.Position;
import com.ossflow.catalog.position.infrastructure.web.dto.*;
import com.ossflow.shared.web.CurrentOwner;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/catalog/positions")
@Validated
@RequiredArgsConstructor
public class PositionController {

    private final PositionService service;
    private final PositionWebMapper mapper;
    private final CurrentOwner currentOwner;

    @GetMapping
    public Page<PositionResponse> list(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(defaultValue = "name,asc") String sort) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), parseSort(sort));
        return service.list(currentOwner.id(), name, pageable).map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    public PositionResponse get(@PathVariable @Positive Long id) {
        return mapper.toResponse(service.findById(id, currentOwner.id()));
    }

    @PostMapping
    public ResponseEntity<PositionResponse> create(@Valid @RequestBody CreatePositionRequest req) {
        Position toCreate = mapper.fromCreate(req).toBuilder().ownerId(currentOwner.id()).build();
        Position created = service.create(toCreate);
        return ResponseEntity
                .created(URI.create("/api/v1/catalog/positions/" + created.id()))
                .body(mapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public PositionResponse replace(@PathVariable @Positive Long id, @Valid @RequestBody UpdatePositionRequest req) {
        Position replacement = Position.builder()
                .name(req.name())
                .type(req.type())
                .description(req.description())
                .visibility(req.visibility())
                .build();
        return mapper.toResponse(service.replace(id, currentOwner.id(), replacement));
    }

    @PatchMapping("/{id}")
    public PositionResponse patch(@PathVariable @Positive Long id, @Valid @RequestBody PatchPositionRequest req) {
        Position existing = service.findById(id, currentOwner.id());
        Position patched = mapper.applyPatch(req, existing);
        return mapper.toResponse(service.patch(id, currentOwner.id(), patched));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.softDelete(id, currentOwner.id());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    public PositionResponse restore(@PathVariable @Positive Long id) {
        return mapper.toResponse(service.restore(id, currentOwner.id()));
    }

    private static Sort parseSort(String s) {
        String[] parts = s.split(",");
        Sort.Direction dir = parts.length > 1 && "desc".equalsIgnoreCase(parts[1]) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(dir, parts[0]);
    }
}
```

- [ ] **Step 6.6: Test unitario del servicio**

```java
package com.ossflow.catalog.position.application;

import com.ossflow.catalog.position.application.port.PositionRepositoryPort;
import com.ossflow.catalog.position.domain.Position;
import com.ossflow.catalog.position.domain.PositionType;
import com.ossflow.catalog.position.domain.Visibility;
import com.ossflow.shared.exception.DuplicateNameException;
import com.ossflow.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @Mock PositionRepositoryPort repository;
    @InjectMocks PositionService service;

    @Test
    void should_create_position_when_name_is_unique() {
        var input = Position.builder().ownerId(1L).name("X").type(PositionType.TOP).visibility(Visibility.PRIVATE).build();
        given(repository.existsByName(1L, "X")).willReturn(false);
        given(repository.save(input)).willReturn(input.toBuilder().id(10L).build());

        var result = service.create(input);

        assertThat(result.id()).isEqualTo(10L);
    }

    @Test
    void should_throw_DuplicateNameException_when_creating_duplicate() {
        var input = Position.builder().ownerId(1L).name("X").type(PositionType.TOP).visibility(Visibility.PRIVATE).build();
        given(repository.existsByName(1L, "X")).willReturn(true);

        assertThatThrownBy(() -> service.create(input))
                .isInstanceOf(DuplicateNameException.class)
                .hasFieldOrPropertyWithValue("errorCode", "POSITION_NAME_DUPLICATE");
    }

    @Test
    void should_throw_NotFoundException_when_findById_misses() {
        given(repository.findById(99L, 1L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(99L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", "POSITION_NOT_FOUND");
    }
}
```

- [ ] **Step 6.7: Test slice del controller**

```java
package com.ossflow.catalog.position.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.catalog.position.application.PositionService;
import com.ossflow.catalog.position.domain.Position;
import com.ossflow.catalog.position.domain.PositionType;
import com.ossflow.catalog.position.domain.Visibility;
import com.ossflow.catalog.position.infrastructure.web.dto.CreatePositionRequest;
import com.ossflow.shared.exception.DuplicateNameException;
import com.ossflow.shared.web.CurrentOwner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PositionController.class)
@Import({PositionWebMapperImpl.class, CurrentOwner.class})
class PositionControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @MockBean PositionService service;

    @Test
    void post_should_return_201_with_location_header() throws Exception {
        var req = new CreatePositionRequest("Guardia Cerrada", PositionType.BOTTOM, null, Visibility.PRIVATE);
        given(service.create(org.mockito.ArgumentMatchers.any())).willReturn(
                Position.builder().id(7L).ownerId(1L).name("Guardia Cerrada").type(PositionType.BOTTOM).visibility(Visibility.PRIVATE).build());

        mvc.perform(post("/api/v1/catalog/positions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/catalog/positions/7"))
                .andExpect(jsonPath("$.id").value(7));
    }

    @Test
    void post_should_return_400_when_name_blank() throws Exception {
        var req = new CreatePositionRequest("", PositionType.BOTTOM, null, Visibility.PRIVATE);
        mvc.perform(post("/api/v1/catalog/positions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("name"));
    }

    @Test
    void post_should_return_409_when_name_duplicate() throws Exception {
        var req = new CreatePositionRequest("Guardia Cerrada", PositionType.BOTTOM, null, Visibility.PRIVATE);
        given(service.create(org.mockito.ArgumentMatchers.any()))
                .willThrow(new DuplicateNameException("POSITION_NAME_DUPLICATE", "duplicado", Map.of()));

        mvc.perform(post("/api/v1/catalog/positions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("POSITION_NAME_DUPLICATE"));
    }
}
```

- [ ] **Step 6.8: Run tests**

Run: `mvn -B test -Dtest='PositionServiceTest,PositionControllerTest'`
Expected: Tests run: 6, Failures: 0.

- [ ] **Step 6.9: Commit**

```bash
git add src/main/java/com/ossflow/catalog/position/application src/main/java/com/ossflow/catalog/position/infrastructure/web src/main/java/com/ossflow/shared/web/CurrentOwner.java src/test/java/com/ossflow/catalog/position
git commit -m "feat(catalog/position): CRUD completo con DTOs, validación y restore

- DTOs CreatePositionRequest/Update/Patch/Response con jakarta.validation
- PositionWebMapper MapStruct domain ↔ DTOs (ignora campos auditados en
  fromCreate, ignora nulls en applyPatch)
- PositionService: create, findById, list, replace (PUT), patch (PATCH
  Merge Patch), softDelete, restore, trash
- PositionController: 7 endpoints con paginación + filtros + 201/204/200
- CurrentOwner bean (placeholder hasta Spring Security)
- Tests unit (Mockito) + slice (@WebMvcTest) cubren happy/error paths"
```

---

## Tarea 7: CRUD completo de Technique (mismo patrón que Position) + endpoint `/trash`

**Files:**
- Mismo patrón que Tarea 5+6 aplicado a Technique:
  - `application/port/TechniqueRepositoryPort.java`
  - `infrastructure/persistence/{TechniquePersistenceMapper, TechniquePersistenceAdapter}.java`
  - `application/TechniqueService.java`
  - `infrastructure/web/dto/{Create,Update,Patch}TechniqueRequest.java`, `TechniqueResponse.java`
  - `infrastructure/web/{TechniqueWebMapper, TechniqueController}.java`
  - Tests: `TechniquePersistenceAdapterTest`, `TechniqueServiceTest`, `TechniqueControllerTest`
- Create: `src/main/java/com/ossflow/catalog/portability/infrastructure/web/CatalogTrashController.java`
- Create: `src/test/java/com/ossflow/integration/CatalogIntegrationTest.java`

- [ ] **Step 7.1: Replicar puerto, adapter, mapper, servicio, DTOs y controller para Technique**

Aplicar **el mismo patrón exacto** de Tareas 5 y 6 sustituyendo Position → Technique. Diferencias específicas:

- `CreateTechniqueRequest` añade `@NotNull Long startPositionId`, `Long endPositionId` (nullable), `@NotNull TechniqueCategory category`, `@NotNull Belt minimumBelt`, `@NotNull Modality modality`, `@Pattern(regexp="^https?://.*")` `String youtubeUrl`.
- `TechniqueService.create()` debe verificar que `startPositionId` existe en `PositionRepositoryPort` antes de guardar; si no → `NotFoundException("POSITION_NOT_FOUND", ...)`.
- `TechniqueService.create()` debe verificar `endPositionId` si no es null.
- Endpoints adicionales: filtros `?category=`, `?belt=`, `?modality=`, `?startPositionId=`, `?endPositionId=`.

Test extra:

```java
@Test
void should_throw_when_startPositionId_not_found() {
    given(positionRepository.findById(999L, 1L)).willReturn(Optional.empty());
    assertThatThrownBy(() -> service.create(Technique.builder().ownerId(1L).startPositionId(999L)
            .name("X").category(TechniqueCategory.SUBMISSION).minimumBelt(Belt.WHITE)
            .modality(Modality.BOTH).visibility(Visibility.PRIVATE).build()))
            .isInstanceOf(NotFoundException.class)
            .hasFieldOrPropertyWithValue("errorCode", "POSITION_NOT_FOUND");
}
```

- [ ] **Step 7.2: Crear `CatalogTrashController`**

```java
package com.ossflow.catalog.portability.infrastructure.web;

import com.ossflow.catalog.position.application.PositionService;
import com.ossflow.catalog.position.infrastructure.web.PositionWebMapper;
import com.ossflow.catalog.position.infrastructure.web.dto.PositionResponse;
import com.ossflow.catalog.technique.application.TechniqueService;
import com.ossflow.catalog.technique.infrastructure.web.TechniqueWebMapper;
import com.ossflow.catalog.technique.infrastructure.web.dto.TechniqueResponse;
import com.ossflow.shared.web.CurrentOwner;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/catalog/trash")
@RequiredArgsConstructor
public class CatalogTrashController {

    private final PositionService positionService;
    private final TechniqueService techniqueService;
    private final PositionWebMapper positionMapper;
    private final TechniqueWebMapper techniqueMapper;
    private final CurrentOwner currentOwner;

    @GetMapping
    public Map<String, Object> trash(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var positions = positionService.trash(currentOwner.id(), PageRequest.of(page, size))
                .map(positionMapper::toResponse);
        var techniques = techniqueService.trash(currentOwner.id(), PageRequest.of(page, size))
                .map(techniqueMapper::toResponse);
        return Map.of(
                "positions", positions,
                "techniques", techniques
        );
    }
}
```

- [ ] **Step 7.3: Test integración cross-CRUD para catalog**

```java
package com.ossflow.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CatalogIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void should_create_position_then_create_technique_referring_it() throws Exception {
        // 1. Crear posición
        var posResp = mvc.perform(post("/api/v1/catalog/positions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Guardia Cerrada","type":"BOTTOM","visibility":"PRIVATE"}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long positionId = json.readTree(posResp).get("id").asLong();

        // 2. Crear técnica que referencia la posición
        mvc.perform(post("/api/v1/catalog/techniques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Kimura","category":"SUBMISSION","minimumBelt":"WHITE",
                                "modality":"BOTH","startPositionId":%d,"visibility":"PRIVATE"}
                                """.formatted(positionId)))
                .andExpect(status().isCreated());

        // 3. Listar y verificar
        mvc.perform(get("/api/v1/catalog/techniques"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void should_return_404_when_creating_technique_with_unknown_start_position() throws Exception {
        mvc.perform(post("/api/v1/catalog/techniques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"X","category":"SUBMISSION","minimumBelt":"WHITE",
                                "modality":"BOTH","startPositionId":99999,"visibility":"PRIVATE"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("POSITION_NOT_FOUND"));
    }

    @Test
    void should_softDelete_then_restore_position() throws Exception {
        var resp = mvc.perform(post("/api/v1/catalog/positions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Espalda","type":"TOP","visibility":"PRIVATE"}
                                """))
                .andReturn().getResponse().getContentAsString();
        Long id = json.readTree(resp).get("id").asLong();

        mvc.perform(delete("/api/v1/catalog/positions/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/catalog/positions/" + id)).andExpect(status().isNotFound());
        mvc.perform(post("/api/v1/catalog/positions/" + id + "/restore")).andExpect(status().isOk());
        mvc.perform(get("/api/v1/catalog/positions/" + id)).andExpect(status().isOk());
    }
}
```

- [ ] **Step 7.4: Run tests**

Run: `mvn -B test -Dtest='Technique*,CatalogIntegrationTest'`
Expected: todos verdes.

- [ ] **Step 7.5: Commit**

```bash
git add src/main/java/com/ossflow/catalog/technique src/main/java/com/ossflow/catalog/portability src/test/java/com/ossflow/catalog/technique src/test/java/com/ossflow/integration/CatalogIntegrationTest.java
git commit -m "feat(catalog): CRUD técnicas + validación FK + endpoint /trash + tests integración

- TechniqueService valida start/endPositionId vía PositionRepositoryPort
- Filtros: ?category, ?belt, ?modality, ?startPositionId, ?endPositionId
- CatalogTrashController agrega papelera de positions+techniques
- CatalogIntegrationTest cubre flujo create→softDelete→restore y FK 404"
```

---

## Tarea 8: Migración V2 Federation + Ruleset + entidades, servicio CRUD y seed

**Files:**
- Create: `src/main/resources/db/migration/V2__init_federations_rulesets.sql`
- Create: `src/main/resources/db/migration/V100__seed_federations.sql`
- Create: `src/main/java/com/ossflow/catalog/federation/domain/Federation.java`
- Create: `src/main/java/com/ossflow/catalog/federation/{application/port/FederationRepositoryPort.java, application/FederationService.java, infrastructure/persistence/FederationEntity.java, infrastructure/persistence/FederationJpaRepository.java, infrastructure/persistence/FederationPersistenceAdapter.java, infrastructure/persistence/FederationPersistenceMapper.java, infrastructure/web/FederationController.java, infrastructure/web/FederationWebMapper.java, infrastructure/web/dto/FederationResponse.java}`
- Create: `src/main/java/com/ossflow/catalog/ruleset/domain/{Ruleset.java, RulesetTechnique.java, LegalityStatus.java}`
- Create: `src/main/java/com/ossflow/catalog/ruleset/{application/...,infrastructure/...}` (mismo patrón)
- Create: `src/test/java/com/ossflow/catalog/federation/...`
- Create: `src/test/java/com/ossflow/catalog/ruleset/...`

- [ ] **Step 8.1: Migración Flyway V2**

```sql
-- src/main/resources/db/migration/V2__init_federations_rulesets.sql

CREATE TABLE federation (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    code            VARCHAR(20) NOT NULL UNIQUE,
    name            VARCHAR(120) NOT NULL,
    official_url    VARCHAR(500),
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP    NOT NULL,
    version         BIGINT       NOT NULL DEFAULT 0
);

CREATE TABLE ruleset (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    federation_id    BIGINT       NOT NULL,
    belt             VARCHAR(15)  NOT NULL,
    modality         VARCHAR(10)  NOT NULL,
    effective_from   DATE         NOT NULL,
    effective_to     DATE,
    source_url       VARCHAR(500),
    created_at       TIMESTAMP    NOT NULL,
    updated_at       TIMESTAMP    NOT NULL,
    version          BIGINT       NOT NULL DEFAULT 0,
    FOREIGN KEY (federation_id) REFERENCES federation(id),
    UNIQUE (federation_id, belt, modality, effective_from)
);

CREATE TABLE ruleset_technique (
    ruleset_id        BIGINT       NOT NULL,
    technique_id      BIGINT       NOT NULL,
    status            VARCHAR(20)  NOT NULL,
    condition_notes   TEXT,
    PRIMARY KEY (ruleset_id, technique_id),
    FOREIGN KEY (ruleset_id) REFERENCES ruleset(id) ON DELETE CASCADE,
    FOREIGN KEY (technique_id) REFERENCES technique(id)
);
```

- [ ] **Step 8.2: Seed V100 con las 10 federaciones**

```sql
-- src/main/resources/db/migration/V100__seed_federations.sql

INSERT INTO federation (code, name, official_url, created_at, updated_at) VALUES
    ('IBJJF',  'International Brazilian Jiu-Jitsu Federation', 'https://ibjjf.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('ADCC',   'Abu Dhabi Combat Club',                        'https://adcombat.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('AJP',    'Abu Dhabi Jiu-Jitsu Pro',                      'https://ajptour.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('NAGA',   'North American Grappling Association',         'https://nagafighter.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('UAEJJF', 'UAE Jiu-Jitsu Federation',                     'https://uaejjf.org', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('FEJJB',  'Federación Española de Jiu-Jitsu Brasileño',   NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('AEJJ',   'Asociación Española de Jiu-Jitsu',             NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('SBJJ',   'Spanish Brazilian Jiu-Jitsu',                  NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('CBJJE',  'Confederación Brasileña de Jiu-Jitsu Esportivo', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('GI',     'Grappling Industries',                          'https://grapplingindustries.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
```

- [ ] **Step 8.3: Crear domain records y enum**

`Federation.java`:

```java
package com.ossflow.catalog.federation.domain;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record Federation(
        Long id,
        String code,
        String name,
        String officialUrl,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {}
```

`Ruleset.java`:

```java
package com.ossflow.catalog.ruleset.domain;

import com.ossflow.catalog.position.domain.Visibility;
import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;

@Builder(toBuilder = true)
public record Ruleset(
        Long id,
        Long federationId,
        Belt belt,
        Modality modality,
        LocalDate effectiveFrom,
        LocalDate effectiveTo,
        String sourceUrl,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {}
```

`LegalityStatus.java`:

```java
package com.ossflow.catalog.ruleset.domain;
public enum LegalityStatus { ALLOWED, PROHIBITED, CONDITIONAL }
```

`RulesetTechnique.java`:

```java
package com.ossflow.catalog.ruleset.domain;

import lombok.Builder;

@Builder
public record RulesetTechnique(
        Long rulesetId,
        Long techniqueId,
        LegalityStatus status,
        String conditionNotes
) {}
```

- [ ] **Step 8.4: Crear Federation entity, repo, adapter, service, controller**

Aplicar el mismo patrón de Tareas 5+6 a Federation. Como las federaciones son **datos sembrados y raramente modificables**, exponer solo:

- `GET /api/v1/catalog/federations` (lista todas, sin paginación porque son ≤ 20).
- `GET /api/v1/catalog/federations/{id}`.

(No hay POST/PUT/DELETE en el alcance actual; la administración futura entrará por panel admin).

- [ ] **Step 8.5: Crear Ruleset entity, repo, adapter, service, controller con sub-recurso techniques**

Endpoints:
- `GET /api/v1/catalog/rulesets?federationId=&belt=&modality=`
- `GET /api/v1/catalog/rulesets/{id}` (detalle con `techniques[]`)
- `POST /api/v1/catalog/rulesets` (crear ruleset; valida UNIQUE)
- `POST /api/v1/catalog/rulesets/{id}/techniques` (añadir/actualizar status de técnica)
- `DELETE /api/v1/catalog/rulesets/{id}/techniques/{tid}`
- `GET /api/v1/catalog/techniques/{id}/legality` (cross-query: para una técnica, lista status por federación/cinturón)

- [ ] **Step 8.6: Test integración federations seed**

```java
@Test
void should_have_10_seeded_federations() throws Exception {
    mvc.perform(get("/api/v1/catalog/federations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(10))
            .andExpect(jsonPath("$[?(@.code == 'IBJJF')]").exists())
            .andExpect(jsonPath("$[?(@.code == 'GI')]").exists());
}
```

- [ ] **Step 8.7: Run tests**

Run: `mvn -B test -Dtest='Federation*,Ruleset*'`

- [ ] **Step 8.8: Commit**

```bash
git add src/main/resources/db/migration/V2*.sql src/main/resources/db/migration/V100*.sql src/main/java/com/ossflow/catalog/federation src/main/java/com/ossflow/catalog/ruleset src/test/java/com/ossflow/catalog/federation src/test/java/com/ossflow/catalog/ruleset
git commit -m "feat(catalog): federations + rulesets con seed de 10 federaciones

- V2: tablas federation, ruleset (UNIQUE per federation/belt/modality/from),
  ruleset_technique (PK compuesta) con FKs y CASCADE
- V100: seed IBJJF, ADCC, AJP, NAGA, UAEJJF, FEJJB, AEJJ, SBJJ, CBJJE, GI
- Federation read-only (lista + detalle)
- Ruleset CRUD con sub-recurso techniques y endpoint /legality cross-query"
```

---

## Tarea 9: System con flowDefinition + JSON schema + Chain of Responsibility

**Files:**
- Create: `src/main/resources/schemas/system-flow.schema.v1.json`
- Create: `src/main/java/com/ossflow/shared/json/JsonSchemaValidator.java`
- Create: `src/main/java/com/ossflow/shared/validation/{ValidationStep,ValidationChain,ValidationContext,ValidationResult}.java`
- Create: `src/main/java/com/ossflow/catalog/system/domain/{System.java, SystemFlowDefinition.java}`
- Create: `src/main/java/com/ossflow/catalog/system/application/validation/{FlowSchemaValidationStep,FlowSemanticValidationStep,FlowReferentialValidationStep}.java`
- Create: `src/main/java/com/ossflow/catalog/system/...` (resto: port, service, adapter, controller, mappers, DTOs)
- Create: `src/main/resources/db/migration/V3__init_systems.sql`
- Tests: unit por validador + slice + integration

- [ ] **Step 9.1: Migración V3**

```sql
-- src/main/resources/db/migration/V3__init_systems.sql

CREATE TABLE system (
    id                   INTEGER PRIMARY KEY AUTOINCREMENT,
    owner_id             BIGINT       NOT NULL DEFAULT 1,
    name                 VARCHAR(120) NOT NULL,
    description          TEXT,
    anchor_position_id   BIGINT,
    flow_definition      TEXT         NOT NULL,
    flow_schema_version  VARCHAR(10)  NOT NULL DEFAULT 'v1',
    visibility           VARCHAR(10)  NOT NULL DEFAULT 'PRIVATE',
    created_at           TIMESTAMP    NOT NULL,
    updated_at           TIMESTAMP    NOT NULL,
    version              BIGINT       NOT NULL DEFAULT 0,
    deleted_at           TIMESTAMP,
    purge_at             TIMESTAMP,
    FOREIGN KEY (anchor_position_id) REFERENCES position(id)
);
CREATE UNIQUE INDEX ux_system_owner_name_active ON system(owner_id, name) WHERE deleted_at IS NULL;
```

- [ ] **Step 9.2: Schema JSON `system-flow.schema.v1.json`**

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "type": "object",
  "required": ["nodes", "edges"],
  "additionalProperties": false,
  "properties": {
    "nodes": {
      "type": "array",
      "items": {
        "type": "object",
        "required": ["id", "kind", "refId"],
        "additionalProperties": false,
        "properties": {
          "id": { "type": "string", "minLength": 1 },
          "kind": { "enum": ["POSITION", "TECHNIQUE"] },
          "refId": { "type": "integer", "minimum": 1 },
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
        "additionalProperties": false,
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

- [ ] **Step 9.3: `JsonSchemaValidator` wrapper sobre networknt**

```java
package com.ossflow.shared.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JsonSchemaValidator {

    private final ObjectMapper objectMapper;
    private final Map<String, JsonSchema> cache = new HashMap<>();

    public Set<ValidationMessage> validate(String classpathSchema, JsonNode payload) {
        JsonSchema schema = cache.computeIfAbsent(classpathSchema, this::load);
        return schema.validate(payload);
    }

    private JsonSchema load(String path) {
        try (var in = new ClassPathResource(path).getInputStream()) {
            JsonNode raw = objectMapper.readTree(in);
            return JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012).getSchema(raw);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar schema: " + path, e);
        }
    }
}
```

- [ ] **Step 9.4: ValidationChain genérica**

```java
package com.ossflow.shared.validation;

public sealed interface ValidationResult permits ValidationResult.Ok, ValidationResult.Fail {
    record Ok() implements ValidationResult {}
    record Fail(String errorCode, String message, java.util.Map<String, Object> details) implements ValidationResult {}
}
```

```java
package com.ossflow.shared.validation;

public interface ValidationStep<T> {
    ValidationResult validate(T payload, ValidationContext ctx);
}
```

```java
package com.ossflow.shared.validation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ValidationContext {
    private final Map<String, Object> data = new ConcurrentHashMap<>();
    public void put(String k, Object v) { data.put(k, v); }
    @SuppressWarnings("unchecked")
    public <T> T get(String k) { return (T) data.get(k); }
}
```

```java
package com.ossflow.shared.validation;

import java.util.List;

public class ValidationChain<T> {
    private final List<ValidationStep<T>> steps;
    public ValidationChain(List<ValidationStep<T>> steps) { this.steps = steps; }
    public ValidationResult run(T payload) {
        var ctx = new ValidationContext();
        for (var step : steps) {
            var result = step.validate(payload, ctx);
            if (result instanceof ValidationResult.Fail) return result;
        }
        return new ValidationResult.Ok();
    }
}
```

- [ ] **Step 9.5: SystemFlowDefinition record**

```java
package com.ossflow.catalog.system.domain;

import com.fasterxml.jackson.databind.JsonNode;

public record SystemFlowDefinition(JsonNode raw) {}
```

- [ ] **Step 9.6: FlowSchemaValidationStep**

```java
package com.ossflow.catalog.system.application.validation;

import com.ossflow.catalog.system.domain.SystemFlowDefinition;
import com.ossflow.shared.json.JsonSchemaValidator;
import com.ossflow.shared.validation.ValidationContext;
import com.ossflow.shared.validation.ValidationResult;
import com.ossflow.shared.validation.ValidationStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class FlowSchemaValidationStep implements ValidationStep<SystemFlowDefinition> {

    private static final String SCHEMA = "schemas/system-flow.schema.v1.json";
    private final JsonSchemaValidator validator;

    @Override
    public ValidationResult validate(SystemFlowDefinition payload, ValidationContext ctx) {
        var violations = validator.validate(SCHEMA, payload.raw());
        if (violations.isEmpty()) return new ValidationResult.Ok();
        var detail = violations.stream()
                .map(v -> Map.of("path", v.getInstanceLocation().toString(), "message", v.getMessage()))
                .toList();
        return new ValidationResult.Fail(
                "SYSTEM_FLOW_SCHEMA_INVALID",
                "El flowDefinition no cumple el schema v1",
                Map.of("validatorStep", "FlowSchemaValidationStep", "violations", detail)
        );
    }
}
```

- [ ] **Step 9.7: FlowSemanticValidationStep**

Verifica:
- Cada `edges[].from` y `edges[].to` apuntan a un `nodes[].id` existente.
- No hay nodos duplicados por id.
- No hay aristas con `from == to` (auto-loop sin trigger).

```java
package com.ossflow.catalog.system.application.validation;

import com.ossflow.catalog.system.domain.SystemFlowDefinition;
import com.ossflow.shared.validation.ValidationContext;
import com.ossflow.shared.validation.ValidationResult;
import com.ossflow.shared.validation.ValidationStep;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class FlowSemanticValidationStep implements ValidationStep<SystemFlowDefinition> {

    @Override
    public ValidationResult validate(SystemFlowDefinition payload, ValidationContext ctx) {
        var nodes = payload.raw().get("nodes");
        var edges = payload.raw().get("edges");

        Set<String> nodeIds = new HashSet<>();
        for (var n : nodes) {
            String id = n.get("id").asText();
            if (!nodeIds.add(id)) {
                return new ValidationResult.Fail("SYSTEM_FLOW_SEMANTIC_INVALID",
                        "Nodo duplicado: " + id,
                        Map.of("validatorStep", "FlowSemanticValidationStep", "duplicateNodeId", id));
            }
        }

        for (var e : edges) {
            String from = e.get("from").asText();
            String to = e.get("to").asText();
            if (!nodeIds.contains(from)) {
                return new ValidationResult.Fail("SYSTEM_FLOW_SEMANTIC_INVALID",
                        "Edge.from apunta a nodo inexistente: " + from,
                        Map.of("validatorStep", "FlowSemanticValidationStep", "missingNode", from));
            }
            if (!nodeIds.contains(to)) {
                return new ValidationResult.Fail("SYSTEM_FLOW_SEMANTIC_INVALID",
                        "Edge.to apunta a nodo inexistente: " + to,
                        Map.of("validatorStep", "FlowSemanticValidationStep", "missingNode", to));
            }
        }
        ctx.put("nodeIds", nodeIds);
        return new ValidationResult.Ok();
    }
}
```

- [ ] **Step 9.8: FlowReferentialValidationStep**

Verifica que cada `nodes[].refId` exista en `position` o `technique` según `kind`, y no esté soft-deleted.

```java
package com.ossflow.catalog.system.application.validation;

import com.ossflow.catalog.position.application.port.PositionRepositoryPort;
import com.ossflow.catalog.system.domain.SystemFlowDefinition;
import com.ossflow.catalog.technique.application.port.TechniqueRepositoryPort;
import com.ossflow.shared.validation.*;
import com.ossflow.shared.web.CurrentOwner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class FlowReferentialValidationStep implements ValidationStep<SystemFlowDefinition> {

    private final PositionRepositoryPort positions;
    private final TechniqueRepositoryPort techniques;
    private final CurrentOwner currentOwner;

    @Override
    public ValidationResult validate(SystemFlowDefinition payload, ValidationContext ctx) {
        Long ownerId = currentOwner.id();
        for (var n : payload.raw().get("nodes")) {
            String kind = n.get("kind").asText();
            long refId = n.get("refId").asLong();
            boolean exists = "POSITION".equals(kind)
                    ? positions.findById(refId, ownerId).isPresent()
                    : techniques.findById(refId, ownerId).isPresent();
            if (!exists) {
                return new ValidationResult.Fail(
                        "SYSTEM_FLOW_REF_NOT_FOUND",
                        "Referencia inexistente: kind=%s refId=%d".formatted(kind, refId),
                        Map.of("validatorStep", "FlowReferentialValidationStep",
                                "kind", kind, "refId", refId));
            }
        }
        return new ValidationResult.Ok();
    }
}
```

- [ ] **Step 9.9: SystemService usa la chain antes de persistir**

```java
@Service
@RequiredArgsConstructor
public class SystemService {
    private final SystemRepositoryPort repository;
    private final FlowSchemaValidationStep schemaStep;
    private final FlowSemanticValidationStep semanticStep;
    private final FlowReferentialValidationStep referentialStep;

    public System create(System system) {
        validateFlow(system.flowDefinition());
        // ... resto del create
    }

    private void validateFlow(SystemFlowDefinition def) {
        var chain = new ValidationChain<>(List.of(schemaStep, semanticStep, referentialStep));
        var result = chain.run(def);
        if (result instanceof ValidationResult.Fail f) {
            switch (f.errorCode()) {
                case "SYSTEM_FLOW_SCHEMA_INVALID" -> throw new JsonSchemaViolationException(f.errorCode(), f.message(), f.details());
                case "SYSTEM_FLOW_SEMANTIC_INVALID" -> throw new SemanticValidationException(f.errorCode(), f.message(), f.details());
                case "SYSTEM_FLOW_REF_NOT_FOUND" -> throw new ReferentialIntegrityException(f.errorCode(), f.message(), f.details());
                default -> throw new IllegalStateException(f.errorCode());
            }
        }
    }
}
```

- [ ] **Step 9.10: Tests**

Unit por validador (3): payload válido / inválido cada uno.
Integration: POST /systems con flow válido (201) + 3 escenarios de fallo (422 con código correcto).

- [ ] **Step 9.11: Commit**

```bash
git add src/main/resources/db/migration/V3*.sql src/main/resources/schemas src/main/java/com/ossflow/shared/json src/main/java/com/ossflow/shared/validation src/main/java/com/ossflow/catalog/system src/test/java/com/ossflow/catalog/system
git commit -m "feat(catalog/system): flowDefinition validado por Chain of Responsibility

- V3 tabla system con flow_definition TEXT + UNIQUE per owner
- system-flow.schema.v1.json: nodes (POSITION/TECHNIQUE+refId), edges
  (from/to/trigger ATTACK/DEFENSE/PASS/ESCAPE/TRANSITION + condition)
- JsonSchemaValidator wrapper sobre networknt cacheando schemas
- ValidationChain genérica + 3 steps:
  · FlowSchemaValidationStep (estructura JSON)
  · FlowSemanticValidationStep (nodos únicos, edges válidos)
  · FlowReferentialValidationStep (refIds existen en catalog)
- SystemService valida con Chain antes de persistir, mapea fail al
  exception type correcto (422 con código específico)
- Tests unit por validador + integration con 4 escenarios"
```

---

## Tarea 10: Importadores (Strategy + Template Method)

**Files:**
- Create: `src/main/resources/schemas/{catalog-import,system-import,ruleset-import}.schema.v1.json`
- Create: `src/main/java/com/ossflow/catalog/portability/application/{Importer,AbstractImporter,ImportMode,ImportReport,CatalogImporter,SystemImporter,RulesetImporter}.java`
- Create: `src/main/java/com/ossflow/catalog/portability/infrastructure/web/ImportController.java`
- Tests: integration por cada importador

- [ ] **Step 10.1: Schema `catalog-import.schema.v1.json`**

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "type": "object",
  "required": ["positions", "techniques"],
  "properties": {
    "positions": {
      "type": "array",
      "items": {
        "type": "object",
        "required": ["name", "type"],
        "properties": {
          "name": { "type": "string", "maxLength": 120 },
          "type": { "enum": ["TOP", "BOTTOM", "STANDING", "GROUND_NEUTRAL", "SUBMITTED"] },
          "description": { "type": "string" },
          "visibility": { "enum": ["PRIVATE", "PUBLIC"], "default": "PRIVATE" }
        }
      }
    },
    "techniques": {
      "type": "array",
      "items": {
        "type": "object",
        "required": ["name", "category", "minimumBelt", "modality", "startPositionName"],
        "properties": {
          "name": { "type": "string", "maxLength": 120 },
          "category": { "enum": ["SUBMISSION", "SWEEP", "PASS", "TAKEDOWN", "ESCAPE", "TRANSITION"] },
          "description": { "type": "string" },
          "youtubeUrl": { "type": "string" },
          "minimumBelt": { "enum": ["WHITE", "BLUE", "PURPLE", "BROWN", "BLACK"] },
          "modality": { "enum": ["GI", "NOGI", "BOTH"] },
          "startPositionName": { "type": "string" },
          "endPositionName": { "type": "string" },
          "visibility": { "enum": ["PRIVATE", "PUBLIC"], "default": "PRIVATE" }
        }
      }
    }
  }
}
```

(Análogos para `system-import.schema.v1.json` y `ruleset-import.schema.v1.json`).

- [ ] **Step 10.2: Strategy + Template Method**

```java
package com.ossflow.catalog.portability.application;

import com.fasterxml.jackson.databind.JsonNode;

public interface Importer<P> {
    String schemaPath();
    Class<P> payloadType();
    ImportReport runImport(P payload, ImportMode mode);
}
```

```java
public enum ImportMode { MERGE, REPLACE }
```

```java
public record ImportReport(
        ImportMode mode,
        int created,
        int skipped,
        java.util.List<String> warnings,
        java.util.List<String> errors,
        java.util.Map<String, java.util.List<Long>> createdEntities
) {}
```

```java
package com.ossflow.catalog.portability.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.shared.exception.JsonSchemaViolationException;
import com.ossflow.shared.json.JsonSchemaValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractImporter<P> implements Importer<P> {

    protected final JsonSchemaValidator schemaValidator;
    protected final ObjectMapper objectMapper;

    @Transactional
    public ImportReport importJson(JsonNode raw, ImportMode mode) {
        // Template Method
        validate(raw);
        P payload = parse(raw);
        return persist(payload, mode);
    }

    protected void validate(JsonNode raw) {
        var violations = schemaValidator.validate(schemaPath(), raw);
        if (!violations.isEmpty()) {
            throw new JsonSchemaViolationException(
                    "IMPORT_VALIDATION_FAILED",
                    "El payload no cumple el schema",
                    Map.of("validatorStep", "schema", "violations", violations.toString()));
        }
    }

    protected P parse(JsonNode raw) {
        return objectMapper.convertValue(raw, payloadType());
    }

    protected abstract ImportReport persist(P payload, ImportMode mode);

    @Override
    public ImportReport runImport(P payload, ImportMode mode) {
        return persist(payload, mode);
    }
}
```

- [ ] **Step 10.3: CatalogImporter Strategy concreta**

```java
package com.ossflow.catalog.portability.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.catalog.position.application.PositionService;
import com.ossflow.catalog.position.domain.Position;
import com.ossflow.catalog.position.domain.PositionType;
import com.ossflow.catalog.position.domain.Visibility;
import com.ossflow.catalog.technique.application.TechniqueService;
// ... imports
import org.springframework.stereotype.Component;

@Component
public class CatalogImporter extends AbstractImporter<CatalogImporter.Payload> {

    public record Payload(
            java.util.List<PositionDto> positions,
            java.util.List<TechniqueDto> techniques) {

        public record PositionDto(String name, PositionType type, String description, Visibility visibility) {}
        public record TechniqueDto(String name, /* ... */) {}
    }

    private final PositionService positionService;
    private final TechniqueService techniqueService;
    private final com.ossflow.shared.web.CurrentOwner currentOwner;

    public CatalogImporter(/* ctor con todas las deps */) { /* ... */ }

    @Override public String schemaPath() { return "schemas/catalog-import.schema.v1.json"; }
    @Override public Class<Payload> payloadType() { return Payload.class; }

    @Override
    protected ImportReport persist(Payload payload, ImportMode mode) {
        // 1. Si REPLACE → soft-delete todo lo existente del owner
        // 2. Para cada position: si MERGE y existe por nombre → skip; si no → create
        // 3. Resolve startPositionName / endPositionName a IDs (usando Map name→id construido en paso 2)
        // 4. Para cada technique: si MERGE y existe → skip; si no → create con FK resuelta
        // 5. Devolver ImportReport con summary
        // ...
    }
}
```

- [ ] **Step 10.4: ImportController con Strategy selection**

```java
@RestController
@RequestMapping("/api/v1/catalog/import")
@RequiredArgsConstructor
public class ImportController {

    private final CatalogImporter catalogImporter;
    private final SystemImporter systemImporter;
    private final RulesetImporter rulesetImporter;
    private final ObjectMapper objectMapper;

    @PostMapping("/catalog")
    public ImportReport importCatalog(@RequestBody JsonNode body, @RequestParam(defaultValue = "MERGE") ImportMode mode) {
        return catalogImporter.importJson(body, mode);
    }

    @PostMapping("/system") public ImportReport importSystem(...) { return systemImporter.importJson(body, mode); }
    @PostMapping("/rulesets") public ImportReport importRulesets(...) { return rulesetImporter.importJson(body, mode); }
}
```

- [ ] **Step 10.5: Tests integración**

Test que importa catálogo en modo MERGE:
- Pre: catálogo vacío.
- POST con 2 positions y 1 technique → 200 con `created: { positions: 2, techniques: 1 }`.
- POST mismo payload → 200 con `created: 0, skipped: 3`.

Test que importa con schema inválido:
- POST con `type: "INVALID"` → 422 con `code: IMPORT_VALIDATION_FAILED`.

- [ ] **Step 10.6: Commit**

```bash
git add src/main/resources/schemas src/main/java/com/ossflow/catalog/portability src/test/java/com/ossflow/catalog/portability
git commit -m "feat(catalog): importadores con Strategy + Template Method

- Importer<P> interfaz Strategy
- AbstractImporter<P> Template Method: validate (schema) → parse → persist
- CatalogImporter, SystemImporter, RulesetImporter strategies concretas
- POST /api/v1/catalog/import/{catalog,system,rulesets}?mode=MERGE|REPLACE
- @Transactional: rollback total si cualquier paso falla
- Schemas JSON catalog-import, system-import, ruleset-import"
```

---

## Tarea 11: Bounded context journal (Note + Tag + TrainingSession + CompetitionLog/Match)

**Files:**
- Create: `src/main/resources/db/migration/V4__init_journal.sql`
- Create: bounded context completo `journal/{note,tag,trainingsession,competitionlog}/`
- Tests: unit + slice + integration

- [ ] **Step 11.1: V4 migración journal**

```sql
-- src/main/resources/db/migration/V4__init_journal.sql

CREATE TABLE tag (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    name        VARCHAR(60) NOT NULL UNIQUE,
    created_at  TIMESTAMP NOT NULL
);

CREATE TABLE note (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    owner_id        BIGINT NOT NULL DEFAULT 1,
    title           VARCHAR(200) NOT NULL,
    body_markdown   TEXT NOT NULL,
    target_type     VARCHAR(20),
    target_id       BIGINT,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL,
    version         BIGINT NOT NULL DEFAULT 0,
    deleted_at      TIMESTAMP,
    purge_at        TIMESTAMP
);
CREATE INDEX ix_note_owner_target ON note(owner_id, target_type, target_id);
CREATE INDEX ix_note_owner_created ON note(owner_id, created_at);

CREATE TABLE note_tag (
    note_id  BIGINT NOT NULL,
    tag_id   BIGINT NOT NULL,
    PRIMARY KEY (note_id, tag_id),
    FOREIGN KEY (note_id) REFERENCES note(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id)  REFERENCES tag(id)  ON DELETE CASCADE
);

CREATE TABLE training_session (
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    owner_id          BIGINT NOT NULL DEFAULT 1,
    session_date      DATE NOT NULL,
    duration_minutes  INTEGER NOT NULL,
    location          VARCHAR(120),
    intensity         VARCHAR(15) NOT NULL,
    notes_markdown    TEXT,
    created_at        TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP NOT NULL,
    version           BIGINT NOT NULL DEFAULT 0,
    deleted_at        TIMESTAMP,
    purge_at          TIMESTAMP
);
CREATE INDEX ix_training_session_owner_date ON training_session(owner_id, session_date DESC);

CREATE TABLE training_session_technique (
    training_session_id  BIGINT NOT NULL,
    technique_id         BIGINT NOT NULL,
    rep_count            INTEGER,
    notes_markdown       TEXT,
    PRIMARY KEY (training_session_id, technique_id),
    FOREIGN KEY (training_session_id) REFERENCES training_session(id) ON DELETE CASCADE,
    FOREIGN KEY (technique_id)        REFERENCES technique(id)
);

CREATE TABLE competition_log (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    owner_id            BIGINT NOT NULL DEFAULT 1,
    event_name          VARCHAR(200) NOT NULL,
    event_date          DATE NOT NULL,
    weight_category     VARCHAR(30),
    total_matches       INTEGER NOT NULL DEFAULT 0,
    result              VARCHAR(15),
    analysis_markdown   TEXT,
    created_at          TIMESTAMP NOT NULL,
    updated_at          TIMESTAMP NOT NULL,
    version             BIGINT NOT NULL DEFAULT 0,
    deleted_at          TIMESTAMP,
    purge_at            TIMESTAMP
);

CREATE TABLE competition_match (
    id                       INTEGER PRIMARY KEY AUTOINCREMENT,
    competition_log_id       BIGINT NOT NULL,
    match_order              INTEGER NOT NULL,
    opponent_name            VARCHAR(120) NOT NULL,
    opponent_team            VARCHAR(120),
    outcome                  VARCHAR(15) NOT NULL,
    method                   VARCHAR(30) NOT NULL,
    submission_technique_id  BIGINT,
    notes_markdown           TEXT,
    UNIQUE (competition_log_id, match_order),
    FOREIGN KEY (competition_log_id)      REFERENCES competition_log(id) ON DELETE CASCADE,
    FOREIGN KEY (submission_technique_id) REFERENCES technique(id)
);
```

- [ ] **Step 11.2: Aplicar el patrón estándar (port + adapter + service + controller + DTOs + tests) a cada feature de journal**

Mismo patrón de Tareas 5+6 aplicado a:

- `journal/tag/`: GET (autocomplete `?prefix=`), POST, DELETE.
- `journal/note/`: CRUD completo + filtros `?targetType=&targetId=&tag=&q=`. El `NoteService.create()` resuelve tags por nombre (busca o crea) y enlaza vía `note_tag`.
- `journal/trainingsession/`: CRUD + sub-recurso `worked-techniques`.
- `journal/competitionlog/`: CRUD + sub-recurso `matches`.

- [ ] **Step 11.3: Test integración journal**

Casos a cubrir:
- Crear nota con 2 tags nuevos → tags se crean, note_tag se enlaza.
- Crear nota con 1 tag nuevo + 1 existente → solo se crea el nuevo, ambos se enlazan.
- Crear training session con 3 worked techniques → se persisten en pivot.
- Crear competition_log con 5 matches inline → todos persistidos con match_order correcto.
- DELETE competition_log → matches caen por CASCADE.

- [ ] **Step 11.4: Commit**

```bash
git add src/main/resources/db/migration/V4*.sql src/main/java/com/ossflow/journal src/test/java/com/ossflow/journal src/test/java/com/ossflow/integration/JournalIntegrationTest.java
git commit -m "feat(journal): bounded context completo (note, tag, trainingsession, competitionlog)

- V4: 7 tablas con FKs, índices, CASCADE en hijos
- Tag global sin owner; note_tag pivot
- training_session_technique pivot con rep_count y notes
- competition_match con UNIQUE (competition_log_id, match_order)
- CRUD completo + sub-recursos /worked-techniques y /matches
- Tags se crean automáticamente al crear nota si no existen"
```

---

## Tarea 12: Bounded context planning con state machine + Observer

**Files:**
- Create: `src/main/resources/db/migration/V5__init_planning.sql`
- Create: `src/main/java/com/ossflow/planning/{studyplan,studyblock,studyitem}/...`
- Create: `src/main/java/com/ossflow/planning/studyitem/application/{StudyItemStateMachine.java, StudyItemStatusChangedEvent.java}`
- Tests

- [ ] **Step 12.1: V5 migración planning**

```sql
-- src/main/resources/db/migration/V5__init_planning.sql

CREATE TABLE study_plan (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    owner_id        BIGINT NOT NULL DEFAULT 1,
    title           VARCHAR(200) NOT NULL,
    goal_markdown   TEXT,
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    status          VARCHAR(15) NOT NULL,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL,
    version         BIGINT NOT NULL DEFAULT 0,
    deleted_at      TIMESTAMP,
    purge_at        TIMESTAMP
);

CREATE TABLE study_block (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    study_plan_id   BIGINT NOT NULL,
    title           VARCHAR(200) NOT NULL,
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    block_order     INTEGER NOT NULL,
    notes_markdown  TEXT,
    focus_entities  TEXT,
    FOREIGN KEY (study_plan_id) REFERENCES study_plan(id) ON DELETE CASCADE
);

CREATE TABLE study_item (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    study_block_id   BIGINT NOT NULL,
    description      VARCHAR(500) NOT NULL,
    status           VARCHAR(15) NOT NULL,
    target_type      VARCHAR(20),
    target_id        BIGINT,
    due_date         DATE,
    ai_generated     BOOLEAN NOT NULL DEFAULT 0,
    completed_at     TIMESTAMP,
    created_at       TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP NOT NULL,
    version          BIGINT NOT NULL DEFAULT 0,
    FOREIGN KEY (study_block_id) REFERENCES study_block(id) ON DELETE CASCADE
);
```

- [ ] **Step 12.2: StudyItemStateMachine bean**

```java
package com.ossflow.planning.studyitem.application;

import com.ossflow.planning.studyitem.domain.StudyItemStatus;
import com.ossflow.shared.exception.InvalidStateTransitionException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

import static com.ossflow.planning.studyitem.domain.StudyItemStatus.*;

@Component
public class StudyItemStateMachine {

    private static final Map<StudyItemStatus, Set<StudyItemStatus>> ALLOWED = Map.of(
            TODO,    Set.of(DOING, DONE, SKIPPED),
            DOING,   Set.of(TODO, DONE, SKIPPED),
            DONE,    Set.of(TODO),
            SKIPPED, Set.of(TODO)
    );

    public void assertTransition(StudyItemStatus from, StudyItemStatus to) {
        var allowed = ALLOWED.getOrDefault(from, Set.of());
        if (!allowed.contains(to)) {
            throw new InvalidStateTransitionException(
                    "INVALID_STATE_TRANSITION",
                    "No se puede pasar de %s a %s".formatted(from, to),
                    Map.of("from", from, "to", to, "allowed", allowed)
            );
        }
    }
}
```

- [ ] **Step 12.3: StudyItemStatusChangedEvent (Observer)**

```java
package com.ossflow.planning.studyitem.application;

import com.ossflow.planning.studyitem.domain.StudyItemStatus;

public record StudyItemStatusChangedEvent(
        Long studyItemId,
        Long ownerId,
        StudyItemStatus from,
        StudyItemStatus to,
        java.time.Instant when
) {}
```

- [ ] **Step 12.4: StudyItemService dispara evento tras transición**

```java
@Service
@RequiredArgsConstructor
public class StudyItemService {
    private final StudyItemRepositoryPort repository;
    private final StudyItemStateMachine stateMachine;
    private final ApplicationEventPublisher events;

    public StudyItem transition(Long itemId, Long ownerId, StudyItemStatus targetStatus) {
        var item = findById(itemId, ownerId);
        stateMachine.assertTransition(item.status(), targetStatus);
        var updated = repository.save(item.toBuilder()
                .status(targetStatus)
                .completedAt(targetStatus == StudyItemStatus.DONE ? Instant.now() : null)
                .build());
        events.publishEvent(new StudyItemStatusChangedEvent(
                updated.id(), ownerId, item.status(), targetStatus, Instant.now()));
        return updated;
    }
}
```

- [ ] **Step 12.5: Endpoint `/transition`**

```java
@PostMapping("/study-plans/{pid}/blocks/{bid}/items/{iid}/transition")
public StudyItemResponse transition(@PathVariable Long iid, @Valid @RequestBody TransitionRequest req) {
    return mapper.toResponse(service.transition(iid, currentOwner.id(), req.targetStatus()));
}

public record TransitionRequest(@NotNull StudyItemStatus targetStatus) {}
```

- [ ] **Step 12.6: Tests**

Unit del state machine: matriz exhaustiva de transiciones permitidas y prohibidas.
Integration: POST `/transition` válido (200) + inválido (409 con `details.allowed`).
Test del evento: `@RecordApplicationEvents` + assertion de que se emitió.

- [ ] **Step 12.7: Commit**

```bash
git add src/main/resources/db/migration/V5*.sql src/main/java/com/ossflow/planning src/test/java/com/ossflow/planning
git commit -m "feat(planning): study plans 3 niveles + state machine + Observer event

- V5: study_plan/block/item con CASCADE; study_block.focus_entities JSON
- StudyItemStateMachine: TODO/DOING/DONE/SKIPPED con matriz explícita
- /transition endpoint dedicado (no PATCH) → 409 INVALID_STATE_TRANSITION
- StudyItemStatusChangedEvent emitido tras transición exitosa (Observer)
- completed_at se setea automáticamente al pasar a DONE"
```

---

## Tarea 13: Bounded context identity (UserProfile + UserProfileFederation multi-select)

**Files:**
- Create: `src/main/resources/db/migration/V6__init_identity.sql`
- Create: `src/main/java/com/ossflow/identity/profile/...`
- Tests

- [ ] **Step 13.1: V6 migración identity**

```sql
CREATE TABLE user_profile (
    id                       INTEGER PRIMARY KEY AUTOINCREMENT,
    owner_id                 BIGINT NOT NULL UNIQUE,
    display_name             VARCHAR(120) NOT NULL,
    current_belt             VARCHAR(15) NOT NULL,
    belt_since               DATE,
    academy                  VARCHAR(200),
    preferred_modality       VARCHAR(10) NOT NULL,
    onboarding_completed     BOOLEAN NOT NULL DEFAULT 0,
    created_at               TIMESTAMP NOT NULL,
    updated_at               TIMESTAMP NOT NULL,
    version                  BIGINT NOT NULL DEFAULT 0,
    deleted_at               TIMESTAMP,
    purge_at                 TIMESTAMP
);

CREATE TABLE user_profile_federation (
    user_profile_id   BIGINT NOT NULL,
    federation_id     BIGINT NOT NULL,
    is_primary        BOOLEAN NOT NULL DEFAULT 0,
    PRIMARY KEY (user_profile_id, federation_id),
    FOREIGN KEY (user_profile_id) REFERENCES user_profile(id) ON DELETE CASCADE,
    FOREIGN KEY (federation_id)   REFERENCES federation(id)
);

-- Constraint: máximo una federation con is_primary = 1 por user_profile
CREATE UNIQUE INDEX ux_user_profile_one_primary
    ON user_profile_federation(user_profile_id) WHERE is_primary = 1;
```

- [ ] **Step 13.2: Servicio con validación multi-select**

`UserProfileService.replaceFederations(userProfileId, federations)`:
- Verifica que **al menos una** tenga `isPrimary = true` → si no → 422 `PRIMARY_FEDERATION_REQUIRED`.
- Verifica que **exactamente una** tenga `isPrimary = true` → si no → 422 `MULTIPLE_PRIMARY_FEDERATIONS`.
- Reemplaza el set: borra todas las existentes y crea las nuevas (transaccional).

- [ ] **Step 13.3: Endpoints**

- `GET /api/v1/identity/profile` → 200 si existe, 404 con `code: PROFILE_NOT_FOUND` si no.
- `POST /api/v1/identity/profile` → 201 si no existe, 409 con `code: PROFILE_ALREADY_EXISTS`.
- `PUT/PATCH /api/v1/identity/profile`.
- `PUT /api/v1/identity/profile/federations` → recibe `[{ federationId, isPrimary }]`.

- [ ] **Step 13.4: Tests**

Unit del servicio:
- `replaceFederations` con cero primary → 422.
- `replaceFederations` con dos primary → 422.
- `replaceFederations` con una primary → OK.

Integration:
- POST profile sin haber hecho onboarding → 201.
- GET profile recién creado → 200 con `onboardingCompleted: true` si lo enviaste así.

- [ ] **Step 13.5: Commit**

```bash
git add src/main/resources/db/migration/V6*.sql src/main/java/com/ossflow/identity src/test/java/com/ossflow/identity
git commit -m "feat(identity): UserProfile + multi-select federations con is_primary

- V6: user_profile (UNIQUE owner_id), user_profile_federation pivot
- Constraint UNIQUE WHERE is_primary garantiza una sola primaria
- replaceFederations valida exactamente una primary → 422 si no
- Endpoints CRUD profile + PUT /profile/federations"
```

---

## Tarea 14: Job de purga programado (soft delete > 30 días)

**Files:**
- Create: `src/main/java/com/ossflow/shared/persistence/SoftDeletePurgeJob.java`
- Create: `src/main/java/com/ossflow/shared/config/ScheduledTasksConfig.java`
- Create: `src/test/java/com/ossflow/integration/SoftDeletePurgeIntegrationTest.java`

- [ ] **Step 14.1: ScheduledTasksConfig**

```java
package com.ossflow.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class ScheduledTasksConfig {}
```

- [ ] **Step 14.2: SoftDeletePurgeJob**

```java
package com.ossflow.shared.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
public class SoftDeletePurgeJob {

    @PersistenceContext private EntityManager em;

    private static final List<String> TABLES = List.of(
            "position", "technique", "system",
            "note", "training_session", "competition_log",
            "study_plan",
            "user_profile"
    );

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void purgeExpired() {
        Instant now = Instant.now();
        for (String table : TABLES) {
            int deleted = em.createNativeQuery(
                    "DELETE FROM " + table + " WHERE purge_at IS NOT NULL AND purge_at < ?1")
                    .setParameter(1, now)
                    .executeUpdate();
            if (deleted > 0) {
                log.info("Purga {}: {} registros eliminados", table, deleted);
            }
        }
    }
}
```

- [ ] **Step 14.3: Test integración fuerza ejecución manual**

```java
@SpringBootTest
@ActiveProfiles("test")
class SoftDeletePurgeIntegrationTest {

    @Autowired SoftDeletePurgeJob job;
    @Autowired PositionPersistenceAdapter positions;
    @Autowired EntityManager em;

    @Test
    @Transactional
    void should_purge_records_past_purge_at() {
        var p = positions.save(Position.builder().ownerId(1L).name("X").type(PositionType.TOP).visibility(Visibility.PRIVATE).build());
        positions.softDelete(p.id(), 1L);

        // forzar purge_at en el pasado
        em.createNativeQuery("UPDATE position SET purge_at = ?1 WHERE id = ?2")
                .setParameter(1, Instant.now().minus(Duration.ofDays(40)))
                .setParameter(2, p.id())
                .executeUpdate();

        job.purgeExpired();

        var count = em.createNativeQuery("SELECT COUNT(*) FROM position WHERE id = ?1")
                .setParameter(1, p.id())
                .getSingleResult();
        assertThat(((Number) count).intValue()).isZero();
    }
}
```

- [ ] **Step 14.4: Commit**

```bash
git add src/main/java/com/ossflow/shared/persistence/SoftDeletePurgeJob.java src/main/java/com/ossflow/shared/config/ScheduledTasksConfig.java src/test/java/com/ossflow/integration/SoftDeletePurgeIntegrationTest.java
git commit -m "feat(shared): job programado de purga de soft-deletes vencidos

- @Scheduled(cron 0 0 3 * * *) recorre 8 tablas con purge_at
- DELETE nativo de filas con purge_at < now()
- Logs por tabla con número de registros eliminados
- Test fuerza purge_at al pasado y verifica eliminación"
```

---

## Tarea 15: Export full streaming + integración cross-context

**Files:**
- Create: `src/main/java/com/ossflow/portability/infrastructure/web/FullExportController.java`
- Create: `src/main/java/com/ossflow/{catalog,journal,planning,identity}/portability/...Exporter.java`
- Tests

- [ ] **Step 15.1: Cada bounded context tiene su exporter**

```java
package com.ossflow.catalog.portability;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CatalogExporter {

    private final PositionService positionService;
    private final TechniqueService techniqueService;
    private final SystemService systemService;

    public Map<String, Object> exportFor(Long ownerId) {
        return Map.of(
                "positions",  positionService.list(ownerId, null, Pageable.unpaged()).getContent(),
                "techniques", techniqueService.list(ownerId, /*filtros*/ null, Pageable.unpaged()).getContent(),
                "systems",    systemService.list(ownerId, null, Pageable.unpaged()).getContent()
        );
    }
}
```

(Análogos para journal, planning, identity).

- [ ] **Step 15.2: FullExportController orquestador**

```java
@RestController
@RequestMapping("/api/v1/export")
@RequiredArgsConstructor
public class FullExportController {

    private final CatalogExporter catalogExporter;
    private final JournalExporter journalExporter;
    private final PlanningExporter planningExporter;
    private final IdentityExporter identityExporter;
    private final CurrentOwner currentOwner;

    @GetMapping(value = "/full", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> exportFull() {
        Long ownerId = currentOwner.id();
        var dump = Map.of(
                "schemaVersion", "v1",
                "exportedAt", Instant.now().toString(),
                "ownerId", ownerId,
                "catalog",  catalogExporter.exportFor(ownerId),
                "journal",  journalExporter.exportFor(ownerId),
                "planning", planningExporter.exportFor(ownerId),
                "identity", identityExporter.exportFor(ownerId)
        );
        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=\"ossflow-backup-%s.json\"".formatted(LocalDate.now()))
                .body(dump);
    }
}
```

- [ ] **Step 15.3: Test integración**

POST varias entidades + GET `/export/full` → 200 con todas las secciones presentes y `Content-Disposition` correcto.

- [ ] **Step 15.4: Commit**

```bash
git add src/main/java/com/ossflow/portability src/main/java/com/ossflow/{catalog,journal,planning,identity}/portability src/test/java/com/ossflow/integration/FullExportIntegrationTest.java
git commit -m "feat(portability): export full cross-context

- Cada bounded context expone su Exporter
- FullExportController orquesta y compone el JSON dump
- Content-Disposition: attachment con nombre fechado
- Test integration verifica las 4 secciones presentes"
```

---

## Tarea 16: OpenAPI + Swagger UI + propagación traceId entre cliente y servidor

**Files:**
- Create: `src/main/java/com/ossflow/shared/config/OpenApiConfig.java`
- Modify: anotaciones `@Operation`, `@ApiResponse`, `@Tag` en cada controller
- Create: `src/test/java/com/ossflow/integration/TraceIdPropagationIntegrationTest.java`

- [ ] **Step 16.1: OpenApiConfig**

```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI ossflowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OssFlow API")
                        .description("Segundo cerebro para BJJ — API REST")
                        .version("v1"))
                .components(new Components().addHeaders("X-Trace-Id",
                        new Header().description("Trace ID propagado end-to-end")
                                .schema(new StringSchema())));
    }
}
```

- [ ] **Step 16.2: Anotar cada controller**

Ejemplo en PositionController:

```java
@Tag(name = "Catalog · Positions")
@RestController
@RequestMapping("/api/v1/catalog/positions")
public class PositionController {

    @Operation(summary = "Crear posición", responses = {
        @ApiResponse(responseCode = "201", description = "Creada"),
        @ApiResponse(responseCode = "400", description = "DTO inválido"),
        @ApiResponse(responseCode = "409", description = "Nombre duplicado")
    })
    @PostMapping
    public ResponseEntity<PositionResponse> create(...) { ... }
}
```

- [ ] **Step 16.3: Test traceId propagation**

```java
@Test
void should_echo_trace_id_in_response_header() throws Exception {
    var traceId = "trace-test-123";
    mvc.perform(get("/api/v1/catalog/positions").header("X-Trace-Id", traceId))
            .andExpect(status().isOk())
            .andExpect(header().string("X-Trace-Id", traceId));
}

@Test
void should_generate_trace_id_when_missing() throws Exception {
    var result = mvc.perform(get("/api/v1/catalog/positions"))
            .andExpect(status().isOk())
            .andExpect(header().exists("X-Trace-Id"))
            .andReturn();
    var trace = result.getResponse().getHeader("X-Trace-Id");
    assertThat(trace).isNotBlank();
}

@Test
void should_include_trace_id_in_500_response_body() throws Exception {
    // Endpoint mock que lanza RuntimeException; o mock service
    // ...
    mvc.perform(get("/api/v1/catalog/positions/9999999"))
            .andExpect(jsonPath("$.traceId").exists());
}
```

- [ ] **Step 16.4: Verificar Swagger UI manualmente**

Run: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
Open: `http://localhost:8080/swagger-ui.html`
Verificar: lista de endpoints agrupados por @Tag, modelos, request bodies.

- [ ] **Step 16.5: Commit**

```bash
git add src/main/java/com/ossflow/shared/config/OpenApiConfig.java src/main/java/com/ossflow/**/web/*Controller.java src/test/java/com/ossflow/integration/TraceIdPropagationIntegrationTest.java
git commit -m "feat(shared): OpenAPI + Swagger UI + tests traceId end-to-end

- OpenApiConfig con info y header X-Trace-Id documentado
- @Tag, @Operation, @ApiResponse en cada controller
- Swagger UI activo en perfil dev (/swagger-ui.html)
- Integration tests validan: respeta header del cliente, genera si falta,
  aparece en cuerpo de 500"
```

---

## Tarea 17: Logging estructurado (Logback dev legible / prod JSON)

**Files:**
- Create: `src/main/resources/logback-spring.xml`

- [ ] **Step 17.1: logback-spring.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <springProfile name="dev,test">
        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{HH:mm:ss.SSS} %-5level [traceId=%X{traceId:-}] %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="INFO"><appender-ref ref="CONSOLE"/></root>
        <logger name="com.ossflow" level="DEBUG"/>
    </springProfile>

    <springProfile name="prod">
        <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
            <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
                <providers>
                    <timestamp/>
                    <logLevel/>
                    <loggerName/>
                    <message/>
                    <stackTrace/>
                    <mdc/>
                    <pattern>
                        <pattern>{"app":"ossflow","env":"prod"}</pattern>
                    </pattern>
                </providers>
            </encoder>
        </appender>
        <root level="INFO"><appender-ref ref="JSON"/></root>
    </springProfile>

</configuration>
```

- [ ] **Step 17.2: Commit**

```bash
git add src/main/resources/logback-spring.xml
git commit -m "feat(shared): logback dev pattern + prod JSON estructurado

- Profile dev/test: pattern legible con [traceId=X] en cada línea
- Profile prod: JSON con logstash-logback-encoder (ready para Loki/ELK)
- MDC traceId incluido automáticamente en ambos formatos"
```

---

## Tarea 18: Checkstyle (god files enforcement)

**Files:**
- Create: `checkstyle.xml`
- Modify: `pom.xml` (añadir plugin checkstyle)

- [ ] **Step 18.1: checkstyle.xml**

```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
    "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
    "https://checkstyle.org/dtds/configuration_1_3.dtd">

<module name="Checker">
    <module name="FileLength">
        <property name="max" value="600"/>
        <property name="fileExtensions" value="java"/>
    </module>

    <module name="TreeWalker">
        <module name="MethodLength">
            <property name="max" value="80"/>
        </module>
        <module name="CyclomaticComplexity">
            <property name="max" value="15"/>
        </module>
    </module>
</module>
```

- [ ] **Step 18.2: Plugin checkstyle en pom.xml**

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.6.0</version>
    <executions>
        <execution>
            <id>validate</id>
            <phase>verify</phase>
            <configuration>
                <configLocation>checkstyle.xml</configLocation>
                <failOnViolation>true</failOnViolation>
                <consoleOutput>true</consoleOutput>
            </configuration>
            <goals><goal>check</goal></goals>
        </execution>
    </executions>
</plugin>
```

- [ ] **Step 18.3: Run verify para confirmar que los archivos existentes cumplen**

Run: `mvn -B verify`
Expected: BUILD SUCCESS. Si algún archivo supera el límite duro: refactorizar inmediatamente.

- [ ] **Step 18.4: Commit**

```bash
git add checkstyle.xml pom.xml
git commit -m "feat(ci): Checkstyle enforce no god files

- FileLength max 600
- MethodLength max 80
- CyclomaticComplexity max 15
- maven-checkstyle-plugin en phase verify, failOnViolation true"
```

---

## Tarea 19: Dockerfile multi-stage con eclipse-temurin:25-jre-noble

**Files:**
- Create: `Dockerfile`
- Create: `.dockerignore`

**Justificación de la imagen runtime:** se eligió `eclipse-temurin:25-jre-noble` (oficial de Eclipse Adoptium) en lugar de `gcr.io/distroless/java25-debian12` porque, a fecha de este proyecto, Google **aún no publica imágenes distroless para Java 25** (LTS de septiembre 2025); el retraso histórico tras cada nueva LTS hace inviable depender de ella todavía. Cuando aparezca, migrar es cambiar una línea `FROM`.

- [ ] **Step 19.1: Dockerfile**

```dockerfile
FROM maven:3.9-eclipse-temurin-25 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B
RUN java -Djarmode=layertools -jar target/*.jar extract --destination target/extracted

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

- [ ] **Step 19.2: .dockerignore**

```
target/
.git/
.idea/
.vscode/
*.iml
docs/
.DS_Store
.agents/
.claude/
README.md
```

- [ ] **Step 19.3: Build local y smoke test**

```bash
docker build -t ossflow-backend:dev .
docker run -d --name ossflow-test -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev ossflow-backend:dev
sleep 20
curl -f http://localhost:8080/actuator/health
docker stop ossflow-test && docker rm ossflow-test
```

Expected: `{"status":"UP"}`.

- [ ] **Step 19.4: Commit**

```bash
git add Dockerfile .dockerignore
git commit -m "feat(docker): Dockerfile multi-stage Java 25 con eclipse-temurin

- Stage 1: maven:3.9-eclipse-temurin-25 con cache de dependencias
- layertools extract para mejor cache de Docker
- Stage 2: eclipse-temurin:25-jre-noble (oficial Eclipse Adoptium)
- Usuario spring no-root creado explícitamente
- Distroless Java 25 descartado (no publicado aún por Google)
- Smoke test local OK contra /actuator/health"
```

---

## Tarea 20: GitHub Actions CI workflow

**Files:**
- Create: `.github/workflows/ci.yml`

- [ ] **Step 20.1: ci.yml**

```yaml
name: CI

on:
  pull_request:
  push:
    branches: [main]

jobs:
  build-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '25'
          cache: maven

      - name: Build + test + jacoco + checkstyle
        run: mvn -B verify

      - name: Upload jacoco report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: jacoco-report
          path: target/site/jacoco/

      - name: Top-10 archivos más largos (god file watch)
        run: |
          echo "## Top 10 archivos más largos" >> $GITHUB_STEP_SUMMARY
          find src/main/java -name '*.java' | xargs wc -l | sort -rn | head -10 >> $GITHUB_STEP_SUMMARY
```

- [ ] **Step 20.2: Commit y push para verificar el workflow**

```bash
git add .github/workflows/ci.yml
git commit -m "feat(ci): GitHub Actions workflow CI con maven verify"
```

---

## Tarea 21: GitHub Actions Release workflow + publicación OpenAPI artifact

**Files:**
- Create: `.github/workflows/release.yml`
- Modify: `pom.xml` (springdoc-openapi-maven-plugin para generar openapi.json en build)

- [ ] **Step 21.1: Plugin springdoc en pom.xml**

```xml
<plugin>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-maven-plugin</artifactId>
    <version>1.4</version>
    <executions>
        <execution>
            <id>integration-test</id>
            <goals><goal>generate</goal></goals>
        </execution>
    </executions>
    <configuration>
        <apiDocsUrl>http://localhost:8080/v3/api-docs</apiDocsUrl>
        <outputFileName>openapi.json</outputFileName>
        <outputDir>${project.build.directory}</outputDir>
    </configuration>
</plugin>
```

- [ ] **Step 21.2: release.yml**

```yaml
name: Release

on:
  push:
    tags: ['v*.*.*']

permissions:
  contents: write
  packages: write

jobs:
  build-and-push:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: docker/setup-buildx-action@v3

      - uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - id: meta
        uses: docker/metadata-action@v5
        with:
          images: ghcr.io/${{ github.repository_owner }}/ossflow-backend
          tags: |
            type=ref,event=tag
            type=semver,pattern={{version}}
            type=semver,pattern={{major}}.{{minor}}
            type=raw,value=latest

      - uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: ${{ steps.meta.outputs.tags }}
          labels: ${{ steps.meta.outputs.labels }}
          cache-from: type=gha
          cache-to: type=gha,mode=max

      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '25'
          cache: maven

      - name: Generar openapi.json
        run: |
          mvn -B spring-boot:start
          curl -s http://localhost:8080/v3/api-docs > openapi.json
          mvn -B spring-boot:stop

      - name: Publicar openapi.json en release
        uses: softprops/action-gh-release@v2
        with:
          files: openapi.json
```

- [ ] **Step 21.3: Notas de uso**

- Para hacer un release: `git tag v0.1.0 && git push origin v0.1.0`.
- Imágenes publicadas en `ghcr.io/<usuario>/ossflow-backend:0.1.0`, `:0.1`, `:latest`.
- `openapi.json` adjunto al GitHub Release. El frontend lo consumirá con `openapi-typescript` para regenerar tipos.

- [ ] **Step 21.4: Commit**

```bash
git add .github/workflows/release.yml pom.xml
git commit -m "feat(ci): release workflow Docker push ghcr + openapi.json artifact

- Trigger en tag v*.*.*
- Login ghcr con GITHUB_TOKEN
- Tags semver completos (v1.2.3, 1.2, 1.2.3, latest)
- Build + push Docker con cache GHA
- Genera openapi.json arrancando temporalmente y lo publica en release"
```

---

## Self-review final del plan

Ejecutar mentalmente esta checklist tras la última tarea:

**Cobertura del spec:**
- ✅ Sección 1 arquitectura → Tareas 1-4 (bootstrap + bounded contexts implícitos por estructura).
- ✅ Sección 2 modelo de datos → Tareas 4, 8, 9, 11, 12, 13 (las 18 tablas distribuidas).
- ✅ Sección 3 API REST → Tareas 6, 7, 8, 9, 11, 12, 13 (CRUDs completos por contexto).
- ✅ Sección 4 manejo errores y validación → Tarea 3 (jerarquía + handler + traceId) + Tarea 9 (Chain of Responsibility).
- ✅ Sección 5 testing → cubierto en todas las tareas (unit, slice, integration) + Tarea 18 (god files enforcement).
- ✅ Sección 6 frontend → fuera de alcance (Plan 2).
- ✅ Sección 7 docker/CI → Tareas 19, 20, 21.
- ✅ Sección 8 roadmap → este plan implementa fases 0-14.

**Patrones aplicados:**
- ✅ Strategy: Importer<T> (T10).
- ✅ Template Method: AbstractImporter (T10).
- ✅ Chain of Responsibility: ValidationChain + 3 steps (T9).
- ✅ State: StudyItemStateMachine (T12).
- ✅ Observer: StudyItemStatusChangedEvent (T12).
- ✅ Builder: Lombok @Builder en domain records (todos).
- ✅ Singleton implícito: @Service, @Component (todos).

**Cosas que NO entran en este plan (documentadas):**
- Frontend completo → Plan 2.
- Repo `ossflow-deploy` → Plan 3.
- Spring Security / multi-usuario → futuro.
- Circuit Breaker → futuro (cuando entre IA externa).

---

**Plan listo para ejecutar.** Total: 21 tareas, ~80 horas estimadas, cubriendo fases 0-14 del roadmap.
