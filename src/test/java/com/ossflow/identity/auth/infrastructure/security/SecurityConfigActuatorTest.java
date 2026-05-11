package com.ossflow.identity.auth.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigActuatorTest {

    @Autowired WebApplicationContext wac;

    MockMvc mvc() {
        return MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();
    }

    @Test
    void health_is_public() throws Exception {
        // 200 si todos los componentes UP; 503 si alguno DOWN (p. ej. mail en test).
        // Lo relevante para A5 es que NO se devuelve 401/403.
        MvcResult result = mvc().perform(get("/actuator/health")).andReturn();
        int status = result.getResponse().getStatus();
        assertThat(status).isIn(200, 503);
    }

    @Test
    void info_is_public() throws Exception {
        mvc().perform(get("/actuator/info")).andExpect(status().isOk());
    }

    @Test
    void other_actuator_endpoints_are_not_permit_all() throws Exception {
        // El endpoint metrics no está expuesto (yml restringe a health/info), pero el control
        // de seguridad es que no se permita acceso anónimo. 401 (denegado) o 404 (no expuesto)
        // son ambos aceptables; el caso negativo sería 200.
        MvcResult result = mvc().perform(get("/actuator/metrics")).andReturn();
        int status = result.getResponse().getStatus();
        assertThat(status).isNotEqualTo(200);
    }
}
