/*
*Copyright (c) Alibaba Group;
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at

*   http://www.apache.org/licenses/LICENSE-2.0

*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/

package org.example.common.exception;

import org.example.common.errorinfo.CommonErrorInfo;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BizExceptionTest {

    @Test
    public void testConstructorWithCommonErrorInfo() {
        CommonErrorInfo commonErrorInfo = mock(CommonErrorInfo.class);
        when(commonErrorInfo.getStatusCode()).thenReturn(200);
        when(commonErrorInfo.getCode()).thenReturn("code");
        when(commonErrorInfo.getMessage()).thenReturn("message");
        BizException actualException = new BizException(commonErrorInfo);

        assertEquals(200, actualException.getStatusCode());
        assertEquals("code", actualException.getCode());
        assertEquals("message", actualException.getMessage());
    }

    @Test
    public void testConstructorWithCommonErrorInfoAndThrowable() {
        CommonErrorInfo commonErrorInfo = mock(CommonErrorInfo.class);
        when(commonErrorInfo.getStatusCode()).thenReturn(200);
        when(commonErrorInfo.getCode()).thenReturn("code");
        when(commonErrorInfo.getMessage()).thenReturn("message");
        Throwable cause = mock(Throwable.class);
        BizException actualException = new BizException(commonErrorInfo, cause);

        assertEquals(200, actualException.getStatusCode());
        assertEquals("code", actualException.getCode());
        assertEquals("message", actualException.getMessage());
    }

    @Test
    public void testAllArgsConstructor() {
        BizException actualException = new BizException(200, "code", "message");
        assertEquals(200, actualException.getStatusCode());
        assertEquals("code", actualException.getCode());
        assertEquals("message", actualException.getMessage());

        Throwable cause = mock(Throwable.class);
        actualException = new BizException(200, "code", "message", cause);
        assertEquals(200, actualException.getStatusCode());
        assertEquals("code", actualException.getCode());
        assertEquals("message", actualException.getMessage());
    }

    @Test
    public void testNoArgsConstructor() {
        BizException actualException = new BizException();
        Assertions.assertNotNull(actualException);
    }
}

