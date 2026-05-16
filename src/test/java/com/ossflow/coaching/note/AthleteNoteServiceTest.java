package com.ossflow.coaching.note;

import com.ossflow.coaching.note.application.AthleteNoteService;
import com.ossflow.coaching.note.application.port.AthleteNoteRepositoryPort;
import com.ossflow.coaching.note.domain.AthleteNote;
import com.ossflow.coaching.note.infrastructure.web.dto.CreateNoteRequest;
import com.ossflow.coaching.notification.application.CoachingNotificationService;
import com.ossflow.coaching.relationship.application.port.CoachAthleteRepositoryPort;
import com.ossflow.shared.exception.ForbiddenException;
import com.ossflow.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AthleteNoteServiceTest {

    @Mock AthleteNoteRepositoryPort repo;
    @Mock CoachAthleteRepositoryPort coachAthleteRepo;
    @Mock CoachingNotificationService notificationService;
    @InjectMocks AthleteNoteService service;

    private AthleteNote sampleNote() {
        return AthleteNote.builder()
                .id(1L)
                .coachId(10L)
                .athleteId(20L)
                .body("Great guard work")
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void create_withValidPair_savesAndNotifies() {
        var request = new CreateNoteRequest(20L, "Great guard work", null);
        given(coachAthleteRepo.existsByCoachIdAndAthleteId(10L, 20L)).willReturn(true);
        given(repo.save(any())).willReturn(sampleNote());

        var result = service.create(10L, request);

        assertThat(result.id()).isEqualTo(1L);
        verify(repo).save(any(AthleteNote.class));
        verify(notificationService).notifyNoteSent(eq(20L), eq(10L));
    }

    @Test
    void create_withInvalidPair_throwsForbidden() {
        var request = new CreateNoteRequest(20L, "Some body", null);
        given(coachAthleteRepo.existsByCoachIdAndAthleteId(10L, 20L)).willReturn(false);

        assertThatThrownBy(() -> service.create(10L, request))
                .isInstanceOf(ForbiddenException.class)
                .hasFieldOrPropertyWithValue("errorCode", "NOTE_NOT_YOUR_ATHLETE");
    }

    @Test
    void softDelete_withOwnership_works() {
        given(repo.softDeleteByIdAndCoachId(1L, 10L)).willReturn(1);

        service.softDelete(10L, 1L);

        verify(repo).softDeleteByIdAndCoachId(1L, 10L);
    }

    @Test
    void softDelete_withoutOwnership_throwsNotFound() {
        given(repo.softDeleteByIdAndCoachId(99L, 10L)).willReturn(0);

        assertThatThrownBy(() -> service.softDelete(10L, 99L))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", "NOTE_NOT_FOUND");
    }

    @Test
    void getReceivedDetail_withWrongAthlete_throwsNotFound() {
        given(repo.findByIdAndAthleteId(1L, 99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.getReceivedDetail(99L, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", "NOTE_NOT_FOUND");
    }
}
