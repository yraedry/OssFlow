package com.ossflow.identity.auth.infrastructure.persistence;

import com.ossflow.identity.auth.domain.AccountProvider;
import com.ossflow.identity.auth.domain.AccountRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "account")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "password_hash", length = 72)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20)
    private AccountProvider provider;

    @Column(name = "provider_id", length = 255)
    private String providerId;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "token_version", nullable = false)
    private int tokenVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private AccountRole role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
