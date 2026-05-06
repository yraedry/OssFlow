package com.ossflow.planning.studyplan.application;

import com.ossflow.planning.studyplan.application.port.StudyPlanRepositoryPort;
import com.ossflow.planning.studyplan.domain.StudyPlan;
import com.ossflow.planning.studyplan.domain.StudyPlanStatus;
import com.ossflow.shared.exception.DuplicateNameException;
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
class StudyPlanServiceTest {

    @Mock StudyPlanRepositoryPort repository;
    @InjectMocks StudyPlanService service;

    private StudyPlan buildPlan(Long id, String title) {
        return StudyPlan.builder()
                .id(id)
                .ownerId(1L)
                .title(title)
                .goalMarkdown("Mejorar guardia cerrada")
                .startDate(LocalDate.of(2026, 5, 1))
                .endDate(LocalDate.of(2026, 8, 1))
                .status(StudyPlanStatus.ACTIVE)
                .build();
    }

    @Test
    void should_return_saved_plan_when_title_is_unique() {
        var input = buildPlan(null, "Plan Guardia");
        var saved = buildPlan(30L, "Plan Guardia");
        given(repository.existsByOwnerIdAndTitle(1L, "Plan Guardia")).willReturn(false);
        given(repository.save(input)).willReturn(saved);

        var result = service.create(input);

        assertThat(result.id()).isEqualTo(30L);
        assertThat(result.title()).isEqualTo("Plan Guardia");
    }

    @Test
    void should_throw_DuplicateNameException_when_title_already_exists() {
        var input = buildPlan(null, "Plan Existente");
        given(repository.existsByOwnerIdAndTitle(1L, "Plan Existente")).willReturn(true);

        assertThatThrownBy(() -> service.create(input))
                .isInstanceOf(DuplicateNameException.class)
                .hasFieldOrPropertyWithValue("errorCode", "STUDY_PLAN_TITLE_DUPLICATE");
    }

    @Test
    void should_return_plan_when_findById_exists() {
        var plan = buildPlan(10L, "Mi Plan");
        given(repository.findById(10L, 1L)).willReturn(Optional.of(plan));

        var result = service.findById(10L, 1L);

        assertThat(result.id()).isEqualTo(10L);
    }

    @Test
    void should_throw_NotFoundException_when_findById_not_found() {
        given(repository.findById(99L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", "STUDY_PLAN_NOT_FOUND");
    }

    @Test
    void should_return_page_when_list_called() {
        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(List.of(buildPlan(1L, "Plan A")), pageable, 1);
        given(repository.findAll(1L, pageable)).willReturn(page);

        var result = service.list(1L, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void should_soft_delete_plan_when_softDelete_called() {
        service.softDelete(5L, 1L);

        verify(repository).softDelete(5L, 1L);
    }

    @Test
    void should_throw_DuplicateNameException_when_replace_uses_existing_title() {
        var existing = buildPlan(10L, "Titulo Original");
        var replacement = buildPlan(null, "Titulo Duplicado");
        given(repository.findById(10L, 1L)).willReturn(Optional.of(existing));
        given(repository.existsByOwnerIdAndTitle(1L, "Titulo Duplicado")).willReturn(true);

        assertThatThrownBy(() -> service.replace(10L, 1L, replacement))
                .isInstanceOf(DuplicateNameException.class)
                .hasFieldOrPropertyWithValue("errorCode", "STUDY_PLAN_TITLE_DUPLICATE");
    }
}
