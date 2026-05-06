package com.ossflow.identity.profile.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserProfileFederationId implements Serializable {

    @Column(name = "user_profile_id")
    private Long userProfileId;

    @Column(name = "federation_id")
    private Long federationId;
}
