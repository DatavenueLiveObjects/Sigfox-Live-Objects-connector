/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sigfox2lo;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ThreadPoolExecutor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;

import com.orange.lo.sample.sigfox2lo.lo.LoProperties;

@ExtendWith(MockitoExtension.class)
class Sigfox2loApplicationConfigTest {

    @Mock
    private LoProperties loProperties;
    @Mock
    private AsyncSupportConfigurer asyncSupportConfigurer;
    private Sigfox2loApplicationConfig sigfox2loApplicationConfig;

    @BeforeEach
    void setUp() {
        sigfox2loApplicationConfig = new Sigfox2loApplicationConfig(loProperties);
    }

    @Test
    void shouldCreateSynchronizingExecutorCorrectly() {
        when(loProperties.getSynchronizationThreadPoolSize()).thenReturn(10);

        ThreadPoolExecutor threadPoolExecutor = sigfox2loApplicationConfig.synchronizingExecutor();

        assertNotNull(threadPoolExecutor);
        verify(loProperties, times(2)).getSynchronizationThreadPoolSize();
    }
}