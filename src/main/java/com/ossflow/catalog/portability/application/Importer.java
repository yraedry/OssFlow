package com.ossflow.catalog.portability.application;

import com.fasterxml.jackson.databind.JsonNode;

public interface Importer<P> {
    String schemaPath();
    Class<P> payloadType();
    ImportReport importJson(JsonNode raw, ImportMode mode);
    ImportReport runImport(P payload, ImportMode mode);
}
