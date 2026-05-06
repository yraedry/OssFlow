package com.ossflow.shared.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionHierarchyTest {

    @Test
    void should_have_correct_status_and_fields_when_BadRequestException_created() {
        var ex = new BadRequestException("BAD_CODE", "mensaje de error", Map.of("key", "value"));

        assertThat(ex.getErrorCode()).isEqualTo("BAD_CODE");
        assertThat(ex.getMessage()).isEqualTo("mensaje de error");
        assertThat(ex.getDetails()).containsEntry("key", "value");
        assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void should_have_empty_details_when_BadRequestException_created_without_details() {
        var ex = new BadRequestException("BAD_CODE", "mensaje");

        assertThat(ex.getDetails()).isEmpty();
        assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void should_have_correct_status_when_ReferenceInUseException_created() {
        var ex = new ReferenceInUseException("REF_IN_USE", "referencia en uso", Map.of("entityId", 42L));

        assertThat(ex.getErrorCode()).isEqualTo("REF_IN_USE");
        assertThat(ex.getMessage()).isEqualTo("referencia en uso");
        assertThat(ex.getDetails()).containsEntry("entityId", 42L);
        assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex).isInstanceOf(ConflictException.class);
    }

    @Test
    void should_have_correct_status_when_ReferentialIntegrityException_created() {
        var ex = new ReferentialIntegrityException("REF_INTEGRITY", "integridad referencial", Map.of("table", "positions"));

        assertThat(ex.getErrorCode()).isEqualTo("REF_INTEGRITY");
        assertThat(ex.getMessage()).isEqualTo("integridad referencial");
        assertThat(ex.getDetails()).containsEntry("table", "positions");
        assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(ex).isInstanceOf(UnprocessableException.class);
    }

    @Test
    void should_have_correct_status_when_SemanticValidationException_created() {
        var ex = new SemanticValidationException("SEMANTIC_ERR", "error semántico", Map.of("field", "startDate"));

        assertThat(ex.getErrorCode()).isEqualTo("SEMANTIC_ERR");
        assertThat(ex.getMessage()).isEqualTo("error semántico");
        assertThat(ex.getDetails()).containsEntry("field", "startDate");
        assertThat(ex.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(ex).isInstanceOf(UnprocessableException.class);
    }

    @Test
    void should_return_immutable_details_when_exception_created() {
        var mutableMap = new java.util.HashMap<String, Object>();
        mutableMap.put("k", "v");
        var ex = new BadRequestException("CODE", "msg", mutableMap);
        mutableMap.put("other", "should-not-appear");

        assertThat(ex.getDetails()).hasSize(1).containsEntry("k", "v");
    }
}
