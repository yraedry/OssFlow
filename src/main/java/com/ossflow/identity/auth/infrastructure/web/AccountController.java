package com.ossflow.identity.auth.infrastructure.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.ossflow.catalog.portability.application.CatalogImporter;
import com.ossflow.catalog.portability.application.ImportMode;
import com.ossflow.catalog.portability.application.ImportReport;
import com.ossflow.identity.auth.application.AccountDeletionService;
import com.ossflow.shared.web.CurrentOwner;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class AccountController {

    private final AccountDeletionService accountDeletionService;
    private final CatalogImporter catalogImporter;
    private final CurrentOwner currentOwner;

    @DeleteMapping("/account")
    public ResponseEntity<Void> deleteAccount() {
        accountDeletionService.deleteAccount(currentOwner.id());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importBackup(@RequestBody JsonNode body,
                                                            @RequestParam(defaultValue = "MERGE") ImportMode mode) {
        JsonNode catalogNode = body.path("catalog");
        ImportReport report = catalogImporter.importJson(catalogNode, mode);
        return ResponseEntity.ok(Map.of(
                "catalog", Map.of(
                        "created", report.created(),
                        "skipped", report.skipped(),
                        "warnings", report.warnings(),
                        "errors", report.errors()
                ),
                "importedAt", java.time.Instant.now().toString()
        ));
    }
}
