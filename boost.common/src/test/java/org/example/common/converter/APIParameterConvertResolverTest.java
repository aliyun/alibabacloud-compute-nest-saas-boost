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

package org.example.common.converter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author liuyuhang
 * date on 2023/9/27.
 */
import org.example.common.APIParameterConvert;
import org.example.common.param.GetAuthTokenParam;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.support.DefaultDataBinderFactory;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

public class APIParameterConvertResolverTest {

    private ApiParameterConvertResolver resolver = new ApiParameterConvertResolver();

    @Test
    public void testSupportsParameter() throws NoSuchMethodException {
        MethodParameter parameter = new MethodParameter(TestController.class.getMethod("testMethod", GetAuthTokenParam.class), 0);
        boolean result = resolver.supportsParameter(parameter);
        assertTrue(result);
    }

    @Test
    public void testResolveArgumentReturnConvertedObject() throws Exception {
        MethodParameter parameter = new MethodParameter(TestController.class.getMethod("testMethod", GetAuthTokenParam.class), 0);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("session_state", "test_value");
        NativeWebRequest webRequest = new ServletWebRequest(request);
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        WebDataBinderFactory binderFactory = new DefaultDataBinderFactory(null);
        GetAuthTokenParam result = (GetAuthTokenParam) resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        assertNotNull(result);
        assertEquals("test_value", result.getSessionState());
    }

    static class TestController {
        public void testMethod(@APIParameterConvert GetAuthTokenParam param) {
        }
    }
}
