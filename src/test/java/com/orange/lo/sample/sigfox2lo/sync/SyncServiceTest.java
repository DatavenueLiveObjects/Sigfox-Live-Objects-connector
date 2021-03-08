package com.orange.lo.sample.sigfox2lo.sync;

import com.orange.lo.sample.sigfox2lo.db.Message;
import com.orange.lo.sample.sigfox2lo.db.MessageRepository;
import com.orange.lo.sample.sigfox2lo.sigfox.model.DataUpDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.util.DefaultPropertiesPersister;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.orange.lo.sample.sigfox2lo.sync.SyncService.PROPERTIES_FILE_HEADER;
import static com.orange.lo.sample.sigfox2lo.sync.SyncService.PROPERTIES_KEY_LAST_SYNC_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SyncServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private DefaultPropertiesPersister defaultPropertiesPersister;
    @Captor
    private ArgumentCaptor<Properties> captor;
    private SyncService syncService;

    @BeforeEach
    void setUp() {
        syncService = new SyncService(messageRepository, new ModelMapper(), defaultPropertiesPersister);
    }

    @Test
    void shouldReadPropertiesFileWhenSetLastSyncTime1() throws IOException {
        syncService.getLastSyncTime();

        verify(defaultPropertiesPersister, times(1)).load(any(Properties.class), any(BufferedInputStream.class));
    }

    @Test
    void shouldReloadPropertiesAndStoreTimeInPropertiesFileWhenLastSyncTimeIsSet() throws IOException {
        long time = new Date().getTime();
        syncService.setLastSyncTime(time);

        verify(defaultPropertiesPersister, times(1)).load(any(Properties.class), any(BufferedInputStream.class));
        verify(defaultPropertiesPersister, times(1)).store(captor.capture(), any(OutputStream.class), eq(PROPERTIES_FILE_HEADER));
        Properties value = captor.getValue();
        assertEquals(String.valueOf(time), value.getProperty(PROPERTIES_KEY_LAST_SYNC_TIME));
    }

    @Test
    void shouldCallMessageRepositoryWhenMessagesAreRetrieved() {
        when(messageRepository.findAll()).thenReturn(Collections.emptyList());

        syncService.getMessages();
        verify(messageRepository, times(1)).findAll();
    }

    @Test
    void shouldCorrectlyRetrieveMessages() {
        List<Message> messagesFromDB = IntStream.range(1, 10)
                .mapToObj(this::getMessage)
                .collect(Collectors.toList());
        when(messageRepository.findAll()).thenReturn(messagesFromDB);

        List<DataUpDto> messages = syncService.getMessages();
        assertEquals(messagesFromDB.size(), messages.size());

        for (Message m : messagesFromDB) {
            List<DataUpDto> collect = messages.stream()
                    .filter(dataUpDto -> equals(m, dataUpDto))
                    .collect(Collectors.toList());
            assertEquals(1, collect.size());
        }
    }

    @Test
    void shouldCallMessageRepositoryWhenMessageIsSaved() {
        syncService.saveMessage(new DataUpDto());

        verify(messageRepository, times(1)).save(any(Message.class));
    }

    private Message getMessage(Integer index) {
        Message message = new Message();
        message.setId(14L + index);
        message.setTime(1611338406L + index);
        message.setDevice("BC97" + index);
        message.setDeviceTypeId("5fc8c7d80499" + index);
        message.setSeqNumber(1004L + index);
        message.setData("271825ef19bddf35" + index);

        return message;
    }

    private boolean equals(Message m, DataUpDto dataUpDto) {
        return m.getSeqNumber().equals(dataUpDto.getSeqNumber())
                && m.getTime().equals(dataUpDto.getTime())
                && m.getDevice().equals(dataUpDto.getDevice())
                && m.getDeviceTypeId().equals(dataUpDto.getDeviceTypeId())
                && m.getData().equals(dataUpDto.getData());
    }
}