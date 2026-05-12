package com.ossflow.planning.weeklytemplate.application;

import com.ossflow.planning.weeklytemplate.application.port.WeeklyTemplateRepositoryPort;
import com.ossflow.planning.weeklytemplate.domain.DayEntry;
import com.ossflow.planning.weeklytemplate.domain.SessionSlot;
import com.ossflow.planning.weeklytemplate.domain.SessionType;
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
                        DayEntry.builder()
                                .dayOfWeek(DayOfWeek.MONDAY)
                                .sessions(List.of(
                                        SessionSlot.builder().type(SessionType.BJJ).build(),
                                        SessionSlot.builder().type(SessionType.STRENGTH).time("09:00").build()
                                ))
                                .build()
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
        assertThat(result.days().getFirst().sessions()).hasSize(2);
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

    @Test
    void should_support_multiple_sessions_per_day() {
        WeeklyTemplate template = WeeklyTemplate.builder()
                .ownerId(4L)
                .days(List.of(
                        DayEntry.builder()
                                .dayOfWeek(DayOfWeek.TUESDAY)
                                .sessions(List.of(
                                        SessionSlot.builder().type(SessionType.BJJ).time("07:00").build(),
                                        SessionSlot.builder().type(SessionType.BJJ).time("19:00").build(),
                                        SessionSlot.builder().type(SessionType.CARDIO).build()
                                ))
                                .build()
                ))
                .build();
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        WeeklyTemplate result = service.upsert(4L, template);

        assertThat(result.days().getFirst().sessions()).hasSize(3);
        assertThat(result.days().getFirst().sessions().stream()
                .filter(s -> s.type() == SessionType.BJJ).count()).isEqualTo(2);
    }
}
