/**
 * Copyright (c) Orange. All Rights Reserved.
 * <p>
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.orange.lo.sample.sigfox2lo;

import com.orange.lo.sample.sigfox2lo.sigfox.model.ErrorInfo;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class Sigfox2loErrorControllerTest {

    @Test
    void shouldCorrectlyReturnInformationWhenExceptionHasBeenThrown() {
        String exceptionMessage = "This is exception message";
        RuntimeException runtimeException = new RuntimeException(exceptionMessage);

        Sigfox2loErrorController sigfox2loErrorController = new Sigfox2loErrorController();
        ResponseEntity<ErrorInfo> errorInfoResponseEntity = sigfox2loErrorController.handleException(runtimeException);
        ErrorInfo body = errorInfoResponseEntity.getBody();

        assertEquals(HttpStatus.NOT_FOUND, errorInfoResponseEntity.getStatusCode());
        assertEquals(exceptionMessage, body.message);
    }
}