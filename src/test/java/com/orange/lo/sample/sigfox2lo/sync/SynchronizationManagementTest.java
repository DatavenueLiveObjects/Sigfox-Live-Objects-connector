/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sigfox2lo.sync;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SynchronizationManagementTest {

    @Mock
    private Synchronization synchronizationOne;
    @Mock
    private Synchronization synchronizationTwo;

    @Test
    void shouldInvokeAllSynchronizationsWhenSynchronizationIsRunning() {
        List<Synchronization> synchronizationComponents = Arrays.asList(synchronizationOne, synchronizationTwo);
        SynchronizationManagement synchronizationManagement = new SynchronizationManagement(synchronizationComponents);
        synchronizationManagement.synchronize();

        for (Synchronization synchronization : synchronizationComponents) {
            verify(synchronization, times(1)).synchronize();
        }
    }
}