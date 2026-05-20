package com.ossflow.coaching.notification.infrastructure.persistence;

import com.ossflow.coaching.notification.domain.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Entity
@Table(name = "coaching_notification")
@Getter @Setter @NoArgsConstructor
public class CoachingNotificationEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "recipient_account_id", nullable = false)
    private Long recipientAccountId;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 40)
    private NotificationType type;
    @Column(name = "payload")
    private String payload;
    @Column(name = "read", nullable = false)
    private boolean read;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
