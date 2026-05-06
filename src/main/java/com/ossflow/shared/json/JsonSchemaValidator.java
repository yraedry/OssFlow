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
