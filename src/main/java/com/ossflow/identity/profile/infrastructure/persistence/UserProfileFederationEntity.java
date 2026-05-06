package com.ossflow.identity.profile.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_profile_federation")
public class UserProfileFederationEntity {

    @EmbeddedId
    private UserProfileFederationId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userProfileId")
    @JoinColumn(name = "user_profile_id")
    private UserProfileEntity userProfile;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary;

    public Long getFederationId() {
        return id != null ? id.getFederationId() : null;
    }
}
