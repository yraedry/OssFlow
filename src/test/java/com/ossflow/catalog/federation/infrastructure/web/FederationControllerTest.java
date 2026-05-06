package com.ossflow.catalog.federation.infrastructure.web;

import com.ossflow.catalog.federation.application.FederationService;
import com.ossflow.catalog.federation.domain.Federation;
import com.ossflow.shared.exception.GlobalExceptionHandler;
import com.ossflow.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FederationControllerTest {

    @Mock FederationService service;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        var mapper = new FederationWebMapperImpl();
        var controller = new FederationController(service, mapper);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void get_list_should_return_200_with_federations() throws Exception {
        given(service.findAll()).willReturn(List.of(
                Federation.builder().id(1L).code("IBJJF").name("International BJJ Federation").build(),
                Federation.builder().id(2L).code("ADCC").name("Abu Dhabi Combat Club").build()
        ));

        mvc.perform(get("/api/v1/catalog/federations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].code").value("IBJJF"));
    }

    @Test
    void get_by_id_should_return_404_when_not_found() throws Exception {
        given(service.findById(99L))
                .willThrow(new NotFoundException("FEDERATION_NOT_FOUND", "no encontrada", Map.of()));

        mvc.perform(get("/api/v1/catalog/federations/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("FEDERATION_NOT_FOUND"));
    }
}
