/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sigfox2lo.sigfox.model;

public class ErrorInfo {
 
	public final String message;
    public final long timestamp;

    public ErrorInfo(String message) {
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
}