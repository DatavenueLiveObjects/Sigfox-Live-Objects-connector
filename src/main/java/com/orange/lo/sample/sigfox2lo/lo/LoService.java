/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sigfox2lo.lo;

import static com.orange.lo.sdk.rest.devicemanagement.Inventory.XCONNECTOR_DEVICES_PREFIX;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.orange.lo.sdk.externalconnector.DataManagementExtConnector;
import com.orange.lo.sdk.rest.devicemanagement.DeviceManagement;
import com.orange.lo.sdk.rest.devicemanagement.Groups;
import com.orange.lo.sdk.rest.devicemanagement.Inventory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.orange.lo.sample.sigfox2lo.sigfox.SigfoxService;
import com.orange.lo.sample.sigfox2lo.sigfox.model.DataUpDto;
import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.externalconnector.model.DataMessage;
import com.orange.lo.sdk.externalconnector.model.Metadata;
import com.orange.lo.sdk.externalconnector.model.NodeStatus;
import com.orange.lo.sdk.externalconnector.model.Status;
import com.orange.lo.sdk.rest.devicemanagement.GetDevicesFilter;
import com.orange.lo.sdk.rest.model.Device;
import com.orange.lo.sdk.rest.model.Group;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

@Component
public class LoService {

	private String groupId;
	
	private LoProperties loProperties;
	private LoDeviceCache loDeviceCache;
	private RetryPolicy<List<Device>> restDevicesRetryPolicy;
	private RetryPolicy<Group> restGroupRetryPolicy;
	private RetryPolicy<Void> mqttRetryPolicy;
	private Inventory inventory;
	private Groups groups;
	private DataManagementExtConnector dataManagementExtConnector;

	private SigfoxService sigfoxService;
	

	public LoService(LOApiClient loApiClient, LoProperties loProperties, SigfoxService sigfoxService, LoDeviceCache loDeviceCache,
					 RetryPolicy<List<Device>> restDevicesRetryPolicy, RetryPolicy<Group> restGroupRetryPolicy,
					 RetryPolicy<Void> mqttRetryPolicy) {
		this.loProperties = loProperties;
		this.sigfoxService = sigfoxService;
		this.loDeviceCache = loDeviceCache;
		this.restDevicesRetryPolicy = restDevicesRetryPolicy;
		this.restGroupRetryPolicy = restGroupRetryPolicy;
		this.mqttRetryPolicy = mqttRetryPolicy;
		this.dataManagementExtConnector = loApiClient.getDataManagementExtConnector();
		DeviceManagement deviceManagement = loApiClient.getDeviceManagement();
		this.inventory = deviceManagement.getInventory();
		this.groups = deviceManagement.getGroups();
		manageGroup();
	}
	
	public void manageGroup() {
		Group group = Failsafe //
				.with(restGroupRetryPolicy) //
				.get(
					() -> groups.getGroups().stream() //
							.filter(g -> loProperties.getDeviceGroup().equals(g.getPathNode())) //
							.findAny() //
							.orElseGet( //
								() -> groups.createGroup(loProperties.getDeviceGroup()) //
							) //
				); //
		
		groupId = group.getId();
	}

	public void createDevice(String nodeId, String deviceName) {
		Failsafe.with(restDevicesRetryPolicy).run(() -> {
			Group group = new Group().withId(groupId);
	    	Device device = new Device().withId(XCONNECTOR_DEVICES_PREFIX + nodeId).withName(deviceName).withGroup(group);
			inventory.createDevice(device);
			loDeviceCache.add(nodeId);
		});
	}

	public void deleteDevice(String nodeId) {
		Failsafe.with(restDevicesRetryPolicy).run(() -> {
			inventory.deleteDevice(XCONNECTOR_DEVICES_PREFIX + nodeId);
			loDeviceCache.delete(nodeId);
		});
	}

	public List<Device> getDevices() {
		GetDevicesFilter filter = new GetDevicesFilter().withLimit(loProperties.getPageSize()).withGroupId(groupId);
		List<Device> devices = new ArrayList<>();
		for (int i = 0;; i++) {
			int j = i;
			List<Device> list = Failsafe //
					.with(restDevicesRetryPolicy) //
					.get(() -> inventory.getDevices(filter.withOffset(j * loProperties.getPageSize()))); //
			
			devices.addAll(list);
			if (list.size() < loProperties.getPageSize()) {
				break;
			}
		}
		return devices;
	}

	public void sendMessage(DataUpDto dataUpDto) {
		Failsafe.with(mqttRetryPolicy).run(() -> {
			String nodeId = dataUpDto.getDevice();
			ensureDeviceExists(nodeId);
			DataMessage dataMessage = prepareMessage(dataUpDto);
			dataManagementExtConnector.sendMessage(nodeId, dataMessage);
		});
	}

	public void sendStatus(String nodeId) {
		Failsafe.with(mqttRetryPolicy).run(() -> {
			NodeStatus nodeStatus = new NodeStatus();
			nodeStatus.setStatus(Status.ONLINE);
			nodeStatus.setCapabilities(new NodeStatus.Capabilities(false));
			dataManagementExtConnector.sendStatus(nodeId, nodeStatus);
		});
	}

	public void addDevicesToCache(Set<String> loDeviceIds) {
		loDeviceCache.addAll(loDeviceIds);
	}

	private void ensureDeviceExists(String nodeId) {
		if (!loDeviceCache.contains(nodeId)) {
			String name = sigfoxService.getDevice(nodeId).getName();
			createDevice(nodeId, name);
			sendStatus(nodeId);
		}
	}

	private DataMessage prepareMessage(DataUpDto dataUpDto) {
		DataMessage dataMessage = new DataMessage();
		dataMessage.setValue(dataUpDto);
		
		if (StringUtils.hasLength(loProperties.getMessageDecoder())) {
			dataMessage.setMetadata(new Metadata(loProperties.getMessageDecoder()));
		}
		return dataMessage;
	}
}