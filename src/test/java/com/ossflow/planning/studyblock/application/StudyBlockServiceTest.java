package com.ossflow.planning.studyblock.application;

import com.ossflow.planning.studyblock.application.port.StudyBlockRepositoryPort;
import com.ossflow.planning.studyblock.domain.StudyBlock;
import com.ossflow.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StudyBlockServiceTest {

    @Mock StudyBlockRepositoryPort repository;
    @InjectMocks StudyBlockService service;

    private StudyBlock buildBlock(Long id) {
        return StudyBlock.builder()
                .id(id)
                .studyPlanId(100L)
                .title("Bloque Guardia")
                .startDate(LocalDate.of(2026, 5, 1))
                .endDate(LocalDate.of(2026, 5, 31))
                .blockOrder(1)
                .notesMarkdown("Notas del bloque")
                .build();
    }

    @Test
    void should_return_saved_block_when_create_called() {
        var input = buildBlock(null);
        var saved = buildBlock(50L);
        given(repository.save(input)).willReturn(saved);

        var result = service.create(input);

        assertThat(result.id()).isEqualTo(50L);
        assertThat(result.title()).isEqualTo("Bloque Guardia");
    }

    @Test
    void should_return_block_when_findById_exists() {
        var block = buildBlock(25L);
        given(repository.findById(25L)).willReturn(Optional.of(block));

        var result = service.findById(25L);

        assertThat(result.id()).isEqualTo(25L);
    }

    @Test
    void should_throw_NotFoundException_when_findById_not_found() {
        given(repository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", "STUDY_BLOCK_NOT_FOUND");
    }

    @Test
    void should_return_blocks_when_listByPlan_called() {
        given(repository.findByStudyPlanId(100L)).willReturn(List.of(buildBlock(1L), buildBlock(2L)));

        var result = service.listByPlan(100L);

        assertThat(result).hasSize(2);
    }

    @Test
    void should_delete_block_when_delete_called_and_block_exists() {
        given(repository.findById(10L)).willReturn(Optional.of(buildBlock(10L)));

        service.delete(10L);

        verify(repository).deleteById(10L);
    }

    @Test
    void should_throw_NotFoundException_when_delete_called_and_block_not_found() {
        given(repository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", "STUDY_BLOCK_NOT_FOUND");
    }

    @Test
    void should_preserve_plan_id_and_created_at_when_update_called() {
        var existing = buildBlock(10L);
        var updated = buildBlock(null).toBuilder().title("Nuevo titulo").build();
        given(repository.findById(10L)).willReturn(Optional.of(existing));
        var savedBlock = updated.toBuilder().id(10L).studyPlanId(100L).build();
        given(repository.save(org.mockito.ArgumentMatchers.any())).willReturn(savedBlock);

        var result = service.update(10L, updated);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.studyPlanId()).isEqualTo(100L);
    }
}
