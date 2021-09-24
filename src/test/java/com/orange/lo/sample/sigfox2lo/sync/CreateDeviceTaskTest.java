/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sigfox2lo.sync;

import com.orange.lo.sample.sigfox2lo.lo.LoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateDeviceTaskTest {

    @Mock
    private LoService loService;

    @Test
    void shouldCreateDeviceInLOWhenTaskIsCalled() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(10);
        String deviceId = "N0D3ID0";
        String deviceName = "N0D3ID0-name";

        CreateDeviceTask createDeviceTask = new CreateDeviceTask(loService, deviceId, deviceName);
        List<CreateDeviceTask> collection = Collections.singletonList(createDeviceTask);
        service.invokeAll(collection);

        verify(loService, times(1)).createDevice(deviceId, deviceName);
    }

    @Test
    void shouldSendStatusToLOWhenTaskIsCalled() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(10);
        String deviceId = "N0D3ID0";
        String deviceName = "N0D3ID0-name";

        CreateDeviceTask createDeviceTask = new CreateDeviceTask(loService, deviceId, deviceName);
        List<CreateDeviceTask> collection = Collections.singletonList(createDeviceTask);
        service.invokeAll(collection);

        verify(loService, times(1)).sendStatus(deviceId);
    }
}