package com.orange.lo.sample.sigfox2lo.sigfox.model;

public class ErrorInfo {
 
	public final String message;
    public final long timestamp;

    public ErrorInfo(String message) {
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
}