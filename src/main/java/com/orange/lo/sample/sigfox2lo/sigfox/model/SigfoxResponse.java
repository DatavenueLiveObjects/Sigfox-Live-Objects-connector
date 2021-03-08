package com.orange.lo.sample.sigfox2lo.sigfox.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SigfoxResponse <T> {

	private List<T> data;
	private List<String> actions;
	private SigfoxPaging paging;
	
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
	public List<String> getActions() {
		return actions;
	}
	public void setActions(List<String> actions) {
		this.actions = actions;
	}
	public SigfoxPaging getPaging() {
		return paging;
	}
	public void setPaging(SigfoxPaging paging) {
		this.paging = paging;
	}
}
