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
import mockit.Mocked;
import mockit.Tested;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;

class WebSecurityConfigTest {

    @Tested
    private WebSecurityConfig webSecurityConfig;

    @Test
    void sessionAuthenticationStrategy() {
        RegisterSessionAuthenticationStrategy registerSessionAuthenticationStrategy = new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
        new Expectations(){{
           webSecurityConfig.sessionAuthenticationStrategy();
           result = registerSessionAuthenticationStrategy;
        }};
        Assertions.assertDoesNotThrow(()-> webSecurityConfig.sessionAuthenticationStrategy());
    }

    @Test
    void configure(@Mocked WebSecurity web) {
        Assertions.assertDoesNotThrow(()-> webSecurityConfig.configure(web));
    }

    @Test
    void testConfigureHttp(@Mocked HttpSecurity http) {
        Assertions.assertDoesNotThrow(()-> webSecurityConfig.configure(http));
    }
}
