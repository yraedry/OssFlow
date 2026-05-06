package com.ossflow.shared.persistence;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class BaseEntityTest {

    static class DummyEntity extends BaseEntity {}

    @Test
    void should_be_not_soft_deleted_by_default() {
        var entity = new DummyEntity();
        assertThat(entity.isSoftDeleted()).isFalse();
    }

    @Test
    void should_set_deleted_at_and_purge_at_on_soft_delete() {
        var entity = new DummyEntity();
        var now = Instant.parse("2026-05-06T10:00:00Z");

        entity.softDelete(now, Duration.ofDays(30));

        assertThat(entity.isSoftDeleted()).isTrue();
        assertThat(entity.getDeletedAt()).isEqualTo(now);
        assertThat(entity.getPurgeAt()).isEqualTo(now.plus(Duration.ofDays(30)));
    }

    @Test
    void should_clear_timestamps_on_restore() {
        var entity = new DummyEntity();
        entity.softDelete(Instant.now(), Duration.ofDays(30));

        entity.restore();

        assertThat(entity.isSoftDeleted()).isFalse();
        assertThat(entity.getDeletedAt()).isNull();
        assertThat(entity.getPurgeAt()).isNull();
    }
}
