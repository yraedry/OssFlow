package com.ossflow.coaching.notification.application.port;

import com.ossflow.coaching.notification.domain.CoachingNotification;
import java.util.List;

public interface CoachingNotificationRepositoryPort {
    CoachingNotification save(CoachingNotification notification);
    List<CoachingNotification> findUnreadByRecipient(Long recipientAccountId);
    List<CoachingNotification> findRecentByRecipient(Long recipientAccountId, int limit);
    void markAllReadByRecipient(Long recipientAccountId);
}
