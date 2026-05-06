package com.ossflow.catalog.ruleset.application;

import com.ossflow.catalog.ruleset.application.port.RulesetRepositoryPort;
import com.ossflow.catalog.ruleset.domain.Ruleset;
import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import com.ossflow.shared.exception.ConflictException;
import com.ossflow.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class RulesetServiceTest {

    @Mock RulesetRepositoryPort repository;
    @InjectMocks RulesetService service;

    private Ruleset baseRuleset() {
        return Ruleset.builder()
                .federationId(1L).belt(Belt.WHITE).modality(Modality.GI)
                .effectiveFrom(LocalDate.of(2024, 1, 1)).build();
    }

    @Test
    void should_create_ruleset_when_no_conflict() {
        var input = baseRuleset();
        given(repository.existsByUniqueKey(1L, Belt.WHITE, Modality.GI, LocalDate.of(2024, 1, 1)))
                .willReturn(false);
        given(repository.save(input)).willReturn(input.toBuilder().id(1L).build());

        var result = service.create(input);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void should_throw_ConflictException_when_duplicate() {
        var input = baseRuleset();
        given(repository.existsByUniqueKey(1L, Belt.WHITE, Modality.GI, LocalDate.of(2024, 1, 1)))
                .willReturn(true);

        assertThatThrownBy(() -> service.create(input))
                .isInstanceOf(ConflictException.class)
                .hasFieldOrPropertyWithValue("errorCode", "RULESET_DUPLICATE");
    }

    @Test
    void should_throw_NotFoundException_when_findById_misses() {
        given(repository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", "RULESET_NOT_FOUND");
    }
}
