package com.orange.lo.sample.sigfox2lo.sync;

import com.orange.lo.sample.sigfox2lo.lo.LoService;
import com.orange.lo.sample.sigfox2lo.sigfox.model.DataUpDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SendMessageTaskTest {

    @Mock
    private LoService loService;
    
    @Mock
    private SyncService syncService;

    @Test
    void shouldSendMessageToLOWhenTaskIsCalled() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(10);
        DataUpDto dataUpDto = new DataUpDto();

        SendMessageTask sendMessageTask = new SendMessageTask(loService, syncService, dataUpDto);
        List<SendMessageTask> collection = Collections.singletonList(sendMessageTask);
        service.invokeAll(collection);

        verify(loService, times(1)).sendMessage(dataUpDto);
    }
}