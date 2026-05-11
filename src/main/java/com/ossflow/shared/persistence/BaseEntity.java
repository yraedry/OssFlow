package com.ossflow.shared.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @Column(name = "purge_at")
    private OffsetDateTime purgeAt;

    public Instant getCreatedAtInstant() {
        return createdAt != null ? createdAt.toInstant() : null;
    }

    public Instant getUpdatedAtInstant() {
        return updatedAt != null ? updatedAt.toInstant() : null;
    }

    public Instant getDeletedAtInstant() {
        return deletedAt != null ? deletedAt.toInstant() : null;
    }

    public Instant getPurgeAtInstant() {
        return purgeAt != null ? purgeAt.toInstant() : null;
    }

    public boolean isSoftDeleted() {
        return deletedAt != null;
    }

    public void softDelete(Instant now, Duration retention) {
        this.deletedAt = now.atOffset(ZoneOffset.UTC);
        this.purgeAt = now.plus(retention).atOffset(ZoneOffset.UTC);
    }

    public void restore() {
        this.deletedAt = null;
        this.purgeAt = null;
    }
}
