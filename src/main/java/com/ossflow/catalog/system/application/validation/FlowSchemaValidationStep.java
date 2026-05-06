package com.ossflow.catalog.system.application.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.catalog.system.domain.OssSystem;
import com.ossflow.shared.json.JsonSchemaValidator;
import com.ossflow.shared.validation.ValidationContext;
import com.ossflow.shared.validation.ValidationResult;
import com.ossflow.shared.validation.ValidationStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FlowSchemaValidationStep implements ValidationStep<OssSystem> {

    private static final String SCHEMA_PATH = "schemas/system-flow.schema.v1.json";

    private final JsonSchemaValidator schemaValidator;
    private final ObjectMapper objectMapper;

    @Override
    public ValidationResult validate(OssSystem system, ValidationContext ctx) {
        try {
            JsonNode node = objectMapper.readTree(system.flowDefinition());
            ctx.put("flowNode", node);
            var errors = schemaValidator.validate(SCHEMA_PATH, node);
            if (!errors.isEmpty()) {
                String msg = errors.stream().map(Object::toString).collect(Collectors.joining("; "));
                return new ValidationResult.Fail("FLOW_SCHEMA_INVALID",
                        "El flowDefinition no cumple el esquema JSON: " + msg,
                        Map.of("violations", msg));
            }
            return new ValidationResult.Ok();
        } catch (Exception e) {
            return new ValidationResult.Fail("FLOW_JSON_PARSE_ERROR",
                    "flowDefinition no es JSON válido: " + e.getMessage(),
                    Map.of("cause", e.getMessage()));
        }
    }
}
