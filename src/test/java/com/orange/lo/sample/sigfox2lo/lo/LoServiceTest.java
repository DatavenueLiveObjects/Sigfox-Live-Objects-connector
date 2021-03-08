package com.orange.lo.sample.sigfox2lo.lo;

import com.orange.lo.sample.sigfox2lo.sigfox.model.DataUpDto;
import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.externalconnector.DataManagementExtConnector;
import com.orange.lo.sdk.externalconnector.model.DataMessage;
import com.orange.lo.sdk.rest.devicemanagement.DeviceManagement;
import com.orange.lo.sdk.rest.devicemanagement.GetDevicesFilter;
import com.orange.lo.sdk.rest.devicemanagement.Groups;
import com.orange.lo.sdk.rest.devicemanagement.Inventory;
import com.orange.lo.sdk.rest.model.Device;
import com.orange.lo.sdk.rest.model.Group;

import net.jodah.failsafe.RetryPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoServiceTest {

    public static final String NODE_ID = "N0D3ID1";
    private static final List<String> DEVICES = Arrays.asList("N0D3ID1", "N0D3ID2", "N0D3ID3", "N0D3ID4", "N0D3ID5");
    private static final String MESSAGE_ID = "0ZWkDm";

    @Mock
    private LOApiClient loApiClient;
    @Mock
    private DeviceManagement deviceManagement;
    @Mock
    private Groups groups;
    @Mock
    private Inventory inventory;
    @Mock
    private LoProperties loProperties;
    @Mock
    private LoDeviceCache loDeviceCache;
    @Mock
    private DataManagementExtConnector dataManagementExtConnector;
    private LoService loService;

    @BeforeEach
    void setUp() {
        initMocks();

        loProperties = new LoProperties();
        loProperties.setPageSize(20);
        loProperties.setDeviceGroup("sigfox");
        loService = new LoService(loApiClient, loProperties, loDeviceCache, new RetryPolicy<>(), new RetryPolicy<>(),
                new RetryPolicy<>());
    }

    @Test
    void shouldCallInventoryAndLoDeviceCacheWhenCreatingDevice() {
        loService.createDevice(NODE_ID);

        verify(inventory, times(1)).createDevice(contains(NODE_ID), contains(MESSAGE_ID));
        verify(groups, times(1)).getGroups();
        verify(loDeviceCache, times(1)).add(contains(NODE_ID));
    }

    @Test
    void shouldCallInventoryAndLoDeviceCacheWhenRemovingDevice() {
        loService.deleteDevice(NODE_ID);

        verify(inventory, times(1)).deleteDevice(contains(NODE_ID));
        verify(loDeviceCache, times(1)).delete(contains(NODE_ID));
    }

    @Test
    void shouldCallInventoryTwiceWhenNumberOfReturnedDevicesIsEqualToPageSize() {
        Device device = new Device();
        List<Device> devices = Collections.nCopies(loProperties.getPageSize(), device);
        when(inventory.getDevices(argThat(f -> f.getOffset().equals(0)))).thenReturn(devices);

        loService.getDevices();

        verify(inventory, times(2)).getDevices(any(GetDevicesFilter.class));
    }

    @Test
    void shouldCallInventoryTwiceWhenNumberOfReturnedDevicesIsBetweenPageSizeAndTwiceItsSize() {
        Device device = new Device();
        List<Device> devices = Collections.nCopies(25, device);
        when(inventory.getDevices(argThat(f -> f.getOffset().equals(0)))).thenReturn(devices);

        loService.getDevices();

        verify(inventory, times(2)).getDevices(any(GetDevicesFilter.class));
    }

    @Test
    void shouldCallInventoryOnceWhenNumberOfReturnedDevicesIsLessToPageSize() {
        Device device = new Device();
        List<Device> devices = Collections.nCopies(5, device);
        when(inventory.getDevices(argThat(f -> f.getOffset().equals(0)))).thenReturn(devices);

        loService.getDevices();

        verify(inventory, times(1)).getDevices(any(GetDevicesFilter.class));
    }

    @Test
    void shouldCallLoDeviceCacheWhenDevicesAreAddedToCache() {
        Set<String> devices = new HashSet<>(DEVICES);
        loService.addDevicesToCache(devices);

        verify(loDeviceCache, times(1)).addAll(devices);
    }

    @Test
    void shouldCallDataManagementExtConnectorAndInventoryAndLoDeviceCacheWhenMessageIsSentAndDeviceDoesNotExist() {
        DataUpDto dataUpDto = new DataUpDto();
        dataUpDto.setDevice(NODE_ID);

        loService.sendMessage(dataUpDto);

        verify(loDeviceCache, times(1)).add(NODE_ID);
        verify(inventory, times(1)).createDevice(contains(NODE_ID), contains(MESSAGE_ID));
        verify(dataManagementExtConnector, times(1)).sendMessage(contains(NODE_ID), any(DataMessage.class));
    }

    @Test
    void shouldCallOnlyDataManagementExtConnectorWhenMessageIsSentAndDeviceExist() {
        when(loDeviceCache.contains(NODE_ID)).thenReturn(true);

        DataUpDto dataUpDto = new DataUpDto();
        dataUpDto.setDevice(NODE_ID);

        loService.sendMessage(dataUpDto);

        verify(loDeviceCache, times(0)).add(NODE_ID);
        verify(inventory, times(0)).createDevice(contains(NODE_ID));
        verify(dataManagementExtConnector, times(1)).sendMessage(contains(NODE_ID), any(DataMessage.class));
    }

    private void initMocks() {
        when(loApiClient.getDataManagementExtConnector()).thenReturn(dataManagementExtConnector);
        when(loApiClient.getDeviceManagement()).thenReturn(deviceManagement);
        when(deviceManagement.getInventory()).thenReturn(inventory);
        when(deviceManagement.getGroups()).thenReturn(groups);
        when(groups.getGroups()).thenReturn(new ArrayList<>());
        Group group = new Group()
                .withId(MESSAGE_ID)
                .withPathNode(loProperties.getDeviceGroup());
        when(groups.createGroup(anyString())).thenReturn(group);
    }
}