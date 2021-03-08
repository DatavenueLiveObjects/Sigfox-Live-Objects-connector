package com.orange.lo.sample.sigfox2lo.sync;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.orange.lo.sample.sigfox2lo.lo.LoProperties;

@Component
@Order(2)
public class RemoveOldMessages implements Synchronization{

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private SyncService syncService;
	private LoProperties loProperties;
	
	public RemoveOldMessages(SyncService syncService, LoProperties loProperties) {
		this.syncService = syncService;
		this.loProperties = loProperties;
	}
	
	@Override
	public void synchronize() {
		LOG.info("Removing old messages from database");
		Instant date = Instant.now().minus(loProperties.getMessageRetryDays(), ChronoUnit.DAYS);
		int amount = syncService.removeMessagesOlderThan(date.getEpochSecond());
		LOG.info("Removed {} old messages from database", amount);
	}
}
