package com.ossflow.identity.profile.application.port;

import com.ossflow.identity.profile.domain.UserProfile;

import java.util.Optional;

public interface UserProfileRepositoryPort {
    UserProfile save(UserProfile profile);
    Optional<UserProfile> findByOwnerId(Long ownerId);
    boolean existsByOwnerId(Long ownerId);
}
