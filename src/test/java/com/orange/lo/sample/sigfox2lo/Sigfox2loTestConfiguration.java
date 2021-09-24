/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sigfox2lo;

import com.orange.lo.sample.sigfox2lo.sigfox.SigfoxService;
import com.orange.lo.sample.sigfox2lo.sigfox.model.Device;
import com.orange.lo.sample.sigfox2lo.sync.SynchronizationManagement;
import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.externalconnector.DataManagementExtConnector;
import com.orange.lo.sdk.rest.devicemanagement.DeviceManagement;
import com.orange.lo.sdk.rest.devicemanagement.Groups;
import com.orange.lo.sdk.rest.devicemanagement.Inventory;
import com.orange.lo.sdk.rest.model.Group;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@TestConfiguration
public class Sigfox2loTestConfiguration {

    @Bean
    LOApiClient loApiClient() {
        LOApiClient loApiClient = Mockito.mock(LOApiClient.class);
        DeviceManagement deviceManagement = Mockito.mock(DeviceManagement.class);
        Mockito.when(loApiClient.getDeviceManagement()).thenReturn(deviceManagement);
        Inventory inventory = Mockito.mock(Inventory.class);
        Mockito.when(deviceManagement.getInventory()).thenReturn(inventory);
        Groups groups = Mockito.mock(Groups.class);
        when(deviceManagement.getGroups()).thenReturn(groups);
        when(groups.getGroups()).thenReturn(new ArrayList<>());
        Group group = new Group()
                .withId("0ZWkDm")
                .withPathNode("sigfox");
        when(groups.createGroup(anyString())).thenReturn(group);
        DataManagementExtConnector dataManagementExtConnector = Mockito.mock(DataManagementExtConnector.class);
        Mockito.when(loApiClient.getDataManagementExtConnector()).thenReturn(dataManagementExtConnector);

        return loApiClient;
    }

    @Bean SigfoxService sigfoxService() {
    	SigfoxService sigfoxService = Mockito.mock(SigfoxService.class);
    	Device device = new Device();
    	device.setId("N0D3ID1");
    	device.setName("N0D3ID1-name");
    	when(sigfoxService.getDevice(anyString())).thenReturn(device);
    	return sigfoxService;
    }
    
    @Bean
    RestTemplate restTemplate() {
        return Mockito.mock(RestTemplate.class);
    }

    @Bean
    SynchronizationManagement synchronizationManagement() {
        return Mockito.mock(SynchronizationManagement.class);
    }
}