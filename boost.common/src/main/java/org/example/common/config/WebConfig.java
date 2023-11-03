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


import org.example.common.handler.RequestIdInterceptor;
import org.example.common.handler.TokenAuthenticationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private TokenAuthenticationInterceptor tokenAuthenticationInterceptor;

    @Resource
    private RequestIdInterceptor requestIdInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestIdInterceptor)
                .addPathPatterns("/api/**");
        registry.addInterceptor(tokenAuthenticationInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/getAuthToken");
    }
}
