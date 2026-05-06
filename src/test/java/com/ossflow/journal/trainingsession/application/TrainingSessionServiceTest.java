package com.ossflow.journal.trainingsession.application;

import com.ossflow.journal.trainingsession.application.port.TrainingSessionRepositoryPort;
import com.ossflow.journal.trainingsession.domain.Intensity;
import com.ossflow.journal.trainingsession.domain.TrainingSession;
import com.ossflow.journal.trainingsession.domain.WorkedTechnique;
import com.ossflow.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainingSessionServiceTest {

    @Mock TrainingSessionRepositoryPort repository;
    @InjectMocks TrainingSessionService service;

    private TrainingSession buildSession(Long id) {
        return TrainingSession.builder()
                .id(id)
                .ownerId(1L)
                .sessionDate(LocalDate.of(2026, 5, 1))
                .durationMinutes(90)
                .intensity(Intensity.MODERATE)
                .workedTechniques(List.of())
                .build();
    }

    @Test
    void should_return_saved_session_when_create_called() {
        var input = buildSession(null);
        var saved = buildSession(20L);
        given(repository.save(input)).willReturn(saved);

        var result = service.create(input);

        assertThat(result.id()).isEqualTo(20L);
        assertThat(result.durationMinutes()).isEqualTo(90);
    }

    @Test
    void should_return_session_when_findById_exists() {
        var session = buildSession(15L);
        given(repository.findById(15L, 1L)).willReturn(Optional.of(session));

        var result = service.findById(15L, 1L);

        assertThat(result.id()).isEqualTo(15L);
    }

    @Test
    void should_throw_NotFoundException_when_findById_not_found() {
        given(repository.findById(99L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", "TRAINING_SESSION_NOT_FOUND");
    }

    @Test
    void should_return_page_when_list_called() {
        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(List.of(buildSession(1L)), pageable, 1);
        given(repository.findAll(1L, pageable)).willReturn(page);

        var result = service.list(1L, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void should_soft_delete_session_when_softDelete_called() {
        service.softDelete(3L, 1L);

        verify(repository).softDelete(3L, 1L);
    }

    @Test
    void should_return_updated_worked_technique_when_upsertWorkedTechnique_called() {
        var wt = WorkedTechnique.builder().trainingSessionId(5L).techniqueId(10L).repCount(20).build();
        given(repository.upsertWorkedTechnique(5L, 1L, wt)).willReturn(wt);

        var result = service.upsertWorkedTechnique(5L, 1L, wt);

        assertThat(result.techniqueId()).isEqualTo(10L);
        assertThat(result.repCount()).isEqualTo(20);
    }

    @Test
    void should_delegate_remove_worked_technique_when_removeWorkedTechnique_called() {
        service.removeWorkedTechnique(5L, 1L, 10L);

        verify(repository).removeWorkedTechnique(5L, 1L, 10L);
    }
}
