/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sigfox2lo.sync;

import com.orange.lo.sample.sigfox2lo.lo.LoDeviceCache;
import com.orange.lo.sample.sigfox2lo.lo.LoService;
import com.orange.lo.sample.sigfox2lo.sigfox.SigfoxService;
import com.orange.lo.sample.sigfox2lo.sigfox.model.Callback;
import com.orange.lo.sample.sigfox2lo.sigfox.model.DataUpDto;
import com.orange.lo.sample.sigfox2lo.sigfox.model.UndeliveredCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageSynchronizationTest {

    @Mock
    private LoDeviceCache loDeviceCache;
    @Mock
    private SigfoxService sigfoxService;
    @Mock
    private LoService loService;
    @Mock
    private SyncService syncService;
    @Mock
    private ThreadPoolExecutor synchronizingExecutor;
    private MessageSynchronization messageSynchronization;

    @BeforeEach
    void setUp() {
        messageSynchronization = new MessageSynchronization(loDeviceCache, sigfoxService, loService, syncService, synchronizingExecutor);
    }

    @Test
    void shouldSynchronizeEachUndeliveredCallbackInSeparateTask() throws InterruptedException {
        int undeliveredCallbackPerDevice = 5;
        List<String> allDevices = Arrays.asList("N0D3ID1", "N0D3ID2");
        when(loDeviceCache.getAll()).thenReturn(allDevices);
        long timeMillis = System.currentTimeMillis();
        when(syncService.getLastSyncTime()).thenReturn(timeMillis);

        for (String deviceId : allDevices) {
            List<UndeliveredCallback> undeliveredCallbacks = getUndeliveredCallbacks(deviceId, undeliveredCallbackPerDevice);
            when(sigfoxService.getUndeliveredCallbacks(deviceId, timeMillis)).thenReturn(undeliveredCallbacks);
        }

        messageSynchronization.synchronize();

        int totalNumberOfExpectedUndeliveredCallbacks = undeliveredCallbackPerDevice * allDevices.size();
        verify(synchronizingExecutor, times(1))
                .invokeAll(argThat(callables ->
                        isCollectionOfSendMessageTasksWithSpecifiedSize(totalNumberOfExpectedUndeliveredCallbacks, callables))
                );
        verify(loDeviceCache, times(1)).getAll();
        verify(syncService, times(allDevices.size())).getLastSyncTime();
        verify(syncService, times(1)).setLastSyncTime(any(Long.class));
        for (String deviceId : allDevices) {
            verify(sigfoxService, times(1)).getUndeliveredCallbacks(deviceId, timeMillis);
        }
    }

    private boolean isCollectionOfSendMessageTasksWithSpecifiedSize(int totalNumberOfUndeliveredCallbacks,
                                                                    Collection<? extends Callable<Object>> callables) {
        boolean isCollectionOfSendMessageTasks = callables.stream()
                .anyMatch(callable -> (callable.getClass().equals(SendMessageTask.class)));

        return callables.size() == totalNumberOfUndeliveredCallbacks && isCollectionOfSendMessageTasks;
    }

    private List<UndeliveredCallback> getUndeliveredCallbacks(String deviceId, int size) {
        DataUpDto dataUpDto = new DataUpDto();
        dataUpDto.setDevice(deviceId);
        Callback callback = new Callback();
        callback.setBody(dataUpDto);
        UndeliveredCallback undeliveredCallback = new UndeliveredCallback();
        undeliveredCallback.setCallback(callback);

        return Collections.nCopies(size, undeliveredCallback);
    }
}