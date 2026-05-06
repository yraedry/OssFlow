package com.ossflow.catalog.portability.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.catalog.position.application.PositionService;
import com.ossflow.catalog.position.domain.Position;
import com.ossflow.catalog.position.domain.PositionType;
import com.ossflow.catalog.position.domain.Visibility;
import com.ossflow.catalog.technique.application.TechniqueService;
import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import com.ossflow.catalog.technique.domain.Technique;
import com.ossflow.catalog.technique.domain.TechniqueCategory;
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
public class CatalogImporter extends AbstractImporter<CatalogImporter.Payload> {

    public record PositionDto(String name, PositionType type, String description, Visibility visibility) {}
    public record TechniqueDto(String name, TechniqueCategory category, String description, String youtubeUrl,
                               Belt minimumBelt, Modality modality, String startPositionName,
                               String endPositionName, Visibility visibility) {}
    public record Payload(List<PositionDto> positions, List<TechniqueDto> techniques) {}

    private final PositionService positionService;
    private final TechniqueService techniqueService;
    private final CurrentOwner currentOwner;

    public CatalogImporter(JsonSchemaValidator schemaValidator, ObjectMapper objectMapper,
                           PositionService positionService, TechniqueService techniqueService,
                           CurrentOwner currentOwner) {
        super(schemaValidator, objectMapper);
        this.positionService = positionService;
        this.techniqueService = techniqueService;
        this.currentOwner = currentOwner;
    }

    @Override public String schemaPath() { return "schemas/catalog-import.schema.v1.json"; }
    @Override public Class<Payload> payloadType() { return Payload.class; }

    @Override
    protected ImportReport persist(Payload payload, ImportMode mode) {
        Long ownerId = currentOwner.id();
        int created = 0, skipped = 0;
        List<String> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        Map<String, List<Long>> entities = new HashMap<>();
        List<Long> positionIds = new ArrayList<>();
        List<Long> techniqueIds = new ArrayList<>();

        Map<String, Long> nameToPositionId = new HashMap<>();

        for (PositionDto dto : payload.positions()) {
            try {
                Visibility vis = dto.visibility() != null ? dto.visibility() : Visibility.PRIVATE;
                Position pos = Position.builder().ownerId(ownerId).name(dto.name())
                        .type(dto.type()).description(dto.description()).visibility(vis).build();
                Position saved = positionService.create(pos);
                nameToPositionId.put(saved.name(), saved.id());
                positionIds.add(saved.id());
                created++;
            } catch (DuplicateNameException e) {
                warnings.add("Posición '%s' ya existe, omitida".formatted(dto.name()));
                skipped++;
            }
        }

        for (TechniqueDto dto : payload.techniques()) {
            Long startId = nameToPositionId.get(dto.startPositionName());
            if (startId == null) {
                errors.add("Técnica '%s': startPositionName '%s' no encontrada en este import"
                        .formatted(dto.name(), dto.startPositionName()));
                continue;
            }
            Long endId = dto.endPositionName() != null ? nameToPositionId.get(dto.endPositionName()) : null;
            try {
                Visibility vis = dto.visibility() != null ? dto.visibility() : Visibility.PRIVATE;
                Technique t = Technique.builder().ownerId(ownerId).name(dto.name())
                        .category(dto.category()).description(dto.description()).youtubeUrl(dto.youtubeUrl())
                        .minimumBelt(dto.minimumBelt()).modality(dto.modality())
                        .startPositionId(startId).endPositionId(endId).visibility(vis).build();
                Technique saved = techniqueService.create(t);
                techniqueIds.add(saved.id());
                created++;
            } catch (DuplicateNameException e) {
                warnings.add("Técnica '%s' ya existe, omitida".formatted(dto.name()));
                skipped++;
            }
        }

        entities.put("positions", positionIds);
        entities.put("techniques", techniqueIds);
        log.info("CatalogImporter: created={} skipped={} errors={}", created, skipped, errors.size());
        return new ImportReport(mode, created, skipped, warnings, errors, entities);
    }
}
