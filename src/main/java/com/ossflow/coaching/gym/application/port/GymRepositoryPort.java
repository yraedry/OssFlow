package com.ossflow.coaching.gym.application.port;
import com.ossflow.coaching.gym.domain.GymLocation;
import java.util.List;
import java.util.Optional;
public interface GymRepositoryPort {
    GymLocation save(GymLocation gym);
    Optional<GymLocation> findById(Long id);
    List<GymLocation> findByCoachId(Long coachId);
    int update(Long id, Long coachId, String name, String address);
    void deleteById(Long id);
    long countClassPlans(Long gymId);
}
