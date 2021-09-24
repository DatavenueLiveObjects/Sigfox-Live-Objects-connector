/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sigfox2lo.lo;

import com.orange.lo.sdk.LOApiClientParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LoConfigTest {

    public static final String API_KEY = "abcDEfgH123I";
    public static final String URI = "liveobjects.orange-business.com";

    private LoProperties loPropertiesStub;
    private LoConfig loConfig;

    @BeforeEach
    void setUp() {
        this.loPropertiesStub = new LoProperties();
        this.loPropertiesStub.setApiKey(API_KEY);
        this.loPropertiesStub.setHostname(URI);
        this.loConfig = new LoConfig(loPropertiesStub);
    }

    @Test
    void shouldCorrectlyCreateApiClientParameters() {
        LOApiClientParameters parameters = loConfig.loApiClientParameters();

        assertNotNull(parameters);
        assertEquals(loPropertiesStub.getApiKey(), parameters.getApiKey());
        assertEquals(loPropertiesStub.getHostname(), parameters.getHostname());
    }
}