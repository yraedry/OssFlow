package com.ossflow.integration;

import com.ossflow.catalog.position.domain.Position;
import com.ossflow.catalog.position.domain.PositionType;
import com.ossflow.catalog.position.domain.Visibility;
import com.ossflow.catalog.position.infrastructure.persistence.PositionPersistenceAdapter;
import com.ossflow.shared.persistence.SoftDeletePurgeJob;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SoftDeletePurgeIntegrationTest {

    @Autowired SoftDeletePurgeJob job;
    @Autowired PositionPersistenceAdapter positions;
    @PersistenceContext EntityManager em;

    @Test
    void should_purge_records_past_purge_at() {
        // 1. Crear una Position
        Position created = positions.save(
                Position.builder()
                        .ownerId(1L)
                        .name("Posicion Purgable Test")
                        .type(PositionType.BOTTOM)
                        .visibility(Visibility.PRIVATE)
                        .build()
        );
        Long id = created.id();

        // 2. Soft-delete
        positions.softDelete(id, 1L);
        em.flush();

        // 3. Forzar purge_at al pasado
        em.createNativeQuery(
                "UPDATE position SET purge_at = ?1 WHERE id = ?2")
                .setParameter(1, java.time.Instant.now().minusSeconds(3600))
                .setParameter(2, id)
                .executeUpdate();
        em.flush();

        // Verificar que la fila existe antes de la purga
        Number countBefore = (Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM position WHERE id = ?1")
                .setParameter(1, id)
                .getSingleResult();
        assertThat(countBefore.longValue()).isEqualTo(1L);

        // 4. Ejecutar el job
        job.purgeExpired();
        em.flush();
        em.clear();

        // 5. Verificar que ya no existe
        Number countAfter = (Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM position WHERE id = ?1")
                .setParameter(1, id)
                .getSingleResult();
        assertThat(countAfter.longValue()).isZero();
    }
}
