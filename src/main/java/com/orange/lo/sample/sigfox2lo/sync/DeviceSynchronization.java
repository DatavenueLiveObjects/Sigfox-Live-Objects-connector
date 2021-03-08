package com.orange.lo.sample.sigfox2lo.sync;

import static com.orange.lo.sdk.rest.devicemanagement.Inventory.XCONNECTOR_DEVICES_PREFIX;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

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
    		Set<String> sigFoxDeviceIds = sigfoxService.getDevices().stream()
					.map(Device::getId)
					.collect(Collectors.toSet());
        	LOG.debug("Got {} devices from SigFox", sigFoxDeviceIds.size());
        	
        	Set<String> loDeviceIds = loService.getDevices().stream()
					.map(com.orange.lo.sdk.rest.model.Device::getId)
					.map(id -> id.substring(XCONNECTOR_DEVICES_PREFIX.length()))
					.collect(Collectors.toSet());
        	loService.addDevicesToCache(loDeviceIds);
        	LOG.debug("Got {} devices from Live Objects", loDeviceIds.size());
        	
        	// add devices to LO
            Set<String> devicesToAddToLo = new HashSet<>(sigFoxDeviceIds);
            devicesToAddToLo.removeAll(loDeviceIds);
            LOG.debug("Devices to add to LO: {}", devicesToAddToLo);
            
            // remove devices from LO
            Set<String> devicesToRemoveFromLo = new HashSet<>(loDeviceIds);
            devicesToRemoveFromLo.removeAll(sigFoxDeviceIds);
            LOG.debug("Devices to remove from LO: {}", devicesToRemoveFromLo);
            
            Set<CreateDeviceTask> createDeviceTasks = devicesToAddToLo.stream()
					.map(id -> new CreateDeviceTask(loService, id))
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
