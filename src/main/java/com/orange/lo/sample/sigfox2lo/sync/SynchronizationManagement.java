package com.orange.lo.sample.sigfox2lo.sync;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class SynchronizationManagement {

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private List<Synchronization> synchronizationComponents;
	
	public SynchronizationManagement(List<Synchronization> synchronizationComponents) {
		this.synchronizationComponents = synchronizationComponents;
	}
	
	@Scheduled(fixedRateString = "${lo.synchronization-interval}")
    public void synchronize() {  
		LOG.info("Synchronization in progress... ");
		synchronizationComponents.forEach(Synchronization::synchronize);
		LOG.info("Synchronization in done... ");
	}
}