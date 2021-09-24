/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sigfox2lo;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.orange.lo.sample.sigfox2lo.lo.LoService;
import com.orange.lo.sample.sigfox2lo.sigfox.model.DataUpDto;
import com.orange.lo.sample.sigfox2lo.sync.SendMessageTask;
import com.orange.lo.sample.sigfox2lo.sync.SyncService;

@RestController
public class Sigfox2loController {

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private LoService loService;
	private SyncService syncService;
	private ThreadPoolExecutor synchronizingExecutor;

	
	public Sigfox2loController(LoService loService, SyncService syncService, ThreadPoolExecutor synchronizingExecutor) {
		this.loService = loService;
		this.syncService = syncService;
		this.synchronizingExecutor = synchronizingExecutor;
	}
	
	@PostMapping("/dataUp")
    public ResponseEntity<Void> dataUp(@RequestBody DataUpDto dataUpDto) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("dataUp endpoint received {}", StringEscapeUtils.escapeHtml4(dataUpDto.toString()));
		}
		SendMessageTask sendMessageTask = new SendMessageTask(loService, syncService, dataUpDto);
		synchronizingExecutor.submit(sendMessageTask);
		return ResponseEntity.ok().build();
	}
}
