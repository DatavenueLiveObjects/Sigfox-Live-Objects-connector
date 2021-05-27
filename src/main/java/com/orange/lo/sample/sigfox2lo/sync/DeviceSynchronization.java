package com.orange.lo.sample.sigfox2lo.sync;

import static com.orange.lo.sdk.rest.devicemanagement.Inventory.XCONNECTOR_DEVICES_PREFIX;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.orange.lo.sample.sigfox2lo.lo.LoService;
import com.orange.lo.sample.sigfox2lo.sigfox.SigfoxService;
import com.orange.lo.sample.sigfox2lo.sigfox.model.Device;

@Component
@Order(1)
public class DeviceSynchronization implements Synchronization {

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private SigfoxService sigfoxService;
	private LoService loService;
	private ThreadPoolExecutor synchronizingExecutor;
	
	public DeviceSynchronization(SigfoxService sigfoxService, LoService loService, ThreadPoolExecutor synchronizingExecutor) {
		this.sigfoxService = sigfoxService;
		this.loService = loService;
		this.synchronizingExecutor = synchronizingExecutor;		
	}
	
	@Override
	public void synchronize() {
		LOG.info("Synchronizing devices ");
    	
    	try {
    		Map<String, Device> sigFoxDevices = sigfoxService.getDevices().stream().collect(Collectors.toMap(Device::getId, Function.identity()));
        	LOG.debug("Got {} devices from SigFox", sigFoxDevices.size());
        	
        	Set<String> loDeviceIds = loService.getDevices().stream()
					.map(com.orange.lo.sdk.rest.model.Device::getId)
					.map(id -> id.substring(XCONNECTOR_DEVICES_PREFIX.length()))
					.collect(Collectors.toSet());
        	loService.addDevicesToCache(loDeviceIds);
        	LOG.debug("Got {} devices from Live Objects", loDeviceIds.size());
        	
        	// remove devices from LO
        	Set<String> devicesToRemoveFromLo = new HashSet<>(loDeviceIds);
        	devicesToRemoveFromLo.removeAll(sigFoxDevices.keySet());
			if (LOG.isDebugEnabled()) {
				String input = Arrays.toString(devicesToRemoveFromLo.toArray());
				LOG.debug("Devices to remove from LO: {}", StringEscapeUtils.escapeHtml4(input));
			}

        	// add devices to LO
        	sigFoxDevices.keySet().removeAll(loDeviceIds);
            LOG.debug("Devices to add to LO: {}", sigFoxDevices.values());
            
            
            Set<CreateDeviceTask> createDeviceTasks = sigFoxDevices.values().stream()
					.map(d -> new CreateDeviceTask(loService, d.getId(), d.getName()))
					.collect(Collectors.toSet());
            synchronizingExecutor.invokeAll(createDeviceTasks);

            Set<DeleteDeviceTask> deleteDeviceTasks = devicesToRemoveFromLo.stream()
					.map(id -> new DeleteDeviceTask(loService, id))
					.collect(Collectors.toSet());
            synchronizingExecutor.invokeAll(deleteDeviceTasks);
            
            LOG.info("Synchronizing devices Done");

        } catch (Exception e) {
            LOG.error("Error in device synchronization process", e);
        }
	}
}
