package com.ossflow.identity.profile.infrastructure.persistence;

import com.ossflow.identity.profile.application.port.UserProfileRepositoryPort;
import com.ossflow.identity.profile.domain.UserProfile;
import com.ossflow.identity.profile.domain.UserProfileFederation;
import jakarta.persistence.EntityManager;
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
    private final EntityManager em;

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
            entity.setFirstName(profile.firstName());
            entity.setLastName(profile.lastName());
            entity.setAlias(profile.alias());
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

        // Clear all federations and flush so DELETEs hit the DB before INSERTs.
        // This avoids violating the partial unique index (only one is_primary=TRUE per profile).
        entity.getFederations().clear();
        em.flush();

        for (UserProfileFederation fed : incoming) {
            UserProfileFederationEntity newFed = UserProfileFederationEntity.builder()
                    .id(new UserProfileFederationId(entity.getId(), fed.federationId()))
                    .userProfile(entity)
                    .isPrimary(fed.isPrimary())
                    .build();
            entity.getFederations().add(newFed);
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

    @Override
    @Transactional(readOnly = true)
    public boolean existsByAliasAndOwnerIdNot(String alias, Long ownerId) {
        return repository.existsByAliasAndOwnerIdNot(alias, ownerId);
    }
}
