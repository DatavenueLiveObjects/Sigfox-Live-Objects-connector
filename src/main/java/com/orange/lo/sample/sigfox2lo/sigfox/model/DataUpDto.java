/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sigfox2lo.sigfox.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataUpDto {

	@JsonIgnore
	private Long id;
    private String device;
    private String deviceTypeId;
    private Long seqNumber;
    private Long time;
    private String data;
    
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getDeviceTypeId() {
		return deviceTypeId;
	}
	public void setDeviceTypeId(String deviceTypeId) {
		this.deviceTypeId = deviceTypeId;
	}
	public Long getSeqNumber() {
		return seqNumber;
	}
	public void setSeqNumber(Long seqNumber) {
		this.seqNumber = seqNumber;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return "DataUpDto [id=" + id + ", device=" + device + ", deviceTypeId=" + deviceTypeId + ", seqNumber="
				+ seqNumber + ", time=" + time + ", data=" + data + "]";
	}
}
