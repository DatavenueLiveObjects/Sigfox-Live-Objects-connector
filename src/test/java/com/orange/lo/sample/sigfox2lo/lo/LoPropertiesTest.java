package com.orange.lo.sample.sigfox2lo.lo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoPropertiesTest {

    @Test
    void shouldSetHostnameCorrectly() {
        String hostname = "liveobjects.orange-business.com";
        LoProperties loProperties = new LoProperties();
        loProperties.setHostname(hostname);

        assertEquals(hostname, loProperties.getHostname());
    }

    @Test
    void shouldSetApiKeyCorrectly() {
        String apiKey = "loAp1K3y";
        LoProperties loProperties = new LoProperties();
        loProperties.setApiKey(apiKey);

        assertEquals(apiKey, loProperties.getApiKey());
    }

    @Test
    void shouldSetSynchronizationDeviceInterval() {
        int synchronizationDeviceInterval = 1000;
        LoProperties loProperties = new LoProperties();
        loProperties.setSynchronizationDeviceInterval(synchronizationDeviceInterval);

        assertEquals(synchronizationDeviceInterval, loProperties.getSynchronizationDeviceInterval());
    }

    @Test
    void shouldSetSynchronizationThreadPoolSize() {
        int synchronizationThreadPoolSize = 5;
        LoProperties loProperties = new LoProperties();
        loProperties.setSynchronizationThreadPoolSize(synchronizationThreadPoolSize);

        assertEquals(synchronizationThreadPoolSize, loProperties.getSynchronizationThreadPoolSize());
    }

    @Test
    void shouldSetMessageQosCorrectly() {
        int qos = 1;
        LoProperties loProperties = new LoProperties();
        loProperties.setMessageQos(qos);

        assertEquals(qos, loProperties.getMessageQos());
    }

    @Test
    void shouldSetMessageDecoder() {
        String messageDecoderName = "MessageDecoderName";
        LoProperties loProperties = new LoProperties();
        loProperties.setMessageDecoder(messageDecoderName);

        assertEquals(messageDecoderName, loProperties.getMessageDecoder());
    }
}