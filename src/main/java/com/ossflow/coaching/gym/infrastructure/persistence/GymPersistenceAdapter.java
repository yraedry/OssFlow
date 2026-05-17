package com.ossflow.coaching.gym.infrastructure.persistence;

import com.ossflow.coaching.gym.application.port.GymRepositoryPort;
import com.ossflow.coaching.gym.domain.GymLocation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GymPersistenceAdapter implements GymRepositoryPort {

    private final GymJpaRepository jpa;

    @Override
    public GymLocation save(GymLocation gym) {
        var entity = GymLocationEntity.builder()
                .id(gym.id())
                .coachId(gym.coachId())
                .name(gym.name())
                .address(gym.address())
                .build();
        var saved = jpa.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<GymLocation> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<GymLocation> findByCoachId(Long coachId) {
        return jpa.findByCoachId(coachId).stream().map(this::toDomain).toList();
    }

    @Override
    public int update(Long id, Long coachId, String name, String address) {
        return jpa.updateNameAndAddress(id, coachId, name, address);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public long countClassPlans(Long gymId) {
        return jpa.countClassPlansByGymId(gymId);
    }

    private GymLocation toDomain(GymLocationEntity e) {
        return new GymLocation(e.getId(), e.getCoachId(), e.getName(), e.getAddress(), e.getCreatedAt());
    }
}
