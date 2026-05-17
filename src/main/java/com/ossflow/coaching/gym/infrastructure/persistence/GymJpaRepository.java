package com.ossflow.coaching.gym.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GymJpaRepository extends JpaRepository<GymLocationEntity, Long> {

    List<GymLocationEntity> findByCoachId(Long coachId);

    @Modifying
    @Query("UPDATE GymLocationEntity g SET g.name = :name, g.address = :address WHERE g.id = :id AND g.coachId = :coachId")
    int updateNameAndAddress(@Param("id") Long id, @Param("coachId") Long coachId, @Param("name") String name, @Param("address") String address);

    @Query(value = "SELECT COUNT(*) FROM class_plan WHERE gym_id = :gymId", nativeQuery = true)
    long countClassPlansByGymId(@Param("gymId") Long gymId);
}
