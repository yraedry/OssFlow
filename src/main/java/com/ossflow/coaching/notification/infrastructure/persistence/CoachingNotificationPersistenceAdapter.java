package com.ossflow.coaching.notification.infrastructure.persistence;

import com.ossflow.coaching.notification.application.port.CoachingNotificationRepositoryPort;
import com.ossflow.coaching.notification.domain.CoachingNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CoachingNotificationPersistenceAdapter implements CoachingNotificationRepositoryPort {
    private final CoachingNotificationJpaRepository jpa;
    private final CoachingNotificationPersistenceMapper mapper;

    @Override public CoachingNotification save(CoachingNotification n) {
        return mapper.toDomain(jpa.save(mapper.toEntity(n)));
    }
    @Override public List<CoachingNotification> findUnreadByRecipient(Long id) {
        return jpa.findByRecipientAccountIdAndReadFalseOrderByCreatedAtDesc(id)
                .stream().map(mapper::toDomain).toList();
    }
    @Override public List<CoachingNotification> findRecentByRecipient(Long id, int limit) {
        return jpa.findByRecipientAccountIdOrderByCreatedAtDesc(id, PageRequest.of(0, limit))
                .stream().map(mapper::toDomain).toList();
    }
    @Override @Transactional public void markAllReadByRecipient(Long id) {
        jpa.markAllReadByRecipientAccountId(id);
    }
}
