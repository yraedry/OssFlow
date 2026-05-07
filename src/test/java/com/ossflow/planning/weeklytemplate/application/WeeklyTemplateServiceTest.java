package com.ossflow.planning.weeklytemplate.application;

import com.ossflow.planning.weeklytemplate.application.port.WeeklyTemplateRepositoryPort;
import com.ossflow.planning.weeklytemplate.domain.DayEntry;
import com.ossflow.planning.weeklytemplate.domain.WeeklyTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeeklyTemplateServiceTest {

    @Mock
    WeeklyTemplateRepositoryPort repository;

    @InjectMocks
    WeeklyTemplateService service;

    private WeeklyTemplate buildTemplate(Long ownerId) {
        return WeeklyTemplate.builder()
                .ownerId(ownerId)
                .days(List.of(
                        DayEntry.builder().dayOfWeek(DayOfWeek.MONDAY).bjj(true).strength(true).cardio(true).build()
                ))
                .build();
    }

    @Test
    void should_return_saved_template_on_upsert() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WeeklyTemplate result = service.upsert(1L, buildTemplate(1L));

        verify(repository).save(any());
        assertThat(result.ownerId()).isEqualTo(1L);
        assertThat(result.days()).hasSize(1);
    }

    @Test
    void should_return_empty_template_when_not_found() {
        when(repository.findByOwnerId(2L)).thenReturn(Optional.empty());

        WeeklyTemplate result = service.getOrEmpty(2L);

        assertThat(result.ownerId()).isEqualTo(2L);
        assertThat(result.days()).isEmpty();
    }

    @Test
    void should_return_stored_template_when_exists() {
        WeeklyTemplate stored = buildTemplate(3L).toBuilder().id(10L).build();
        when(repository.findByOwnerId(3L)).thenReturn(Optional.of(stored));

        WeeklyTemplate result = service.getOrEmpty(3L);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.ownerId()).isEqualTo(3L);
        assertThat(result.days()).hasSize(1);
    }
}
