package com.ossflow.coaching.observation.application;

import com.ossflow.catalog.technique.domain.TechniqueFamily;
import com.ossflow.coaching.observation.application.port.CoachObservationRepositoryPort;
import com.ossflow.coaching.observation.domain.CoachObservation;
import com.ossflow.coaching.observation.domain.LabelledBy;
import com.ossflow.coaching.observation.domain.Tone;
import com.ossflow.coaching.relationship.application.port.CoachAthleteRepositoryPort;
import com.ossflow.shared.exception.ForbiddenException;
import com.ossflow.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoachObservationServiceTest {

    @Mock CoachObservationRepositoryPort repo;
    @Mock CoachAthleteRepositoryPort coachAthleteRepo;
    @InjectMocks CoachObservationService service;

    @Test
    void create_throws_forbidden_when_not_linked() {
        when(coachAthleteRepo.existsByCoachIdAndAthleteId(1L, 2L)).thenReturn(false);
        var request = CoachObservation.builder().athleteId(2L).body("test").tone(Tone.POSITIVE).build();
        assertThatThrownBy(() -> service.create(1L, request))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void create_sets_labelled_by_manual_when_family_provided() {
        when(coachAthleteRepo.existsByCoachIdAndAthleteId(1L, 2L)).thenReturn(true);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        var request = CoachObservation.builder().athleteId(2L).body("test").tone(Tone.POSITIVE)
                .techniqueFamily(TechniqueFamily.CHOKES).build();

        CoachObservation result = service.create(1L, request);

        assertThat(result.labelledBy()).isEqualTo(LabelledBy.MANUAL);
        assertThat(result.techniqueFamily()).isEqualTo(TechniqueFamily.CHOKES);
    }

    @Test
    void create_sets_labelled_by_null_when_no_family() {
        when(coachAthleteRepo.existsByCoachIdAndAthleteId(1L, 2L)).thenReturn(true);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        var request = CoachObservation.builder().athleteId(2L).body("test").tone(Tone.NEUTRAL).build();

        CoachObservation result = service.create(1L, request);

        assertThat(result.labelledBy()).isNull();
    }

    @Test
    void list_throws_forbidden_when_not_linked() {
        when(coachAthleteRepo.existsByCoachIdAndAthleteId(1L, 2L)).thenReturn(false);
        assertThatThrownBy(() -> service.list(1L, 2L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void list_returns_observations_when_linked() {
        when(coachAthleteRepo.existsByCoachIdAndAthleteId(1L, 2L)).thenReturn(true);
        when(repo.findAllByCoachIdAndAthleteIdOrderByObservedAtDesc(1L, 2L)).thenReturn(List.of());
        assertThat(service.list(1L, 2L)).isEmpty();
    }

    @Test
    void delete_throws_not_found_when_not_owned() {
        when(repo.deleteByIdAndCoachId(99L, 1L)).thenReturn(0);
        assertThatThrownBy(() -> service.delete(1L, 99L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_removes_when_owned() {
        when(repo.deleteByIdAndCoachId(5L, 1L)).thenReturn(1);
        service.delete(1L, 5L);
        verify(repo).deleteByIdAndCoachId(5L, 1L);
    }

    @Test
    void radar_throws_forbidden_when_not_linked() {
        when(coachAthleteRepo.existsByCoachIdAndAthleteId(1L, 2L)).thenReturn(false);
        assertThatThrownBy(() -> service.radar(1L, 2L))
                .isInstanceOf(ForbiddenException.class);
    }
}
