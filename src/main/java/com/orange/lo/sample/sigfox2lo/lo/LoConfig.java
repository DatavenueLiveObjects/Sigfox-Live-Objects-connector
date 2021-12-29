/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sigfox2lo.lo;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.orange.lo.sdk.LOApiClient;
import com.orange.lo.sdk.LOApiClientParameters;
import com.orange.lo.sdk.mqtt.exceptions.LoMqttException;
import com.orange.lo.sdk.rest.model.Device;
import com.orange.lo.sdk.rest.model.Group;

import net.jodah.failsafe.RetryPolicy;

@Configuration
public class LoConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final LoProperties loProperties;

    public LoConfig(LoProperties loProperties) {
		this.loProperties = loProperties;
	}

    @Bean
    public RetryPolicy<List<Device>> restDevicesRetryPolicy() {
    	return new RetryPolicy<List<Device>>() 
				.handleIf(e -> e instanceof HttpClientErrorException && ((HttpClientErrorException) e).getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) //
				.withMaxAttempts(-1) //
				.withBackoff(1, 60, ChronoUnit.SECONDS) //
				.withMaxDuration(Duration.ofHours(1)); //
    }
    
    @Bean
    public RetryPolicy<Group> restGroupRetryPolicy() {
    	return new RetryPolicy<Group>() 
				.handleIf(e -> e instanceof HttpClientErrorException && ((HttpClientErrorException) e).getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) //
				.withMaxAttempts(-1) //
				.withBackoff(1, 60, ChronoUnit.SECONDS) //
				.withMaxDuration(Duration.ofHours(1)); //
    }
    
    @Bean
    public RetryPolicy<Void> mqttRetryPolicy() {
		return new RetryPolicy<Void>() 				
				.handleIf(e -> e instanceof LoMqttException) //
				.withMaxAttempts(-1) //
				.withBackoff(1, 60, ChronoUnit.SECONDS) //
				.withMaxDuration(Duration.ofHours(1)); //
	}

    @Bean
    public LOApiClient loApiClient() {
        LOGGER.debug("Initializing LOApiClient");
        LOApiClientParameters parameters = loApiClientParameters();
        LOApiClient loApiClient = new LOApiClient(parameters);
        loApiClient.getDataManagementExtConnector().connect();
        return loApiClient;
    }

    LOApiClientParameters loApiClientParameters() {
        return LOApiClientParameters.builder()
                .hostname(loProperties.getHostname())
                .apiKey(loProperties.getApiKey())
                .automaticReconnect(true)
                .mqttPersistenceDataDir(loProperties.getMqttPersistenceDir())
                .connectorType(loProperties.getConnectorType())
                .connectorVersion(getConnectorVersion())
                .build();
    }
    
    private String getConnectorVersion() {
    	MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = null;
        try {			
	        if ((new File("pom.xml")).exists()) {
	          model = reader.read(new FileReader("pom.xml"));
	        } else {
	          model = reader.read(
	            new InputStreamReader(
	            	LoConfig.class.getResourceAsStream(
	                "/META-INF/maven/com.orange.lo.sample.sigfox/sigfox2lo/pom.xml"
	              )
	            )
	          );
	        }
	        return model.getVersion().replace(".", "_");
        } catch (Exception e) {
			return "";
		}
    }
}