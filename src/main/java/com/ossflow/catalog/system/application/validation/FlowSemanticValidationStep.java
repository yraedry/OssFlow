package com.ossflow.catalog.system.application.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.ossflow.catalog.system.domain.OssSystem;
import com.ossflow.shared.validation.ValidationContext;
import com.ossflow.shared.validation.ValidationResult;
import com.ossflow.shared.validation.ValidationStep;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class FlowSemanticValidationStep implements ValidationStep<OssSystem> {

    @Override
    public ValidationResult validate(OssSystem system, ValidationContext ctx) {
        JsonNode flow = ctx.get("flowNode");
        if (flow == null) return new ValidationResult.Ok();

        Set<String> nodeIds = new HashSet<>();
        for (JsonNode node : flow.get("nodes")) {
            String id = node.get("id").asText();
            if (!nodeIds.add(id)) {
                return new ValidationResult.Fail("FLOW_DUPLICATE_NODE_ID",
                        "El nodo id '%s' está duplicado en el flow".formatted(id),
                        Map.of("nodeId", id));
            }
        }

        for (JsonNode edge : flow.get("edges")) {
            String from = edge.get("from").asText();
            String to = edge.get("to").asText();
            if (!nodeIds.contains(from)) {
                return new ValidationResult.Fail("FLOW_EDGE_UNKNOWN_FROM",
                        "El edge referencia nodo 'from' desconocido: %s".formatted(from),
                        Map.of("nodeId", from));
            }
            if (!nodeIds.contains(to)) {
                return new ValidationResult.Fail("FLOW_EDGE_UNKNOWN_TO",
                        "El edge referencia nodo 'to' desconocido: %s".formatted(to),
                        Map.of("nodeId", to));
            }
        }
        return new ValidationResult.Ok();
    }
}
