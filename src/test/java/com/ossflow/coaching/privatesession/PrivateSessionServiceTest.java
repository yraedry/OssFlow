package com.ossflow.coaching.privatesession;

import com.ossflow.coaching.privatesession.application.PrivateSessionService;
import com.ossflow.coaching.privatesession.application.port.PrivateSessionRepositoryPort;
import com.ossflow.coaching.privatesession.domain.PrivateSession;
import com.ossflow.coaching.privatesession.infrastructure.web.dto.CreatePrivateSessionRequest;
import com.ossflow.coaching.privatesession.infrastructure.web.dto.UpdatePrivateSessionRequest;
import com.ossflow.coaching.relationship.application.port.CoachAthleteRepositoryPort;
import com.ossflow.shared.exception.ForbiddenException;
import com.ossflow.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PrivateSessionServiceTest {

    @Mock PrivateSessionRepositoryPort repo;
    @Mock CoachAthleteRepositoryPort coachAthleteRepo;
    @InjectMocks PrivateSessionService service;

    static final long COACH_ID = 10L;
    static final long ATHLETE_ID = 20L;

    private PrivateSession sample() {
        return PrivateSession.builder()
                .id(1L).coachId(COACH_ID).athleteId(ATHLETE_ID)
                .sessionDate(LocalDate.of(2026, 5, 17))
                .title("Single leg X — entrada desde guard")
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void create_withValidPair_savesSession() {
        var req = new CreatePrivateSessionRequest(ATHLETE_ID, null, LocalDate.of(2026, 5, 17),
                null, null, "Single leg X", null);
        given(coachAthleteRepo.existsByCoachIdAndAthleteId(COACH_ID, ATHLETE_ID)).willReturn(true);
        given(repo.save(any())).willReturn(sample());

        var result = service.create(COACH_ID, req);

        assertThat(result.id()).isEqualTo(1L);
        verify(repo).save(any(PrivateSession.class));
    }

    @Test
    void create_withUnlinkedAthlete_throwsForbidden() {
        var req = new CreatePrivateSessionRequest(ATHLETE_ID, null, LocalDate.of(2026, 5, 17),
                null, null, "Test", null);
        given(coachAthleteRepo.existsByCoachIdAndAthleteId(COACH_ID, ATHLETE_ID)).willReturn(false);

        assertThatThrownBy(() -> service.create(COACH_ID, req))
                .isInstanceOf(ForbiddenException.class)
                .hasFieldOrPropertyWithValue("errorCode", "SESSION_NOT_YOUR_ATHLETE");
    }

    @Test
    void listForAthlete_withUnlinkedAthlete_throwsForbidden() {
        given(coachAthleteRepo.existsByCoachIdAndAthleteId(COACH_ID, ATHLETE_ID)).willReturn(false);

        assertThatThrownBy(() -> service.listByAthlete(COACH_ID, ATHLETE_ID))
                .isInstanceOf(ForbiddenException.class)
                .hasFieldOrPropertyWithValue("errorCode", "SESSION_NOT_YOUR_ATHLETE");
    }

    @Test
    void listForAthlete_withValidPair_returnsSessions() {
        given(coachAthleteRepo.existsByCoachIdAndAthleteId(COACH_ID, ATHLETE_ID)).willReturn(true);
        given(repo.findByCoachIdAndAthleteIdOrderBySessionDateDesc(COACH_ID, ATHLETE_ID))
                .willReturn(List.of(sample()));

        var result = service.listByAthlete(COACH_ID, ATHLETE_ID);

        assertThat(result).hasSize(1);
    }

    @Test
    void listAll_returnsCoachSessions() {
        given(repo.findByCoachIdOrderBySessionDateDesc(COACH_ID)).willReturn(List.of(sample()));

        var result = service.listAll(COACH_ID);

        assertThat(result).hasSize(1);
    }

    @Test
    void update_withOwnership_updatesSession() {
        var existing = sample();
        var req = new UpdatePrivateSessionRequest(null, LocalDate.of(2026, 5, 18), null, null, "Updated title", null);
        given(repo.findByIdAndCoachId(1L, COACH_ID)).willReturn(Optional.of(existing));
        given(repo.save(any())).willAnswer(inv -> inv.getArgument(0));

        var result = service.update(COACH_ID, 1L, req);

        assertThat(result.title()).isEqualTo("Updated title");
        assertThat(result.sessionDate()).isEqualTo(LocalDate.of(2026, 5, 18));
    }

    @Test
    void update_withoutOwnership_throwsNotFound() {
        given(repo.findByIdAndCoachId(99L, COACH_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(COACH_ID, 99L, new UpdatePrivateSessionRequest(
                null, null, null, null, null, null)))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", "SESSION_NOT_FOUND");
    }

    @Test
    void delete_withOwnership_deletesSession() {
        given(repo.deleteByIdAndCoachId(1L, COACH_ID)).willReturn(1);

        service.delete(COACH_ID, 1L);

        verify(repo).deleteByIdAndCoachId(1L, COACH_ID);
    }

    @Test
    void delete_withoutOwnership_throwsNotFound() {
        given(repo.deleteByIdAndCoachId(99L, COACH_ID)).willReturn(0);

        assertThatThrownBy(() -> service.delete(COACH_ID, 99L))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", "SESSION_NOT_FOUND");
    }

    @Test
    void listMine_returnsByAthleteId() {
        given(repo.findByAthleteIdOrderBySessionDateDesc(ATHLETE_ID)).willReturn(List.of(sample()));

        var result = service.listMine(ATHLETE_ID);

        assertThat(result).hasSize(1);
    }
}
