package com.orange.lo.sample.sigfox2lo.sigfox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.orange.lo.sample.sigfox2lo.sigfox.model.Device;
import com.orange.lo.sample.sigfox2lo.sigfox.model.UndeliveredCallback;
import com.orange.lo.sample.sigfox2lo.sigfox.model.SigfoxResponse;

@Component
public class SigfoxService {

	private RestTemplate restTemplate;
	private String devicesUrl;
	private String callbacksUrl;

	public SigfoxService(SigfoxProperties properties, RestTemplate restTemplate) {
		this.devicesUrl = properties.getHostname() + "/devices";
        this.callbacksUrl = this.devicesUrl + "/%s/callbacks-not-delivered?since=%s";
		this.restTemplate = restTemplate;		
	}

	public List<Device> getDevices() {
		List<Device> devices = new ArrayList<>();

		ParameterizedTypeReference<SigfoxResponse<Device>> parameterizedTypeReference = new ParameterizedTypeReference<SigfoxResponse<Device>>() {};        
		ResponseEntity<SigfoxResponse<Device>> exchange = null;			
		String url = devicesUrl;
		while (url != null) {
			exchange = restTemplate.exchange(url, HttpMethod.GET, null, parameterizedTypeReference);

			Optional<SigfoxResponse<Device>> responseBody = Optional.ofNullable(exchange.getBody());
			if (responseBody.isPresent()) {
				SigfoxResponse<Device> body = responseBody.get();
				devices.addAll(body.getData());
				url = body.getPaging().getNext();
			} else {
				url = null;
			}
		}
		return devices;
	}

	public List<UndeliveredCallback> getUndeliveredCallbacks(String deviceId, Long since) {
		List<UndeliveredCallback> undeliveredCallbacks = new ArrayList<>();

		ParameterizedTypeReference<SigfoxResponse<UndeliveredCallback>> parameterizedTypeReference =
				new ParameterizedTypeReference<SigfoxResponse<UndeliveredCallback>>() {
				};

		String url = String.format(callbacksUrl, deviceId, since);
		while (url != null) {
			ResponseEntity<SigfoxResponse<UndeliveredCallback>> exchange =
					restTemplate.exchange(url, HttpMethod.GET, null, parameterizedTypeReference);

			Optional<SigfoxResponse<UndeliveredCallback>> responseBody = Optional.ofNullable(exchange.getBody());
			if (responseBody.isPresent()) {
				SigfoxResponse<UndeliveredCallback> body = responseBody.get();
				undeliveredCallbacks.addAll(body.getData());
				url = body.getPaging().getNext();
			} else {
				url = null;
			}
		}
		return undeliveredCallbacks;
	}
}
