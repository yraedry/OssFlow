package com.ossflow.coaching.notification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CoachingNotificationJpaRepository extends JpaRepository<CoachingNotificationEntity, Long> {
    List<CoachingNotificationEntity> findByRecipientAccountIdAndReadFalseOrderByCreatedAtDesc(Long id);

    @Modifying
    @Query("UPDATE CoachingNotificationEntity n SET n.read = true WHERE n.recipientAccountId = :id AND n.read = false")
    void markAllReadByRecipientAccountId(@Param("id") Long id);
}
