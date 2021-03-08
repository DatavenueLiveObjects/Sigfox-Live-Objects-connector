package com.orange.lo.sample.sigfox2lo.lo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoDeviceCacheTest {

    private static final String DEVICE_ID = "N0D3ID0";
    private static final List<String> DEVICE_ID_LIST
            = Arrays.asList("N0D3ID1", "N0D3ID2", "N0D3ID3", "N0D3ID4", "N0D3ID5");
    private LoDeviceCache loDeviceCache;

    @BeforeEach
    void setUp() {
        loDeviceCache = new LoDeviceCache();
    }

    @Test
    void shouldAddDeviceToCache() {
        loDeviceCache.add(DEVICE_ID);

        assertTrue(loDeviceCache.contains(DEVICE_ID));
    }

    @Test
    void shouldAddAllDevicesToCache() {
        loDeviceCache.addAll(DEVICE_ID_LIST);

        for (String deviceId : DEVICE_ID_LIST) {
            assertTrue(loDeviceCache.contains(deviceId));
        }
    }

    @Test
    void shouldDeleteDeviceWhenDeviceExistInCache() {
        loDeviceCache.add(DEVICE_ID);
        assertTrue(loDeviceCache.contains(DEVICE_ID));

        loDeviceCache.delete(DEVICE_ID);
        assertFalse(loDeviceCache.contains(DEVICE_ID));
    }

    @Test
    void shouldNotThrowAnyExceptionWhenDeviceDoesNotExistInCache() {
        assertDoesNotThrow(() -> loDeviceCache.delete(DEVICE_ID));
    }

    @Test
    void shouldContainsReturnTrueWhenDeviceExistInCache() {
        loDeviceCache.add(DEVICE_ID);

        assertTrue(loDeviceCache.contains(DEVICE_ID));
    }

    @Test
    void shouldContainsReturnFalseWhenDeviceDoesNotExistInCache() {
        assertFalse(loDeviceCache.contains(DEVICE_ID));
    }

    @Test
    void shouldBeThreadSafe() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(10);

        List<Callable<Void>> callable = DEVICE_ID_LIST.stream()
                .map(s -> (Callable<Void>) () -> {
                    loDeviceCache.add(s);
                    return null;
                })
                .collect(Collectors.toList());
        service.invokeAll(callable);

        for (String deviceId : DEVICE_ID_LIST) {
            assertTrue(loDeviceCache.contains(deviceId));
        }
    }
}