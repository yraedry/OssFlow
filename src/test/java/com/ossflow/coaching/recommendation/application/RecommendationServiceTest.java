package com.ossflow.coaching.recommendation.application;

import com.ossflow.catalog.technique.application.port.TechniqueRepositoryPort;
import com.ossflow.catalog.technique.domain.Technique;
import com.ossflow.coaching.notification.application.CoachingNotificationService;
import com.ossflow.coaching.recommendation.application.port.RecommendationRepositoryPort;
import com.ossflow.coaching.recommendation.domain.RecommendationStatus;
import com.ossflow.coaching.recommendation.domain.TechniqueRecommendation;
import com.ossflow.coaching.recommendation.infrastructure.web.dto.CreateRecommendationRequest;
import com.ossflow.coaching.relationship.application.port.CoachAthleteRepositoryPort;
import com.ossflow.identity.profile.application.port.UserProfileRepositoryPort;
import com.ossflow.identity.profile.domain.UserProfile;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock RecommendationRepositoryPort repo;
    @Mock CoachAthleteRepositoryPort coachAthleteRepo;
    @Mock CoachingNotificationService notificationService;
    @Mock UserProfileRepositoryPort profileRepo;
    @Mock TechniqueRepositoryPort techniqueRepo;
    @InjectMocks RecommendationService service;

    static final long COACH_ID = 10L;
    static final long ATHLETE_ID = 20L;
    static final long TECHNIQUE_ID = 5L;

    private TechniqueRecommendation sampleRec() {
        return TechniqueRecommendation.builder()
                .id(1L)
                .coachId(COACH_ID)
                .athleteId(ATHLETE_ID)
                .techniqueId(TECHNIQUE_ID)
                .status(RecommendationStatus.PENDING)
                .recommendedAt(Instant.now())
                .build();
    }

    private Technique sampleTechnique() {
        return Technique.builder()
                .id(TECHNIQUE_ID)
                .name("Armbar")
                .build();
    }

    private UserProfile sampleProfile() {
        return UserProfile.builder()
                .id(1L)
                .ownerId(COACH_ID)
                .displayName("Sensei Carlos")
                .build();
    }

    @Test
    void create_withValidPair_savesAndNotifies() {
        var request = new CreateRecommendationRequest(TECHNIQUE_ID, ATHLETE_ID, "Practica esto");
        given(coachAthleteRepo.existsByCoachIdAndAthleteId(COACH_ID, ATHLETE_ID)).willReturn(true);
        given(techniqueRepo.findById(TECHNIQUE_ID, null)).willReturn(Optional.of(sampleTechnique()));
        given(profileRepo.findByOwnerId(COACH_ID)).willReturn(Optional.of(sampleProfile()));
        given(repo.save(any())).willReturn(sampleRec());

        var result = service.create(COACH_ID, request);

        assertThat(result.id()).isEqualTo(1L);
        verify(repo).save(any(TechniqueRecommendation.class));
        verify(notificationService).notifyRecommendationSent(ATHLETE_ID, "Sensei Carlos", "Armbar");
    }

    @Test
    void create_withInvalidPair_throwsForbidden() {
        var request = new CreateRecommendationRequest(TECHNIQUE_ID, ATHLETE_ID, null);
        given(coachAthleteRepo.existsByCoachIdAndAthleteId(COACH_ID, ATHLETE_ID)).willReturn(false);

        assertThatThrownBy(() -> service.create(COACH_ID, request))
                .isInstanceOf(ForbiddenException.class)
                .hasFieldOrPropertyWithValue("errorCode", "RECOMMENDATION_NOT_YOUR_ATHLETE");

        verifyNoInteractions(repo, notificationService);
    }

    @Test
    void cancel_pendingRecommendation_succeeds() {
        given(repo.updateStatusAsCoach(any(), any(), any(), any(), any())).willReturn(1);

        service.cancel(COACH_ID, 1L);

        verify(repo).updateStatusAsCoach(any(), any(), any(), any(), any());
    }

    @Test
    void cancel_alreadyResolved_throwsNotFound() {
        given(repo.updateStatusAsCoach(any(), any(), any(), any(), any())).willReturn(0);

        assertThatThrownBy(() -> service.cancel(COACH_ID, 99L))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", "RECOMMENDATION_NOT_FOUND");
    }

    @Test
    void accept_pendingRecommendation_succeeds() {
        given(repo.updateStatusAsAthlete(any(), any(), any(), any(), any())).willReturn(1);

        service.accept(ATHLETE_ID, 1L);

        verify(repo).updateStatusAsAthlete(any(), any(), any(), any(), any());
    }

    @Test
    void accept_alreadyResolved_throwsNotFound() {
        given(repo.updateStatusAsAthlete(any(), any(), any(), any(), any())).willReturn(0);

        assertThatThrownBy(() -> service.accept(ATHLETE_ID, 99L))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", "RECOMMENDATION_NOT_FOUND");
    }

    @Test
    void dismiss_pendingRecommendation_succeeds() {
        given(repo.updateStatusAsAthlete(any(), any(), any(), any(), any())).willReturn(1);

        service.dismiss(ATHLETE_ID, 1L);

        verify(repo).updateStatusAsAthlete(any(), any(), any(), any(), any());
    }
}
