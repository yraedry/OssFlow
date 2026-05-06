package com.ossflow.catalog.portability.application;

import java.util.List;
import java.util.Map;

public record ImportReport(
        ImportMode mode,
        int created,
        int skipped,
        List<String> warnings,
        List<String> errors,
        Map<String, List<Long>> createdEntities
) {}
