package com.ossflow.journal.competitionlog;

import com.ossflow.coaching.relationship.application.port.CoachAthleteRepositoryPort;
import com.ossflow.journal.competitionlog.application.CompetitionLogService;
import com.ossflow.journal.competitionlog.application.port.CompetitionLogRepositoryPort;
import com.ossflow.journal.competitionlog.domain.CompetitionLog;
import com.ossflow.shared.exception.ForbiddenException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CompetitionLogServiceTest {

    @Mock CompetitionLogRepositoryPort repository;
    @Mock CoachAthleteRepositoryPort coachAthleteRepository;
    @InjectMocks CompetitionLogService service;

    @Test
    void listForCoach_throws_forbidden_when_not_linked() {
        given(coachAthleteRepository.existsByCoachIdAndAthleteId(10L, 42L)).willReturn(false);

        assertThatThrownBy(() -> service.listForCoach(10L, 42L, PageRequest.of(0, 20)))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void listForCoach_returns_page_when_linked() {
        given(coachAthleteRepository.existsByCoachIdAndAthleteId(10L, 42L)).willReturn(true);
        var expected = new PageImpl<>(List.of(CompetitionLog.builder()
                .id(1L).ownerId(42L).eventName("Copa BJJ").build()));
        given(repository.findAll(42L, PageRequest.of(0, 20))).willReturn(expected);

        Page<CompetitionLog> result = service.listForCoach(10L, 42L, PageRequest.of(0, 20));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).eventName()).isEqualTo("Copa BJJ");
    }
}
