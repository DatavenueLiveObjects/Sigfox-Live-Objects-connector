/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sigfox2lo.sync;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Properties;

import com.orange.lo.sample.sigfox2lo.db.Message;
import com.orange.lo.sample.sigfox2lo.db.MessageRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orange.lo.sample.sigfox2lo.sigfox.model.DataUpDto;
import org.springframework.util.DefaultPropertiesPersister;

@Service
public class SyncService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String PROPERTIES_FILE_LOCATION = "sync.properties";
    protected static final String PROPERTIES_FILE_HEADER = "";
    protected static final String PROPERTIES_KEY_LAST_SYNC_TIME = "lastSyncTime";

    private final MessageRepository messageRepository;
    private final ModelMapper modelMapper;
    private final DefaultPropertiesPersister defaultPropertiesPersister;
    private final Resource resource;

    public SyncService(MessageRepository messageRepository, ModelMapper modelMapper,
                       DefaultPropertiesPersister defaultPropertiesPersister) {
        this.messageRepository = messageRepository;
        this.modelMapper = modelMapper;
        this.defaultPropertiesPersister = defaultPropertiesPersister;
        this.resource = new ClassPathResource(PROPERTIES_FILE_LOCATION, getClass().getClassLoader());
    }

    public Long getLastSyncTime() {
        long lastSyncTime = 0L;
        try {
            Properties props = getProperties();
            String value = props.getProperty(PROPERTIES_KEY_LAST_SYNC_TIME);
            lastSyncTime = value != null ? Long.parseLong(value) : 0;
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return lastSyncTime;
    }

    public void setLastSyncTime(Long time) {
        try {
            File file = resource.getFile();
            WritableResource fileSystemResource = new FileSystemResource(file);
            Properties properties = getProperties();
            properties.setProperty(PROPERTIES_KEY_LAST_SYNC_TIME, String.valueOf(time));
            defaultPropertiesPersister.store(properties, fileSystemResource.getOutputStream(), PROPERTIES_FILE_HEADER);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private Properties getProperties() throws IOException {
        Properties props = new Properties();
        defaultPropertiesPersister.load(props, resource.getInputStream());
        return props;
    }

    public List<DataUpDto> getMessages() {
        List<Message> messages = messageRepository.findAll();
        Type type = new TypeToken<List<DataUpDto>>() {
        }.getType();
        return modelMapper.map(messages, type);
    }

    public void saveMessage(DataUpDto message) {
        Message entity = modelMapper.map(message, Message.class);
        messageRepository.save(entity);
    }
    
    public void removeMessage(Long id) {
    	if (id != null) {
    		messageRepository.deleteById(id);    	
    	}
    }

    @Transactional
	public int removeMessagesOlderThan(long epochSecond) {
		return messageRepository.removeByTimeLessThan(epochSecond);
	}
}
