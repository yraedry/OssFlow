package com.ossflow.coaching.privatesession.application.port;

import com.ossflow.coaching.privatesession.domain.PrivateSession;

import java.util.List;
import java.util.Optional;

public interface PrivateSessionRepositoryPort {
    PrivateSession save(PrivateSession session);
    Optional<PrivateSession> findByIdAndCoachId(Long id, Long coachId);
    List<PrivateSession> findByCoachIdAndAthleteIdOrderBySessionDateDesc(Long coachId, Long athleteId);
    List<PrivateSession> findByCoachIdOrderBySessionDateDesc(Long coachId);
    List<PrivateSession> findByAthleteIdOrderBySessionDateDesc(Long athleteId);
    int deleteByIdAndCoachId(Long id, Long coachId);
}
