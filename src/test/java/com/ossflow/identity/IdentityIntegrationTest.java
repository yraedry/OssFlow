package com.ossflow.identity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class IdentityIntegrationTest {

    @Autowired
    WebApplicationContext wac;

    MockMvc mvc;
    final ObjectMapper json = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    private static final String CREATE_BODY = """
            {
              "displayName": "Adrian BJJ",
              "currentBelt": "BLUE",
              "beltSince": "2022-01-15",
              "academy": "Team Omega",
              "preferredModality": "GI"
            }
            """;

    @Test
    void should_create_profile_and_get_it() throws Exception {
        // POST → 201
        String resp = mvc.perform(post("/api/v1/identity/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.displayName").value("Adrian BJJ"))
                .andExpect(jsonPath("$.currentBelt").value("BLUE"))
                .andExpect(jsonPath("$.onboardingCompleted").value(true))
                .andReturn().getResponse().getContentAsString();

        // GET → 200
        mvc.perform(get("/api/v1/identity/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Adrian BJJ"));
    }

    @Test
    void should_return_409_when_creating_profile_twice() throws Exception {
        // First creation
        mvc.perform(post("/api/v1/identity/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_BODY))
                .andExpect(status().isCreated());

        // Second creation → 409
        mvc.perform(post("/api/v1/identity/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("PROFILE_ALREADY_EXISTS"));
    }
}
