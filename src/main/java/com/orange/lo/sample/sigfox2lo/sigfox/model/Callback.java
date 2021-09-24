/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sigfox2lo.sigfox.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Callback {

    private DataUpDto body;

    public DataUpDto getBody() {
    	return body;
    }
    
    public void setBody(DataUpDto body) {
    	this.body = body;
    }
    	
    @Override
    public String toString() {
        return "Callback{" +
                "body=" + body +
                '}';
    }
}
