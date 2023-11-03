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

package org.example.common.aspect;

import com.alicloud.openservices.tablestore.ClientException;
import com.alicloud.openservices.tablestore.TableStoreException;
import com.alipay.api.AlipayApiException;
import org.example.common.BaseResult;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.lang.reflect.Constructor;

class ExceptionHandlerAdviceTest {

    @InjectMocks
    ExceptionHandlerAdvice exceptionHandlerAdvice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testBadRequestException() {
        BaseResult<String> result = exceptionHandlerAdvice.badRequestException(new Exception());
        Assertions.assertTrue(result.getCode().equals("InvalidInput"));
    }

    @Test
    void testBadRequestExceptionInvalidInput() {
        BaseResult<String> result = exceptionHandlerAdvice.badRequestException(new Exception());
        Assertions.assertTrue(result.getCode().equals("InvalidInput"));
    }

    @Test
    void testIllegalArgumentException() {
        BaseResult<String> result = exceptionHandlerAdvice.badRequestException(new IllegalArgumentException());
        Assertions.assertTrue(result.getCode().equals("InvalidInput"));
    }

    @Test
    void testAliPayApiException() {
        BaseResult<String> result = exceptionHandlerAdvice.badRequestException(new AlipayApiException());
        Assertions.assertTrue(result.getCode().equals("400"));
    }

    @Test
    void testHandlerMethodArgumentNotValidException() {
        BaseResult<String> result = exceptionHandlerAdvice.handlerMethodArgumentNotValidException(null);
        Assertions.assertTrue(result.getCode().equals("InvalidInput"));
    }

    @Test
    void testHandleTypeMismatchException() {
        BaseResult<String> result = exceptionHandlerAdvice.handleTypeMismatchException(null);
        Assertions.assertTrue(result.getCode().equals("500"));
    }

    @Test
    void testHandleUnexpectedServer() {
        BaseResult<String> result = exceptionHandlerAdvice.handleUnexpectedServer(null);
        Assertions.assertTrue(result.getCode().equals("500"));
    }

    @Test
    void testException() {
        BaseResult<String> result = exceptionHandlerAdvice.exception(null);
        Assertions.assertTrue(result.getCode().equals("200"));
    }

    @Test
    void testBizExceptionHandler() {
        ResponseEntity<BaseResult<String>> result = exceptionHandlerAdvice.bizExceptionHandler(new BizException(ErrorInfo.SERVER_UNAVAILABLE));
        Assertions.assertNotNull(result);
    }

    @Test
    void testTableStoreClientExceptionHandler() {
        ResponseEntity<BaseResult<String>> result = exceptionHandlerAdvice.tableStoreClientExceptionHandler(new ClientException("test", "test"));
        Assertions.assertNotNull(result);
    }

    @Test
    void testTableStoreServerExceptionHandler() {
        ResponseEntity<BaseResult<String>> result = exceptionHandlerAdvice.tableStoreServerExceptionHandler(new TableStoreException("test", "test"));
        Assertions.assertNotNull(result);
    }

    @Test
    void testAccesionDenied() {
        BaseResult<String> result = exceptionHandlerAdvice.badRequestException(new AccessDeniedException("test"));
        Assertions.assertNotNull(result);
    }

    @Test
    void testMissingServletRequestParameterException() {
        BaseResult<String> result = exceptionHandlerAdvice.badRequestException(new MissingServletRequestParameterException("test", "test"));
        Assertions.assertNotNull(result);
    }

    @Test
    void testMissingRequestHeaderException() throws NoSuchMethodException {
        Constructor<String> constructor = String.class.getConstructor();
        MethodParameter methodParameter = new MethodParameter(constructor, -1, -1);
        BaseResult<String> result = exceptionHandlerAdvice.badRequestException(new MissingRequestHeaderException("testHeader", methodParameter));
        Assertions.assertNotNull(result);
    }
}
