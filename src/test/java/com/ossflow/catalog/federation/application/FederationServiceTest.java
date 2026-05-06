package com.ossflow.catalog.federation.application;

import com.ossflow.catalog.federation.application.port.FederationRepositoryPort;
import com.ossflow.catalog.federation.domain.Federation;
import com.ossflow.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FederationServiceTest {

    @Mock FederationRepositoryPort repository;
    @InjectMocks FederationService service;

    @Test
    void should_return_all_federations_when_list_called() {
        var f1 = Federation.builder().id(1L).code("IBJJF").name("International BJJ Federation").build();
        var f2 = Federation.builder().id(2L).code("ADCC").name("ADCC Submission Wrestling").build();
        given(repository.findAll()).willReturn(List.of(f1, f2));

        var result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Federation::code).containsExactly("IBJJF", "ADCC");
    }

    @Test
    void should_return_empty_list_when_no_federations_exist() {
        given(repository.findAll()).willReturn(List.of());

        var result = service.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void should_return_federation_when_findById_exists() {
        var federation = Federation.builder().id(10L).code("UFC").name("Ultimate Fighting Championship").build();
        given(repository.findById(10L)).willReturn(Optional.of(federation));

        var result = service.findById(10L);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.code()).isEqualTo("UFC");
    }

    @Test
    void should_throw_NotFoundException_when_findById_not_found() {
        given(repository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", "FEDERATION_NOT_FOUND");
    }
}
