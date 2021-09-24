/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sigfox2lo.sigfox;

import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SigfoxConfig {

	private SigfoxProperties properties;

	public SigfoxConfig(SigfoxProperties properties) {
		this.properties = properties;
	}
	
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();        
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(properties.getLogin(), properties.getPassword(), StandardCharsets.UTF_8));
        return restTemplate;
	}	
}
