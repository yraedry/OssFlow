package com.ossflow.catalog.portability.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.catalog.federation.application.FederationService;
import com.ossflow.catalog.federation.domain.Federation;
import com.ossflow.catalog.ruleset.application.RulesetService;
import com.ossflow.catalog.ruleset.domain.LegalityStatus;
import com.ossflow.catalog.ruleset.domain.Ruleset;
import com.ossflow.catalog.ruleset.domain.RulesetTechnique;
import com.ossflow.catalog.technique.application.TechniqueService;
import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import com.ossflow.shared.exception.ConflictException;
import com.ossflow.shared.json.JsonSchemaValidator;
import com.ossflow.shared.web.CurrentOwner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RulesetImporter extends AbstractImporter<RulesetImporter.Payload> {

    public record TechniqueEntryDto(String techniqueName, LegalityStatus status, String conditionNotes) {}
    public record RulesetDto(String federationCode, Belt belt, Modality modality,
                             LocalDate effectiveFrom, LocalDate effectiveTo, String sourceUrl,
                             List<TechniqueEntryDto> techniques) {}
    public record Payload(List<RulesetDto> rulesets) {}

    private final FederationService federationService;
    private final RulesetService rulesetService;
    private final TechniqueService techniqueService;
    private final CurrentOwner currentOwner;

    public RulesetImporter(JsonSchemaValidator schemaValidator, ObjectMapper objectMapper,
                           FederationService federationService, RulesetService rulesetService,
                           TechniqueService techniqueService, CurrentOwner currentOwner) {
        super(schemaValidator, objectMapper);
        this.federationService = federationService;
        this.rulesetService = rulesetService;
        this.techniqueService = techniqueService;
        this.currentOwner = currentOwner;
    }

    @Override public String schemaPath() { return "schemas/ruleset-import.schema.v1.json"; }
    @Override public Class<Payload> payloadType() { return Payload.class; }

    @Override
    protected ImportReport persist(Payload payload, ImportMode mode) {
        Long ownerId = currentOwner.id();
        int created = 0, skipped = 0;
        List<String> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        List<Long> rulesetIds = new ArrayList<>();

        List<Federation> federations = federationService.findAll();
        Map<String, Long> codeToFedId = new HashMap<>();
        for (Federation f : federations) codeToFedId.put(f.code(), f.id());

        for (RulesetDto dto : payload.rulesets()) {
            Long fedId = codeToFedId.get(dto.federationCode());
            if (fedId == null) {
                errors.add("Federación '%s' no encontrada".formatted(dto.federationCode()));
                continue;
            }
            try {
                Ruleset ruleset = Ruleset.builder().federationId(fedId).belt(dto.belt())
                        .modality(dto.modality()).effectiveFrom(dto.effectiveFrom())
                        .effectiveTo(dto.effectiveTo()).sourceUrl(dto.sourceUrl()).build();
                Ruleset saved = rulesetService.create(ruleset);
                rulesetIds.add(saved.id());
                created++;

                if (dto.techniques() != null) {
                    for (TechniqueEntryDto te : dto.techniques()) {
                        var page = techniqueService.list(ownerId, null, null, null, null, null, null, PageRequest.of(0, 200));
                        page.stream().filter(t -> t.name().equals(te.techniqueName())).findFirst().ifPresentOrElse(
                                t -> rulesetService.upsertTechnique(saved.id(),
                                        RulesetTechnique.builder().rulesetId(saved.id()).techniqueId(t.id())
                                                .status(te.status()).conditionNotes(te.conditionNotes()).build()),
                                () -> warnings.add("Técnica '%s' no encontrada, omitida de reglamento".formatted(te.techniqueName()))
                        );
                    }
                }
            } catch (ConflictException e) {
                warnings.add("Reglamento %s/%s/%s/%s ya existe, omitido".formatted(
                        dto.federationCode(), dto.belt(), dto.modality(), dto.effectiveFrom()));
                skipped++;
            }
        }

        Map<String, List<Long>> entities = new HashMap<>();
        entities.put("rulesets", rulesetIds);
        log.info("RulesetImporter: created={} skipped={}", created, skipped);
        return new ImportReport(mode, created, skipped, warnings, errors, entities);
    }
}
