package com.ossflow.coaching.gym;

import com.ossflow.coaching.gym.application.GymService;
import com.ossflow.coaching.gym.application.port.GymRepositoryPort;
import com.ossflow.coaching.gym.domain.GymLocation;
import com.ossflow.shared.exception.ConflictException;
import com.ossflow.shared.exception.ForbiddenException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GymServiceTest {

    @Mock GymRepositoryPort repo;
    @InjectMocks GymService service;

    static final long COACH_ID = 10L;
    static final long GYM_ID   = 1L;

    GymLocation sample() {
        return new GymLocation(GYM_ID, COACH_ID, "Academia Central", "Calle Mayor 1", Instant.now());
    }

    @Test
    void create_savesGym() {
        given(repo.save(any())).willReturn(sample());
        var result = service.create(COACH_ID, "Academia Central", null);
        assertThat(result.name()).isEqualTo("Academia Central");
        verify(repo).save(any(GymLocation.class));
    }

    @Test
    void list_returnsOnlyCoachGyms() {
        given(repo.findByCoachId(COACH_ID)).willReturn(List.of(sample()));
        var result = service.list(COACH_ID);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).coachId()).isEqualTo(COACH_ID);
    }

    @Test
    void update_wrongCoach_throwsForbidden() {
        given(repo.findById(GYM_ID)).willReturn(Optional.of(sample()));
        assertThatThrownBy(() -> service.update(GYM_ID, 99L, "X", null))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void delete_withPlans_throwsConflict() {
        given(repo.findById(GYM_ID)).willReturn(Optional.of(sample()));
        given(repo.countClassPlans(GYM_ID)).willReturn(2L);
        assertThatThrownBy(() -> service.delete(GYM_ID, COACH_ID))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void delete_withoutPlans_deletesOk() {
        given(repo.findById(GYM_ID)).willReturn(Optional.of(sample()));
        given(repo.countClassPlans(GYM_ID)).willReturn(0L);
        service.delete(GYM_ID, COACH_ID);
        verify(repo).deleteById(GYM_ID);
    }
}
