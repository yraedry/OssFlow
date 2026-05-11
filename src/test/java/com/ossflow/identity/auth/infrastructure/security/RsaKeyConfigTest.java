package com.ossflow.identity.auth.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class RsaKeyConfigTest {

    @Test
    void loads_from_classpath_when_b64_empty_and_dev_profile() throws Exception {
        Environment env = mock(Environment.class);
        given(env.getActiveProfiles()).willReturn(new String[]{"dev"});

        RsaKeyConfig config = new RsaKeyConfig(env);
        config.setPrivateKeyPath(new ClassPathResource("certs/private.pem"));
        config.setPublicKeyPath(new ClassPathResource("certs/public.pem"));

        assertThat(config.rsaPrivateKey()).isNotNull();
        assertThat(config.rsaPublicKey()).isNotNull();
    }

    @Test
    void loads_from_b64_when_provided() throws Exception {
        Environment env = mock(Environment.class);
        given(env.getActiveProfiles()).willReturn(new String[]{"prod"});

        String pem = new ClassPathResource("certs/private.pem").getContentAsString(StandardCharsets.UTF_8);
        String b64 = Base64.getEncoder().encodeToString(pem.getBytes(StandardCharsets.UTF_8));

        String pubPem = new ClassPathResource("certs/public.pem").getContentAsString(StandardCharsets.UTF_8);
        String pubB64 = Base64.getEncoder().encodeToString(pubPem.getBytes(StandardCharsets.UTF_8));

        RsaKeyConfig config = new RsaKeyConfig(env);
        config.setPrivateKeyB64(b64);
        config.setPublicKeyB64(pubB64);

        assertThat(config.rsaPrivateKey()).isNotNull();
        assertThat(config.rsaPublicKey()).isNotNull();
    }

    @Test
    void fails_when_prod_profile_and_no_b64_or_path() {
        Environment env = mock(Environment.class);
        given(env.getActiveProfiles()).willReturn(new String[]{"prod"});

        RsaKeyConfig config = new RsaKeyConfig(env);

        assertThatThrownBy(config::rsaPrivateKey)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("AUTH_JWT_PRIVATE_KEY_B64");
    }
}
