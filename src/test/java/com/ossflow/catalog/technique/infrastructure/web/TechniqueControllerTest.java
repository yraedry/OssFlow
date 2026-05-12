package com.ossflow.catalog.technique.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ossflow.catalog.position.domain.Visibility;
import com.ossflow.catalog.ruleset.application.RulesetService;
import com.ossflow.catalog.ruleset.infrastructure.web.RulesetWebMapperImpl;
import com.ossflow.catalog.technique.application.TechniqueService;
import com.ossflow.catalog.technique.domain.Belt;
import com.ossflow.catalog.technique.domain.Modality;
import com.ossflow.catalog.technique.domain.Technique;
import com.ossflow.catalog.technique.domain.TechniqueCategory;
import com.ossflow.catalog.technique.infrastructure.web.dto.CreateTechniqueRequest;
import com.ossflow.shared.exception.DuplicateNameException;
import com.ossflow.shared.exception.GlobalExceptionHandler;
import com.ossflow.shared.web.CurrentOwner;
import com.ossflow.testsupport.TestSecurityContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TechniqueControllerTest {

    @Mock TechniqueService service;
    @Mock RulesetService rulesetService;

    MockMvc mvc;
    ObjectMapper json = new ObjectMapper();

    @BeforeEach
    void setUp() {
        var mapper = new TechniqueWebMapperImpl();
        var rulesetMapper = new RulesetWebMapperImpl();
        var controller = new TechniqueController(service, mapper, new CurrentOwner(), rulesetService, rulesetMapper);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        TestSecurityContext.setOwner(1L);
    }

    @AfterEach
    void tearDownAuth() { TestSecurityContext.clear(); }

    @Test
    void post_should_return_201_with_location_header() throws Exception {
        var req = new CreateTechniqueRequest(
                "Armbar", TechniqueCategory.SUBMISSION, null, null, null,
                Belt.WHITE, Modality.GI, 1L, null, Visibility.PRIVATE);
        given(service.create(org.mockito.ArgumentMatchers.any())).willReturn(
                Technique.builder().id(3L).ownerId(1L).name("Armbar")
                        .category(TechniqueCategory.SUBMISSION)
                        .minimumBelt(Belt.WHITE).modality(Modality.GI)
                        .startPositionId(1L).visibility(Visibility.PRIVATE).build());

        mvc.perform(post("/api/v1/catalog/techniques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/catalog/techniques/3"))
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    void post_should_return_400_when_name_blank() throws Exception {
        var req = new CreateTechniqueRequest(
                "", TechniqueCategory.SUBMISSION, null, null, null,
                Belt.WHITE, Modality.GI, 1L, null, Visibility.PRIVATE);

        mvc.perform(post("/api/v1/catalog/techniques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("name"));
    }

    @Test
    void post_should_return_409_when_name_duplicate() throws Exception {
        var req = new CreateTechniqueRequest(
                "Armbar", TechniqueCategory.SUBMISSION, null, null, null,
                Belt.WHITE, Modality.GI, 1L, null, Visibility.PRIVATE);
        given(service.create(org.mockito.ArgumentMatchers.any()))
                .willThrow(new DuplicateNameException("TECHNIQUE_NAME_DUPLICATE", "duplicado", Map.of()));

        mvc.perform(post("/api/v1/catalog/techniques")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("TECHNIQUE_NAME_DUPLICATE"));
    }
}
