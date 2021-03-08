package com.orange.lo.sample.sigfox2lo.sync;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.orange.lo.sample.sigfox2lo.lo.LoDeviceCache;
import com.orange.lo.sample.sigfox2lo.lo.LoService;
import com.orange.lo.sample.sigfox2lo.sigfox.SigfoxService;
import com.orange.lo.sample.sigfox2lo.sigfox.model.Callback;
import com.orange.lo.sample.sigfox2lo.sigfox.model.UndeliveredCallback;

@Component
@Order(3)
public class MessageSynchronization implements Synchronization {

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private SigfoxService sigfoxService;
	private LoService loService;
	private ThreadPoolExecutor synchronizingExecutor;
	private LoDeviceCache loDeviceCache;
	private SyncService syncService;

	public MessageSynchronization(LoDeviceCache loDeviceCache, SigfoxService sigfoxService, LoService loService, SyncService syncService,
			ThreadPoolExecutor synchronizingExecutor) {
		this.loDeviceCache = loDeviceCache;
		this.sigfoxService = sigfoxService;
		this.loService = loService;
		this.syncService = syncService;
		this.synchronizingExecutor = synchronizingExecutor;

	}

	@Override
	public void synchronize() {
		LOG.info("Synchronizing messages ");
		try {
			Collection<String> ids = loDeviceCache.getAll();
			long currentTimeMillis = System.currentTimeMillis();

			Set<SendMessageTask> callbackStream = ids.stream() //
					.map(id -> sigfoxService.getUndeliveredCallbacks(id, syncService.getLastSyncTime())) //
					.flatMap(Collection::stream) //
					.map(UndeliveredCallback::getCallback) //
					.map(Callback::getBody) //
					.map(body -> new SendMessageTask(loService, syncService, body))
					.collect(Collectors.toSet()); //

			LOG.debug("Got {} undeliwered messages from Sigfox", callbackStream.size());

            Set<SendMessageTask> databaseMessage = syncService.getMessages().stream()
                    .map(m -> new SendMessageTask(loService, syncService, m))
                    .collect(Collectors.toSet());

			LOG.debug("Got {} undelivered messages from database", databaseMessage.size());

			Set<SendMessageTask> tasks = Stream.of(callbackStream, databaseMessage)
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());
			LOG.debug("Trying to send {} messages", tasks.size());

			synchronizingExecutor.invokeAll(tasks);

			syncService.setLastSyncTime(currentTimeMillis);
		} catch (HttpClientErrorException e) {
            LOG.error("Error in message synchronization process \n {}", e.getResponseBodyAsString());
        } catch (Exception e) {
            LOG.error("Error in message synchronization process", e);
        }
		LOG.info("Synchronizing messages Done");
	}
}
