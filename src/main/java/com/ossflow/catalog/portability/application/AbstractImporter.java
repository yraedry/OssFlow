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

    @Override
    @Transactional
    public ImportReport importJson(JsonNode raw, ImportMode mode) {
        validate(raw);
        P payload = parse(raw);
        return persist(payload, mode);
    }

    protected void validate(JsonNode raw) {
        var violations = schemaValidator.validate(schemaPath(), raw);
        if (!violations.isEmpty()) {
            throw new JsonSchemaViolationException(
                    "IMPORT_VALIDATION_FAILED",
                    "El payload no cumple el schema de importación",
                    Map.of("schema", schemaPath(), "violations", violations.toString()));
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
