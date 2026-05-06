package com.ossflow.portability.infrastructure.web;

import com.ossflow.catalog.portability.CatalogExporter;
import com.ossflow.identity.portability.IdentityExporter;
import com.ossflow.journal.portability.JournalExporter;
import com.ossflow.planning.portability.PlanningExporter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/export")
@RequiredArgsConstructor
public class FullExportController {

    private final CatalogExporter catalogExporter;
    private final JournalExporter journalExporter;
    private final PlanningExporter planningExporter;
    private final IdentityExporter identityExporter;

    @GetMapping(value = "/full", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> exportFull() {
        Long ownerId = 1L; // hardcoded como en el resto del proyecto
        var dump = new java.util.LinkedHashMap<String, Object>();
        dump.put("schemaVersion", "v1");
        dump.put("exportedAt", java.time.Instant.now().toString());
        dump.put("ownerId", ownerId);
        dump.put("catalog",  catalogExporter.exportFor(ownerId));
        dump.put("journal",  journalExporter.exportFor(ownerId));
        dump.put("planning", planningExporter.exportFor(ownerId));
        dump.put("identity", identityExporter.exportFor(ownerId));
        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=\"ossflow-backup-%s.json\"".formatted(java.time.LocalDate.now()))
                .body(dump);
    }
}
