package com.ossflow.identity.profile.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserProfileJpaRepository extends JpaRepository<UserProfileEntity, Long> {

    @Query("SELECT up FROM UserProfileEntity up LEFT JOIN FETCH up.federations WHERE up.ownerId = :ownerId")
    Optional<UserProfileEntity> findByOwnerId(@Param("ownerId") Long ownerId);

    boolean existsByOwnerId(Long ownerId);
}
