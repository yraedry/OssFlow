package com.ossflow.catalog.system.application.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.ossflow.catalog.position.application.port.PositionRepositoryPort;
import com.ossflow.catalog.system.domain.OssSystem;
import com.ossflow.catalog.technique.application.port.TechniqueRepositoryPort;
import com.ossflow.shared.validation.ValidationContext;
import com.ossflow.shared.validation.ValidationResult;
import com.ossflow.shared.validation.ValidationStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class FlowReferentialValidationStep implements ValidationStep<OssSystem> {

    private final PositionRepositoryPort positionRepository;
    private final TechniqueRepositoryPort techniqueRepository;

    @Override
    public ValidationResult validate(OssSystem system, ValidationContext ctx) {
        JsonNode flow = ctx.get("flowNode");
        if (flow == null) return new ValidationResult.Ok();

        for (JsonNode node : flow.get("nodes")) {
            String kind = node.get("kind").asText();
            long refId = node.get("refId").asLong();

            if ("POSITION".equals(kind)) {
                if (positionRepository.findById(refId, system.ownerId()).isEmpty()) {
                    return new ValidationResult.Fail("FLOW_POSITION_NOT_FOUND",
                            "Posición referenciada no encontrada: %d".formatted(refId),
                            Map.of("refId", refId, "kind", kind));
                }
            } else if ("TECHNIQUE".equals(kind)) {
                if (techniqueRepository.findById(refId, system.ownerId()).isEmpty()) {
                    return new ValidationResult.Fail("FLOW_TECHNIQUE_NOT_FOUND",
                            "Técnica referenciada no encontrada: %d".formatted(refId),
                            Map.of("refId", refId, "kind", kind));
                }
            }
        }
        return new ValidationResult.Ok();
    }
}
