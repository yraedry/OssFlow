package com.ossflow.catalog.portability.infrastructure.web;

import org.springframework.security.access.prepost.PreAuthorize;
import com.fasterxml.jackson.databind.JsonNode;
import com.ossflow.catalog.portability.application.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/catalog/import")
@RequiredArgsConstructor
public class ImportController {

    private final CatalogImporter catalogImporter;
    private final SystemImporter systemImporter;
    private final RulesetImporter rulesetImporter;

    @PostMapping("/catalog")
    public ImportReport importCatalog(@RequestBody JsonNode body,
                                      @RequestParam(defaultValue = "MERGE") ImportMode mode) {
        return catalogImporter.importJson(body, mode);
    }

    @PostMapping("/system")
    public ImportReport importSystem(@RequestBody JsonNode body,
                                     @RequestParam(defaultValue = "MERGE") ImportMode mode) {
        return systemImporter.importJson(body, mode);
    }

    @PostMapping("/rulesets")
    public ImportReport importRulesets(@RequestBody JsonNode body,
                                       @RequestParam(defaultValue = "MERGE") ImportMode mode) {
        return rulesetImporter.importJson(body, mode);
    }
}
