/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sigfox2lo.sigfox;

import com.orange.lo.sample.sigfox2lo.sigfox.model.Device;
import com.orange.lo.sample.sigfox2lo.sigfox.model.SigfoxPaging;
import com.orange.lo.sample.sigfox2lo.sigfox.model.SigfoxResponse;
import com.orange.lo.sample.sigfox2lo.sigfox.model.UndeliveredCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SigfoxServiceTest {

    public static final String CALLBACKS_ENDPOINT = "callbacks-not-delivered";
    public static final String DEVICES_ENDPOINT = "devices";
    public static final String NEXT_PAGE = "next-page";
    public static final int RECORDS_ON_FIRST_PAGE = 10;
    public static final int RECORDS_ON_SECOND_PAGE = 5;
    public static final String DEVICE_ID = "N0D3ID1";
    public static final Long SINCE = 1610706663L;

    @Mock
    private RestTemplate restTemplate;
    private SigfoxService sigfoxService;

    @BeforeEach
    void setUp() {
        SigfoxProperties properties = new SigfoxProperties();
        sigfoxService = new SigfoxService(properties, restTemplate);
    }

    @Test
    void shouldOnlyCallSigfoxDevicesApiOnceWhenNextUrlIsEmpty() {
        ResponseEntity<SigfoxResponse<Device>> responseResponseEntity = getSigfoxDevicesResponse(RECORDS_ON_FIRST_PAGE, null);

        when(restTemplate.exchange(contains(DEVICES_ENDPOINT), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseResponseEntity);

        List<Device> devices = sigfoxService.getDevices();

        verify(restTemplate, times(1))
                .exchange(contains(DEVICES_ENDPOINT), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class));
        assertEquals(RECORDS_ON_FIRST_PAGE, devices.size());
    }

    @Test
    void shouldCallSigfoxDevicesAPIUntilNextUrlIsEmpty() {
        ResponseEntity<SigfoxResponse<Device>> firstResponse = getSigfoxDevicesResponse(RECORDS_ON_FIRST_PAGE, NEXT_PAGE);
        when(restTemplate.exchange(contains(DEVICES_ENDPOINT), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(firstResponse);
        ResponseEntity<SigfoxResponse<Device>> secondResponse = getSigfoxDevicesResponse(RECORDS_ON_SECOND_PAGE, null);
        when(restTemplate.exchange(contains(NEXT_PAGE), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(secondResponse);

        List<Device> devices = sigfoxService.getDevices();

        verify(restTemplate, times(1))
                .exchange(contains(DEVICES_ENDPOINT), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class));
        verify(restTemplate, times(1))
                .exchange(contains(NEXT_PAGE), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class));

        int expected = RECORDS_ON_FIRST_PAGE + RECORDS_ON_SECOND_PAGE;
        assertEquals(expected, devices.size());
    }

    @Test
    void shouldOnlyCallSigfoxUndeliveredCallbackApiOnceWhenNextUrlIsEmpty() {
        ResponseEntity<SigfoxResponse<UndeliveredCallback>> responseResponseEntity = getSigfoxUndeliveredCallbacksResponse(RECORDS_ON_FIRST_PAGE, null);
        when(restTemplate.exchange(contains(CALLBACKS_ENDPOINT), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseResponseEntity);

        List<UndeliveredCallback> undeliveredCallbacks = sigfoxService.getUndeliveredCallbacks(DEVICE_ID, SINCE);

        verify(restTemplate, times(1))
                .exchange(contains(CALLBACKS_ENDPOINT), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class));
        assertEquals(RECORDS_ON_FIRST_PAGE, undeliveredCallbacks.size());
    }

    @Test
    void shouldOnlyCallSigfoxUndeliveredCallbackApiUntilNextUrlIsEmpty() {
        ResponseEntity<SigfoxResponse<UndeliveredCallback>> firstResponse = getSigfoxUndeliveredCallbacksResponse(RECORDS_ON_FIRST_PAGE, NEXT_PAGE);
        when(restTemplate.exchange(contains(CALLBACKS_ENDPOINT), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(firstResponse);
        ResponseEntity<SigfoxResponse<UndeliveredCallback>> secondResponse = getSigfoxUndeliveredCallbacksResponse(RECORDS_ON_SECOND_PAGE, null);
        when(restTemplate.exchange(contains(NEXT_PAGE), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(secondResponse);

        List<UndeliveredCallback> undeliveredCallbacks = sigfoxService.getUndeliveredCallbacks(DEVICE_ID, SINCE);

        verify(restTemplate, times(1))
                .exchange(contains(CALLBACKS_ENDPOINT), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class));
        verify(restTemplate, times(1))
                .exchange(contains(NEXT_PAGE), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class));

        int expected = RECORDS_ON_FIRST_PAGE + RECORDS_ON_SECOND_PAGE;
        assertEquals(expected, undeliveredCallbacks.size());
    }

    private ResponseEntity<SigfoxResponse<Device>> getSigfoxDevicesResponse(int numberOfDevices, String nextUrl) {
        Device device = new Device();
        List<Device> devices = Collections.nCopies(numberOfDevices, device);
        SigfoxPaging paging = new SigfoxPaging();
        paging.setNext(nextUrl);

        SigfoxResponse<Device> response = new SigfoxResponse<>();
        response.setData(devices);
        response.setPaging(paging);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<SigfoxResponse<UndeliveredCallback>> getSigfoxUndeliveredCallbacksResponse(int numberOfCallbacks, String nextUrl) {
        UndeliveredCallback callback = new UndeliveredCallback();
        List<UndeliveredCallback> callbacks = Collections.nCopies(numberOfCallbacks, callback);
        SigfoxPaging paging = new SigfoxPaging();
        paging.setNext(nextUrl);

        SigfoxResponse<UndeliveredCallback> response = new SigfoxResponse<>();
        response.setData(callbacks);
        response.setPaging(paging);
        return ResponseEntity.ok(response);
    }
}