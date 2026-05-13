package com.ossflow.coaching.notification.application;

import com.ossflow.coaching.notification.application.port.CoachingNotificationRepositoryPort;
import com.ossflow.coaching.notification.domain.CoachingNotification;
import com.ossflow.coaching.notification.domain.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoachingNotificationService {

    private final CoachingNotificationRepositoryPort repo;

    public void notifyAthleteJoined(Long coachId, String athleteName) {
        repo.save(build(coachId, NotificationType.ATHLETE_JOINED,
                "{\"athleteName\":\"" + athleteName + "\"}"));
    }

    public void notifyAthleteLeft(Long coachId, String athleteName) {
        repo.save(build(coachId, NotificationType.ATHLETE_LEFT,
                "{\"athleteName\":\"" + athleteName + "\"}"));
    }

    public void notifyCoachRemovedYou(Long athleteId, String coachName) {
        repo.save(build(athleteId, NotificationType.COACH_REMOVED_YOU,
                "{\"coachName\":\"" + coachName + "\"}"));
    }

    public List<CoachingNotification> getUnread(Long accountId) {
        return repo.findUnreadByRecipient(accountId);
    }

    public void markAllRead(Long accountId) {
        repo.markAllReadByRecipient(accountId);
    }

    private CoachingNotification build(Long recipientId, NotificationType type, String payload) {
        return CoachingNotification.builder()
                .recipientAccountId(recipientId)
                .type(type)
                .payload(payload)
                .read(false)
                .createdAt(Instant.now())
                .build();
    }
}
