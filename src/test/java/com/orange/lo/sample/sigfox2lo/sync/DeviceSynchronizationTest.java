/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sigfox2lo.sync;

import com.orange.lo.sample.sigfox2lo.lo.LoService;
import com.orange.lo.sample.sigfox2lo.sigfox.SigfoxService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.orange.lo.sdk.rest.devicemanagement.Inventory.XCONNECTOR_DEVICES_PREFIX;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceSynchronizationTest {

    @Mock
    private SigfoxService sigfoxService;
    @Mock
    private LoService loService;
    @Mock
    private ThreadPoolExecutor synchronizingExecutor;
    @Captor
    private ArgumentCaptor<Collection<? extends Callable<Object>>> captor;
    private DeviceSynchronization deviceSynchronization;
    private int numberOfDevicesToAddToLo;
    private int numberOfDevicesToRemoveFromLo;

    @BeforeEach
    void setUp() {
        List<com.orange.lo.sample.sigfox2lo.sigfox.model.Device> sigfoxDevices = Arrays.asList(
                new com.orange.lo.sample.sigfox2lo.sigfox.model.Device("N0D3ID1"),
                new com.orange.lo.sample.sigfox2lo.sigfox.model.Device("N0D3ID2")
        );
        when(sigfoxService.getDevices()).thenReturn(sigfoxDevices);

        List<com.orange.lo.sdk.rest.model.Device> loDevices = Arrays.asList(
                new com.orange.lo.sdk.rest.model.Device().withId(XCONNECTOR_DEVICES_PREFIX + "N0D3ID1"),
                new com.orange.lo.sdk.rest.model.Device().withId(XCONNECTOR_DEVICES_PREFIX + "N0D3ID3"),
                new com.orange.lo.sdk.rest.model.Device().withId(XCONNECTOR_DEVICES_PREFIX + "N0D3ID4")
        );
        when(loService.getDevices()).thenReturn(loDevices);

        numberOfDevicesToAddToLo = (int) sigfoxDevices.stream()
                .filter(sd -> loDevices.stream().noneMatch(ld -> haveSameId(sd, ld)))
                .count();
        numberOfDevicesToRemoveFromLo = (int) loDevices.stream()
                .filter(ld -> sigfoxDevices.stream().noneMatch(sd -> haveSameId(sd, ld)))
                .count();

        deviceSynchronization = new DeviceSynchronization(sigfoxService, loService, synchronizingExecutor);
    }

    @Test
    void shouldSynchronizeEachDeviceInSeparateTask() throws InterruptedException {
        deviceSynchronization.synchronize();

        verify(synchronizingExecutor, times(2)).invokeAll(captor.capture());
        List<? extends Callable<Object>> collect = captor.getAllValues().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        int totalNumberOfDevicesToSynchronize = numberOfDevicesToAddToLo + numberOfDevicesToRemoveFromLo;
        assertEquals(totalNumberOfDevicesToSynchronize, collect.size());
    }

    @Test
    void shouldAddCorrectNumberOfDevicesWhenDeviceListIsNotEmpty() throws InterruptedException {
        deviceSynchronization.synchronize();

        verify(synchronizingExecutor, times(2)).invokeAll(captor.capture());
        List<Callable<Object>> createDeviceTasks = captor.getAllValues().stream()
                .filter(this::isCollectionOfCreateDeviceTask)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        assertEquals(numberOfDevicesToAddToLo, createDeviceTasks.size());
    }

    @Test
    void shouldRemoveCorrectNumberOfDevicesWhenDeviceListIsNotEmpty() throws InterruptedException {
        deviceSynchronization.synchronize();

        verify(synchronizingExecutor, times(2)).invokeAll(captor.capture());
        List<Callable<Object>> deleteDeviceTasks = captor.getAllValues().stream()
                .filter(this::isCollectionOfDeleteDeviceTask)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        assertEquals(numberOfDevicesToRemoveFromLo, deleteDeviceTasks.size());
    }

    private boolean isCollectionOfCreateDeviceTask(Collection<? extends Callable<Object>> callables) {
        return callables.stream().anyMatch(callable -> (callable.getClass().equals(CreateDeviceTask.class)));
    }

    private boolean isCollectionOfDeleteDeviceTask(Collection<? extends Callable<Object>> callables) {
        return callables.stream().anyMatch(callable -> (callable.getClass().equals(DeleteDeviceTask.class)));
    }

    private boolean haveSameId(com.orange.lo.sample.sigfox2lo.sigfox.model.Device sd, com.orange.lo.sdk.rest.model.Device ld) {
        String deviceIdWithoutPrefix = ld.getId().substring(XCONNECTOR_DEVICES_PREFIX.length());
        return sd.getId().equalsIgnoreCase(deviceIdWithoutPrefix);
    }
}