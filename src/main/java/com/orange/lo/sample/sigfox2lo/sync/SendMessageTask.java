package com.orange.lo.sample.sigfox2lo.sync;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orange.lo.sample.sigfox2lo.lo.LoService;
import com.orange.lo.sample.sigfox2lo.sigfox.model.DataUpDto;

public class SendMessageTask implements Callable<Void>{

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private LoService loService;
	private DataUpDto dataUpDto;
	private SyncService syncService;

	public SendMessageTask(LoService loService, SyncService syncService, DataUpDto dataUpDto) {
		this.loService = loService;
		this.syncService = syncService;
		this.dataUpDto = dataUpDto;
	}
	
	@Override
	public Void call() throws Exception {
		try {
			LOG.debug("Trying send message for {}", dataUpDto.getDevice());
			loService.sendMessage(dataUpDto);
			syncService.removeMessage(dataUpDto.getId());
			LOG.debug("Message sent for {}", dataUpDto.getDevice());
		} catch (Exception e) {
			LOG.error("Cannot send message, saving in dababase");
			syncService.saveMessage(dataUpDto);
		}
		return null;
	}
}
