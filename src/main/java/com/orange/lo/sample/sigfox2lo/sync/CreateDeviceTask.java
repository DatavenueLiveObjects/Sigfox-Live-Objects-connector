/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sigfox2lo.sync;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orange.lo.sample.sigfox2lo.lo.LoService;

public class CreateDeviceTask implements Callable<Void>{

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private LoService loService;
	private String deviceId;
	private String deviceName;

	public CreateDeviceTask(LoService loService, String deviceId, String deviceName) {
		this.loService = loService;
		this.deviceId = deviceId;
		this.deviceName = deviceName;
	}	
	
	@Override
	public Void call() throws Exception {
		try {
			loService.createDevice(deviceId, deviceName);
			loService.sendStatus(deviceId);
			LOG.debug("Device created for {}", deviceId);
		} catch (Exception e) {
			LOG.error("Cannot create device {} because of {}", deviceId, e.getMessage());
		}
		return null;			
	}
}
