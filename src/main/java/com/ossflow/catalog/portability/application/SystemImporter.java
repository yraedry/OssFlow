package com.ossflow.catalog.portability.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.catalog.position.domain.Visibility;
import com.ossflow.catalog.system.application.SystemService;
import com.ossflow.catalog.system.domain.OssSystem;
import com.ossflow.shared.exception.DuplicateNameException;
import com.ossflow.shared.json.JsonSchemaValidator;
import com.ossflow.shared.web.CurrentOwner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SystemImporter extends AbstractImporter<SystemImporter.Payload> {

    public record SystemDto(String name, String description, String flowDefinition, Visibility visibility) {}
    public record Payload(List<SystemDto> systems) {}

    private final SystemService systemService;
    private final CurrentOwner currentOwner;

    public SystemImporter(JsonSchemaValidator schemaValidator, ObjectMapper objectMapper,
                          SystemService systemService, CurrentOwner currentOwner) {
        super(schemaValidator, objectMapper);
        this.systemService = systemService;
        this.currentOwner = currentOwner;
    }

    @Override public String schemaPath() { return "schemas/system-import.schema.v1.json"; }
    @Override public Class<Payload> payloadType() { return Payload.class; }

    @Override
    protected ImportReport persist(Payload payload, ImportMode mode) {
        Long ownerId = currentOwner.id();
        int created = 0, skipped = 0;
        List<String> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        List<Long> systemIds = new ArrayList<>();

        for (SystemDto dto : payload.systems()) {
            try {
                Visibility vis = dto.visibility() != null ? dto.visibility() : Visibility.PRIVATE;
                OssSystem system = OssSystem.builder().ownerId(ownerId).name(dto.name())
                        .description(dto.description()).flowDefinition(dto.flowDefinition())
                        .flowSchemaVersion("v1").visibility(vis).build();
                OssSystem saved = systemService.create(system);
                systemIds.add(saved.id());
                created++;
            } catch (DuplicateNameException e) {
                warnings.add("Sistema '%s' ya existe, omitido".formatted(dto.name()));
                skipped++;
            } catch (Exception e) {
                errors.add("Sistema '%s': %s".formatted(dto.name(), e.getMessage()));
            }
        }

        Map<String, List<Long>> entities = new HashMap<>();
        entities.put("systems", systemIds);
        log.info("SystemImporter: created={} skipped={}", created, skipped);
        return new ImportReport(mode, created, skipped, warnings, errors, entities);
    }
}
