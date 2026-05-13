package com.ossflow.coaching.invitation.infrastructure.persistence;

import com.ossflow.coaching.invitation.domain.InvitationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Entity
@Table(name = "coach_invitation")
@Getter @Setter @NoArgsConstructor
public class CoachInvitationEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "coach_id", nullable = false)
    private Long coachId;
    @Column(name = "code", nullable = false, length = 6)
    private String code;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InvitationStatus status;
    @Column(name = "used_count", nullable = false)
    private int usedCount;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
