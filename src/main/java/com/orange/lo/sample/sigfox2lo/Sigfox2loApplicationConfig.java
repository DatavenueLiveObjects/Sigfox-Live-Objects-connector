package com.orange.lo.sample.sigfox2lo;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.orange.lo.sample.sigfox2lo.lo.LoProperties;
import org.springframework.util.DefaultPropertiesPersister;

@Configuration
public class Sigfox2loApplicationConfig {

	private LoProperties loProperties;

    public Sigfox2loApplicationConfig(LoProperties loProperties) {
        this.loProperties = loProperties;
    }

    @Bean
    ThreadPoolExecutor synchronizingExecutor() {
    	return new ThreadPoolExecutor(loProperties.getSynchronizationThreadPoolSize(), 
    								  loProperties.getSynchronizationThreadPoolSize(), 
    								  10, 
    								  TimeUnit.SECONDS, 
    								  new LinkedBlockingQueue<>());
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public DefaultPropertiesPersister defaultPropertiesPersister() {
        return new DefaultPropertiesPersister();
    }
}
