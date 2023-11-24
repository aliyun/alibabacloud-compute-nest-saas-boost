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

package org.example.common;

import org.example.common.errorinfo.ErrorInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MDC.class, HttpStatus.class})
public class BaseResultTest {

    @Test
    public void testSuccess() {
        BaseResult<String> result = BaseResult.success("Data");

        Assert.assertEquals(HttpStatus.OK.getReasonPhrase(), result.getMessage());
        Assert.assertEquals("Data", result.getData());
    }

    @Test
    public void testFailWithMessage() {
        BaseResult<String> result = BaseResult.fail("Error message");

        Assert.assertEquals(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), result.getCode());
        Assert.assertEquals("Error message", result.getMessage());
    }

    @Test
    public void testFailWithCommonErrorInfo() {
        BaseResult<String> result = BaseResult.fail(ErrorInfo.SERVER_UNAVAILABLE);
        Assert.assertEquals("ServerUnavailable", result.getCode());
        Assert.assertEquals("Server is unavailable.", result.getMessage());
    }

    @Test
    public void testConstructorWithHttpStatus() {
        BaseResult<String> result = new BaseResult<>(HttpStatus.OK);
        Assert.assertEquals("200", result.getCode());
        Assert.assertEquals("OK", result.getMessage());
    }

    @Test
    public void testNoArgsConstructor() {
        BaseResult<String> result = new BaseResult<>();
        Assert.assertEquals("200", result.getCode());
    }

    @Test
    public void testArgsConstructor() {
        BaseResult<String> result = new BaseResult<>("200", "msg", null);
        Assert.assertNull(result.requestId);
        Assert.assertEquals("200", result.getCode());
    }

    @Test
    public void testSuccessWithNull() {
        BaseResult<String> result = BaseResult.success();
        result.setRequestId("test");
        Assert.assertNotNull(result.requestId);
        Assert.assertEquals("200", result.getCode());
    }
}
