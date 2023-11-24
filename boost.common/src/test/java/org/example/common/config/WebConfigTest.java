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

package org.example.common.config;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import org.example.common.handler.RequestIdInterceptor;
import org.example.common.handler.TokenAuthenticationInterceptor;
import org.example.common.helper.TokenParseHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.util.ArrayList;
import java.util.List;


public class WebConfigTest {

    @Injectable
    private TokenAuthenticationInterceptor tokenAuthenticationInterceptor;

    @Injectable
    private RequestIdInterceptor requestIdInterceptor;

    private InterceptorRegistry interceptorRegistryMock;

    @Mocked
    private InterceptorRegistration interceptorRegistrationMock;

    @Tested
    private WebConfig webConfig;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(tokenAuthenticationInterceptor, "tokenParseHelper", new TokenParseHelper());
        interceptorRegistryMock = new InterceptorRegistry();
        ReflectionTestUtils.setField(interceptorRegistryMock, "registrations", new ArrayList<>());
    }

    @Test
    public void testAddArgumentResolvers() {
        List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>();
        new Expectations() {{
            argumentResolvers.add(withAny(new AuthenticationPrincipalArgumentResolver()));
        }};
        Assertions.assertDoesNotThrow(() -> webConfig.addArgumentResolvers(argumentResolvers));
    }

    @Test
    public void testAddInterceptors(@Mocked InterceptorRegistry registry) {
        new Expectations() {{
            registry.addInterceptor(requestIdInterceptor)
                    .addPathPatterns("/api/**");
            registry.addInterceptor(tokenAuthenticationInterceptor)
                    .addPathPatterns("/api/**")
                    .excludePathPatterns("/api/getAuthToken");
        }};
        Assertions.assertDoesNotThrow(() -> webConfig.addInterceptors(interceptorRegistryMock));
    }
}
