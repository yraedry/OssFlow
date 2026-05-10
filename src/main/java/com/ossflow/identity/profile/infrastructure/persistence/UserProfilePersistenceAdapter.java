package com.ossflow.identity.profile.infrastructure.persistence;

import com.ossflow.identity.profile.application.port.UserProfileRepositoryPort;
import com.ossflow.identity.profile.domain.UserProfile;
import com.ossflow.identity.profile.domain.UserProfileFederation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

        syncFederations(entity, profile.federations());

        return mapper.toDomain(repository.save(entity));
    }

    private void syncFederations(UserProfileEntity entity, List<UserProfileFederation> domainFeds) {
        List<UserProfileFederation> incoming = domainFeds != null ? domainFeds : List.of();

        Set<Long> incomingIds = incoming.stream()
                .map(UserProfileFederation::federationId)
                .collect(Collectors.toSet());

        Map<Long, UserProfileFederationEntity> existing = entity.getFederations().stream()
                .collect(Collectors.toMap(UserProfileFederationEntity::getFederationId, f -> f));

        // Remove federations no longer in the incoming list
        entity.getFederations().removeIf(f -> !incomingIds.contains(f.getFederationId()));

        // Update or add
        for (UserProfileFederation fed : incoming) {
            UserProfileFederationEntity fedEntity = existing.get(fed.federationId());
            if (fedEntity != null) {
                fedEntity.setPrimary(fed.isPrimary());
            } else {
                UserProfileFederationEntity newFed = UserProfileFederationEntity.builder()
                        .id(new UserProfileFederationId(entity.getId(), fed.federationId()))
                        .userProfile(entity)
                        .isPrimary(fed.isPrimary())
                        .build();
                entity.getFederations().add(newFed);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserProfile> findByOwnerId(Long ownerId) {
        return repository.findByOwnerId(ownerId).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByOwnerId(Long ownerId) {
        return repository.existsByOwnerId(ownerId);
    }
}
