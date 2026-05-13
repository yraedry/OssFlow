package com.ossflow.coaching.relationship.application;

import com.ossflow.coaching.relationship.application.port.CoachAthleteRepositoryPort;
import com.ossflow.coaching.relationship.domain.CoachAthleteRelationship;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoachAthleteServiceTest {

    @Mock CoachAthleteRepositoryPort repo;
    @InjectMocks CoachAthleteService service;

    @Test
    void link_throws_when_already_linked() {
        when(repo.findByCoachIdAndAthleteId(1L, 2L))
                .thenReturn(Optional.of(CoachAthleteRelationship.builder()
                        .id(1L).coachId(1L).athleteId(2L).linkedAt(Instant.now()).build()));

        assertThatThrownBy(() -> service.link(1L, 2L, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ALREADY_LINKED");
    }

    @Test
    void link_creates_relationship_when_not_exists() {
        when(repo.findByCoachIdAndAthleteId(1L, 2L)).thenReturn(Optional.empty());
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.link(1L, 2L, 5L);

        verify(repo).save(argThat(r ->
                r.coachId().equals(1L) && r.athleteId().equals(2L) && Long.valueOf(5L).equals(r.invitationId())));
    }

    @Test
    void unlinkByCoach_calls_delete() {
        service.unlinkByCoach(1L, 2L);
        verify(repo).deleteByCoachIdAndAthleteId(1L, 2L);
    }

    @Test
    void unlinkByAthlete_calls_delete_with_correct_order() {
        service.unlinkByAthlete(2L, 1L);
        verify(repo).deleteByCoachIdAndAthleteId(1L, 2L);
    }

    @Test
    void isLinked_delegates_to_repo() {
        when(repo.existsByCoachIdAndAthleteId(1L, 2L)).thenReturn(true);
        assertThat(service.isLinked(1L, 2L)).isTrue();
    }
}
