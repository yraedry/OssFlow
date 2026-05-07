package com.ossflow.shared.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Duration;
import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "INTEGER")
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId = 1L;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "purge_at")
    private Instant purgeAt;

    public boolean isSoftDeleted() {
        return deletedAt != null;
    }

    public void softDelete(Instant now, Duration retention) {
        this.deletedAt = now;
        this.purgeAt = now.plus(retention);
    }

    public void restore() {
        this.deletedAt = null;
        this.purgeAt = null;
    }
}
