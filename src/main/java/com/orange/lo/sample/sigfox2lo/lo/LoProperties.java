/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sigfox2lo.lo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "lo")
public class LoProperties {

	private static final String CONNECTOR_TYPE = "SIGFOX_LO_ADAPTER";
	private static final int DEFAULT_PAGE_SIZE = 1000;
	private static final String DEFAULT_HOSTNAME = "liveobjects.orange-business.com";
	private static final int DEFAULT_SYNCHRONIZATION_THREAD_POOL_SIZE = 40;
	private static final int DEFAULT_MESSAGE_RETRY_DAYS = 10;

	private static final int DEFAULT_MESSAGE_QOS = 1;
	private static final String DEFAULT_MQTT_PERSISTENCE_DIR = ".";
	
	
	private String hostname = DEFAULT_HOSTNAME;
	private String apiKey;
	private String deviceGroup;
	private int synchronizationDeviceInterval;
	private int synchronizationThreadPoolSize = DEFAULT_SYNCHRONIZATION_THREAD_POOL_SIZE;
	private int messageRetryDays = DEFAULT_MESSAGE_RETRY_DAYS;
	private int pageSize = DEFAULT_PAGE_SIZE;
	private int messageQos = DEFAULT_MESSAGE_QOS;
	private String mqttPersistenceDir = DEFAULT_MQTT_PERSISTENCE_DIR;
	private String messageDecoder;

	public String getConnectorType() {
        return CONNECTOR_TYPE;
    }
	
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getDeviceGroup() {
		return deviceGroup;
	}

	public void setDeviceGroup(String deviceGroup) {
		this.deviceGroup = deviceGroup;
	}
	
	public int getSynchronizationDeviceInterval() {
		return synchronizationDeviceInterval;
	}

	public void setSynchronizationDeviceInterval(int synchronizationDeviceInterval) {
		this.synchronizationDeviceInterval = synchronizationDeviceInterval;
	}

	public int getSynchronizationThreadPoolSize() {
		return synchronizationThreadPoolSize;
	}

	public void setSynchronizationThreadPoolSize(int synchronizationThreadPoolSize) {
		this.synchronizationThreadPoolSize = synchronizationThreadPoolSize;
	}
	
	public int getMessageRetryDays() {
		return messageRetryDays;
	}

	public void setMessageRetryDays(int messageRetryDays) {
		this.messageRetryDays = messageRetryDays;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getMessageQos() {
		return messageQos;
	}

	public void setMessageQos(int messageQos) {
		this.messageQos = messageQos;
	}

	public String getMqttPersistenceDir() {
		return mqttPersistenceDir;
	}
	
	public void setMqttPersistenceDir(String mqttPersistenceDir) {
		this.mqttPersistenceDir = mqttPersistenceDir;
	}

	public String getMessageDecoder() {
		return messageDecoder;
	}

	public void setMessageDecoder(String messageDecoder) {
		this.messageDecoder = messageDecoder;
	}

	@Override
	public String toString() {
		return "LoProperties [hostname=" + hostname + ", apiKey=" + apiKey + ", deviceGroup=" + deviceGroup
				+ ", synchronizationDeviceInterval=" + synchronizationDeviceInterval
				+ ", synchronizationThreadPoolSize=" + synchronizationThreadPoolSize + ", messageRetryDays="
				+ messageRetryDays + ", pageSize=" + pageSize + ", messageQos=" + messageQos + ", mqttPersistenceDir="
				+ mqttPersistenceDir + ", messageDecoder=" + messageDecoder + "]";
	}	
}