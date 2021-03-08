package com.orange.lo.sample.sigfox2lo.sigfox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SigfoxConfigTest {

    public static final String SIGFOX_USER = "sigfox.user";
    public static final String SIGFOX_PASSWORD = "sigfox.password";

    private SigfoxConfig sigfoxConfig;

    @BeforeEach
    void setUp() {
        SigfoxProperties properties = new SigfoxProperties();
        properties.setLogin(SIGFOX_USER);
        properties.setPassword(SIGFOX_PASSWORD);
        sigfoxConfig = new SigfoxConfig(properties);
    }

    @Test
    void shouldCorrectlyCreateRestTemplate() {
        RestTemplate restTemplate = sigfoxConfig.restTemplate();

        assertNotNull(restTemplate);
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        assertEquals(1, interceptors.size());
    }
}