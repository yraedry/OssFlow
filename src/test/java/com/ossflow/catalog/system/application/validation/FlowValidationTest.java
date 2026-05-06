package com.ossflow.catalog.system.application.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.catalog.position.application.port.PositionRepositoryPort;
import com.ossflow.catalog.position.domain.Position;
import com.ossflow.catalog.position.domain.Visibility;
import com.ossflow.catalog.system.domain.OssSystem;
import com.ossflow.catalog.technique.application.port.TechniqueRepositoryPort;
import com.ossflow.shared.json.JsonSchemaValidator;
import com.ossflow.shared.validation.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FlowValidationTest {

    @Mock PositionRepositoryPort positionRepository;
    @Mock TechniqueRepositoryPort techniqueRepository;

    ObjectMapper objectMapper = new ObjectMapper();
    FlowSchemaValidationStep schemaStep;
    FlowSemanticValidationStep semanticStep;
    FlowReferentialValidationStep referentialStep;

    private static final String VALID_FLOW = """
            {
              "nodes": [
                {"id": "n1", "kind": "POSITION", "refId": 1},
                {"id": "n2", "kind": "TECHNIQUE", "refId": 2}
              ],
              "edges": [
                {"from": "n1", "to": "n2", "trigger": "ATTACK"}
              ]
            }
            """;

    @BeforeEach
    void setUp() {
        var validator = new JsonSchemaValidator(objectMapper);
        schemaStep = new FlowSchemaValidationStep(validator, objectMapper);
        semanticStep = new FlowSemanticValidationStep();
        referentialStep = new FlowReferentialValidationStep(positionRepository, techniqueRepository);
    }

    private OssSystem systemWith(String flow) {
        return OssSystem.builder().ownerId(1L).name("Test").flowDefinition(flow)
                .flowSchemaVersion("v1").visibility(Visibility.PRIVATE).build();
    }

    @Test
    void should_pass_schema_validation_for_valid_flow() {
        var result = schemaStep.validate(systemWith(VALID_FLOW), new com.ossflow.shared.validation.ValidationContext());
        assertThat(result).isInstanceOf(ValidationResult.Ok.class);
    }

    @Test
    void should_fail_schema_validation_when_nodes_missing() {
        var result = schemaStep.validate(systemWith("{\"edges\":[]}"),
                new com.ossflow.shared.validation.ValidationContext());
        assertThat(result).isInstanceOf(ValidationResult.Fail.class);
        assertThat(((ValidationResult.Fail) result).errorCode()).isEqualTo("FLOW_SCHEMA_INVALID");
    }

    @Test
    void should_fail_semantic_when_edge_references_unknown_node() throws Exception {
        String flow = """
                {
                  "nodes": [{"id": "n1", "kind": "POSITION", "refId": 1}],
                  "edges": [{"from": "n1", "to": "UNKNOWN", "trigger": "ATTACK"}]
                }
                """;
        var ctx = new com.ossflow.shared.validation.ValidationContext();
        ctx.put("flowNode", objectMapper.readTree(flow));
        var result = semanticStep.validate(systemWith(flow), ctx);
        assertThat(result).isInstanceOf(ValidationResult.Fail.class);
        assertThat(((ValidationResult.Fail) result).errorCode()).isEqualTo("FLOW_EDGE_UNKNOWN_TO");
    }

    @Test
    void should_fail_referential_when_position_not_found() throws Exception {
        given(positionRepository.findById(1L, 1L)).willReturn(Optional.empty());

        var ctx = new com.ossflow.shared.validation.ValidationContext();
        ctx.put("flowNode", objectMapper.readTree(VALID_FLOW));
        var result = referentialStep.validate(systemWith(VALID_FLOW), ctx);
        assertThat(result).isInstanceOf(ValidationResult.Fail.class);
        assertThat(((ValidationResult.Fail) result).errorCode()).isEqualTo("FLOW_POSITION_NOT_FOUND");
    }

    @Test
    void should_pass_referential_when_all_refs_exist() throws Exception {
        given(positionRepository.findById(1L, 1L))
                .willReturn(Optional.of(Position.builder().id(1L).build()));
        given(techniqueRepository.findById(2L, 1L))
                .willReturn(Optional.of(com.ossflow.catalog.technique.domain.Technique.builder()
                        .id(2L).build()));

        var ctx = new com.ossflow.shared.validation.ValidationContext();
        ctx.put("flowNode", objectMapper.readTree(VALID_FLOW));
        var result = referentialStep.validate(systemWith(VALID_FLOW), ctx);
        assertThat(result).isInstanceOf(ValidationResult.Ok.class);
    }
}
