package com.ossflow.coaching.gym.application;

import com.ossflow.coaching.gym.application.port.GymRepositoryPort;
import com.ossflow.coaching.gym.domain.GymLocation;
import com.ossflow.shared.exception.ConflictException;
import com.ossflow.shared.exception.ForbiddenException;
import com.ossflow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GymService {
    private final GymRepositoryPort repo;

    @Transactional
    public GymLocation create(Long coachId, String name, String address) {
        return repo.save(new GymLocation(null, coachId, name, address, null));
    }

    public List<GymLocation> list(Long coachId) {
        return repo.findByCoachId(coachId);
    }

    @Transactional
    public GymLocation update(Long gymId, Long coachId, String name, String address) {
        var gym = requireOwned(gymId, coachId);
        int rows = repo.update(gymId, coachId, name, address);
        if (rows == 0) throw new NotFoundException("GYM_NOT_FOUND", "Gym not found");
        return new GymLocation(gym.id(), gym.coachId(), name, address, gym.createdAt());
    }

    @Transactional
    public void delete(Long gymId, Long coachId) {
        requireOwned(gymId, coachId);
        long plans = repo.countClassPlans(gymId);
        if (plans > 0) throw new ConflictException("GYM_HAS_PLANS", "Cannot delete gym with existing class plans");
        repo.deleteById(gymId);
    }

    private GymLocation requireOwned(Long gymId, Long coachId) {
        return repo.findById(gymId)
                .filter(g -> g.coachId().equals(coachId))
                .orElseThrow(() -> new ForbiddenException("GYM_ACCESS_DENIED", "Not your gym"));
    }
}
