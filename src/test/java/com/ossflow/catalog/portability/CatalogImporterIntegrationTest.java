package com.ossflow.catalog.portability;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.catalog.portability.application.CatalogImporter;
import com.ossflow.catalog.portability.application.ImportMode;
import com.ossflow.catalog.portability.application.ImportReport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CatalogImporterIntegrationTest {

    @Autowired CatalogImporter catalogImporter;
    @Autowired ObjectMapper objectMapper;

    private static final String VALID_CATALOG = """
            {
              "positions": [
                {"name": "Guardia Cerrada Import", "type": "BOTTOM", "visibility": "PRIVATE"},
                {"name": "Monte Import", "type": "TOP", "visibility": "PRIVATE"}
              ],
              "techniques": [
                {
                  "name": "Armbar Import",
                  "category": "SUBMISSION",
                  "minimumBelt": "WHITE",
                  "modality": "GI",
                  "startPositionName": "Guardia Cerrada Import",
                  "visibility": "PRIVATE"
                }
              ]
            }
            """;

    @Test
    void should_import_positions_and_techniques_in_merge_mode() throws Exception {
        var node = objectMapper.readTree(VALID_CATALOG);
        ImportReport report = catalogImporter.importJson(node, ImportMode.MERGE);

        assertThat(report.created()).isEqualTo(3);
        assertThat(report.skipped()).isEqualTo(0);
        assertThat(report.errors()).isEmpty();
        assertThat(report.createdEntities().get("positions")).hasSize(2);
        assertThat(report.createdEntities().get("techniques")).hasSize(1);
    }

    @Test
    void should_skip_duplicate_on_second_import() throws Exception {
        var node = objectMapper.readTree(VALID_CATALOG);
        catalogImporter.importJson(node, ImportMode.MERGE);
        ImportReport second = catalogImporter.importJson(node, ImportMode.MERGE);

        assertThat(second.skipped()).isGreaterThan(0);
        assertThat(second.warnings()).isNotEmpty();
    }

    @Test
    void should_fail_schema_when_positions_missing() throws Exception {
        var node = objectMapper.readTree("{\"techniques\":[]}");
        org.junit.jupiter.api.Assertions.assertThrows(
                com.ossflow.shared.exception.JsonSchemaViolationException.class,
                () -> catalogImporter.importJson(node, ImportMode.MERGE));
    }
}
