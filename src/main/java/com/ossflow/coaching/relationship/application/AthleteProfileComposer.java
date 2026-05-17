package com.ossflow.coaching.relationship.application;

import com.ossflow.coaching.relationship.application.port.CoachAthleteRepositoryPort;
import com.ossflow.coaching.relationship.infrastructure.web.dto.AthleteSummaryResponse;
import com.ossflow.identity.injury.application.port.InjuryRepositoryPort;
import com.ossflow.identity.injury.domain.InjuryStatus;
import com.ossflow.identity.profile.application.port.UserProfileRepositoryPort;
import com.ossflow.journal.competitionlog.application.port.CompetitionLogRepositoryPort;
import com.ossflow.journal.trainingsession.application.port.TrainingSessionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AthleteProfileComposer {

    private final CoachAthleteRepositoryPort relationshipRepo;
    private final UserProfileRepositoryPort profileRepo;
    private final InjuryRepositoryPort injuryRepo;
    private final TrainingSessionRepositoryPort trainingSessionRepo;
    private final CompetitionLogRepositoryPort competitionLogRepo;

    public AthleteSummaryResponse compose(Long coachId, Long athleteId) {
        if (!relationshipRepo.existsByCoachIdAndAthleteId(coachId, athleteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No vinculado");
        }

        var profile = profileRepo.findByOwnerId(athleteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "El atleta no tiene perfil"));

        // Active injuries
        var activeInjuries = injuryRepo.findAllByOwnerId(athleteId).stream()
                .filter(i -> i.status() == InjuryStatus.ACTIVE)
                .map(i -> new AthleteSummaryResponse.ActiveInjury(
                        i.bodyPart(),
                        i.severity() != null ? i.severity().name() : null,
                        i.status().name()))
                .toList();

        // Recent competitions (up to 5, sorted by eventDate desc)
        var recentCompetitions = competitionLogRepo
                .findAll(athleteId, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "eventDate")))
                .getContent().stream()
                .map(c -> new AthleteSummaryResponse.RecentCompetition(
                        c.eventName(), c.eventDate(), c.result()))
                .toList();

        // Last training session
        var sessions = trainingSessionRepo
                .findAll(athleteId, PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "sessionDate")))
                .getContent();

        LocalDate lastSessionDate = sessions.isEmpty() ? null : sessions.get(0).sessionDate();
        long daysSinceLastSession = lastSessionDate != null
                ? ChronoUnit.DAYS.between(lastSessionDate, LocalDate.now())
                : -1L;

        // Days in current belt
        long daysInBelt = profile.beltSince() != null
                ? ChronoUnit.DAYS.between(profile.beltSince(), LocalDate.now())
                : 0L;

        return new AthleteSummaryResponse(
                athleteId,
                profile.displayName(),
                profile.currentBelt(),
                daysInBelt,
                profile.academy(),
                profile.ageCategory(),
                profile.stripes(),
                profile.weight(),
                profile.preferredModality(),
                activeInjuries,
                recentCompetitions,
                lastSessionDate,
                daysSinceLastSession
        );
    }
}
