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
class DeleteDeviceTaskTest {

    @Mock
    private LoService loService;

    @Test
    void shouldCreateDeviceInLOWhenTaskIsCalled() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(10);
        String deviceId = "N0D3ID0";

        DeleteDeviceTask deleteDeviceTask = new DeleteDeviceTask(loService, deviceId);
        List<DeleteDeviceTask> collection = Collections.singletonList(deleteDeviceTask);
        service.invokeAll(collection);

        verify(loService, times(1)).deleteDevice(deviceId);
    }
}