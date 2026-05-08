package com.ossflow.identity.injury.infrastructure.persistence;

import com.ossflow.identity.injury.domain.InjurySeverity;
import com.ossflow.identity.injury.domain.InjuryStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "injury")
@EntityListeners(AuditingEntityListener.class)
public class InjuryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "INTEGER")
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "body_part", nullable = false, length = 100)
    private String bodyPart;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private InjurySeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InjuryStatus status;

    @Column(name = "started_on", length = 20, columnDefinition = "date")
    @JdbcTypeCode(SqlTypes.DATE)
    private LocalDate startedOn;

    @Column(name = "recovered_on", length = 20, columnDefinition = "date")
    @JdbcTypeCode(SqlTypes.DATE)
    private LocalDate recoveredOn;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;
}
