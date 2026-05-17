package com.ossflow.coaching.classplan;

import com.ossflow.coaching.classplan.application.ClassPlanService;
import com.ossflow.coaching.classplan.application.port.ClassPlanRepositoryPort;
import com.ossflow.coaching.classplan.domain.ClassPlan;
import com.ossflow.coaching.classplan.domain.ClassPlanStatus;
import com.ossflow.coaching.gym.application.port.GymRepositoryPort;
import com.ossflow.coaching.gym.domain.GymLocation;
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
class ClassPlanServiceTest {

    @Mock ClassPlanRepositoryPort repo;
    @Mock GymRepositoryPort gymRepo;
    @InjectMocks ClassPlanService service;

    static final long COACH_ID = 10L;
    static final long GYM_ID   = 1L;
    static final long PLAN_ID  = 5L;

    GymLocation gym() {
        return new GymLocation(GYM_ID, COACH_ID, "Academia", null, Instant.now());
    }

    ClassPlan plan() {
        return ClassPlan.builder()
                .id(PLAN_ID).coachId(COACH_ID).gymId(GYM_ID)
                .title("Clase lunes").status(ClassPlanStatus.DRAFT)
                .blocks(List.of()).build();
    }

    @Test
    void create_validGym_savesOk() {
        given(gymRepo.findById(GYM_ID)).willReturn(Optional.of(gym()));
        given(repo.save(any())).willReturn(plan());
        var result = service.create(COACH_ID, GYM_ID, "Clase lunes", null, null, null, null);
        assertThat(result.title()).isEqualTo("Clase lunes");
        verify(repo).save(any(ClassPlan.class));
    }

    @Test
    void create_gymNotOwned_throwsForbidden() {
        given(gymRepo.findById(GYM_ID))
                .willReturn(Optional.of(new GymLocation(GYM_ID, 99L, "Otro", null, Instant.now())));
        assertThatThrownBy(() -> service.create(COACH_ID, GYM_ID, "X", null, null, null, null))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void list_delegatesToRepo() {
        given(gymRepo.findById(GYM_ID)).willReturn(Optional.of(gym()));
        given(repo.findByCoachIdAndGymId(COACH_ID, GYM_ID)).willReturn(List.of(plan()));
        var result = service.list(COACH_ID, GYM_ID);
        assertThat(result).hasSize(1);
    }

    @Test
    void delete_wrongCoach_throwsForbidden() {
        given(repo.findById(PLAN_ID)).willReturn(Optional.of(plan()));
        assertThatThrownBy(() -> service.delete(PLAN_ID, 99L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void delete_ownedPlan_deletesOk() {
        given(repo.findById(PLAN_ID)).willReturn(Optional.of(plan()));
        service.delete(PLAN_ID, COACH_ID);
        verify(repo).deleteById(PLAN_ID);
    }
}
