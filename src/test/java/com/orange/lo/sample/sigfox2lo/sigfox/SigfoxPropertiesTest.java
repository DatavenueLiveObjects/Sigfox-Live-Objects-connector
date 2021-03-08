package com.orange.lo.sample.sigfox2lo.sigfox;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SigfoxPropertiesTest {

    @Test
    void shouldSetHostname() {
        SigfoxProperties sigfoxConfig = new SigfoxProperties();
        String hostname = "sigfox.hostname.com";
        sigfoxConfig.setHostname(hostname);

        assertEquals(hostname, sigfoxConfig.getHostname());
    }

    @Test
    void shouldSetLogin() {
        SigfoxProperties sigfoxConfig = new SigfoxProperties();
        String login = "sigfox.login";
        sigfoxConfig.setLogin(login);

        assertEquals(login, sigfoxConfig.getLogin());
    }

    @Test
    void shouldSetPassword() {
        SigfoxProperties sigfoxConfig = new SigfoxProperties();
        String password = "sigfox.password";
        sigfoxConfig.setPassword(password);

        assertEquals(password, sigfoxConfig.getPassword());
    }
}