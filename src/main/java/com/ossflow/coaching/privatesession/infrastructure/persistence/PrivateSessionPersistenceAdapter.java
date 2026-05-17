package com.ossflow.coaching.privatesession.infrastructure.persistence;

import com.ossflow.coaching.privatesession.application.port.PrivateSessionRepositoryPort;
import com.ossflow.coaching.privatesession.domain.PrivateSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PrivateSessionPersistenceAdapter implements PrivateSessionRepositoryPort {

    private final PrivateSessionJpaRepository jpa;
    private final PrivateSessionMapper mapper;

    @Override
    public PrivateSession save(PrivateSession session) {
        return mapper.toDomain(jpa.save(mapper.toEntity(session)));
    }

    @Override
    public Optional<PrivateSession> findByIdAndCoachId(Long id, Long coachId) {
        return jpa.findByIdAndCoachId(id, coachId).map(mapper::toDomain);
    }

    @Override
    public List<PrivateSession> findByCoachIdAndAthleteIdOrderBySessionDateDesc(Long coachId, Long athleteId) {
        return jpa.findByCoachIdAndAthleteIdOrderBySessionDateDesc(coachId, athleteId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<PrivateSession> findByCoachIdOrderBySessionDateDesc(Long coachId) {
        return jpa.findByCoachIdOrderBySessionDateDesc(coachId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<PrivateSession> findByAthleteIdOrderBySessionDateDesc(Long athleteId) {
        return jpa.findByAthleteIdOrderBySessionDateDesc(athleteId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public int deleteByIdAndCoachId(Long id, Long coachId) {
        return jpa.deleteByIdAndCoachId(id, coachId);
    }
}
