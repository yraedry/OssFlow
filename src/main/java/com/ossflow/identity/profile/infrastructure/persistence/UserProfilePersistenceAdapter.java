package com.ossflow.identity.profile.infrastructure.persistence;

import com.ossflow.identity.profile.application.port.UserProfileRepositoryPort;
import com.ossflow.identity.profile.domain.UserProfile;
import com.ossflow.identity.profile.domain.UserProfileFederation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserProfilePersistenceAdapter implements UserProfileRepositoryPort {

    private final UserProfileJpaRepository repository;
    private final UserProfilePersistenceMapper mapper;

    @Override
    @Transactional
    public UserProfile save(UserProfile profile) {
        UserProfileEntity entity;
        if (profile.id() == null) {
            entity = mapper.toEntity(profile);
            entity.setOwnerId(profile.ownerId());
        } else {
            entity = repository.findById(profile.id())
                    .orElseGet(() -> {
                        UserProfileEntity e = mapper.toEntity(profile);
                        e.setOwnerId(profile.ownerId());
                        return e;
                    });
            entity.setDisplayName(profile.displayName());
            entity.setCurrentBelt(profile.currentBelt());
            entity.setBeltSince(profile.beltSince());
            entity.setAcademy(profile.academy());
            entity.setPreferredModality(profile.preferredModality());
            entity.setOnboardingCompleted(profile.onboardingCompleted());
        }

        // Sync federations
        entity.getFederations().clear();
        List<UserProfileFederation> domainFeds = profile.federations();
        if (domainFeds != null) {
            for (UserProfileFederation fed : domainFeds) {
                UserProfileFederationEntity fedEntity = UserProfileFederationEntity.builder()
                        .id(new UserProfileFederationId(null, fed.federationId()))
                        .userProfile(entity)
                        .isPrimary(fed.isPrimary())
                        .build();
                entity.getFederations().add(fedEntity);
            }
        }

        UserProfileEntity saved = repository.save(entity);
        // Fix federation IDs after save (now entity has a real id)
        saved.getFederations().forEach(f -> {
            if (f.getId() != null && f.getId().getUserProfileId() == null) {
                f.getId().setUserProfileId(saved.getId());
            }
        });
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<UserProfile> findByOwnerId(Long ownerId) {
        return repository.findByOwnerId(ownerId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByOwnerId(Long ownerId) {
        return repository.existsByOwnerId(ownerId);
    }
}
