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

import mockit.*;
import org.aspectj.lang.JoinPoint;
import org.example.common.model.UserInfoModel;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

public class ControllerLoggingAdviceTest {

    @Tested
    ControllerLoggingAdvice controllerLoggingAdvice;

    @Injectable
    JoinPoint joinPoint;

    @Test
    public void testLogBefore() {
        new Expectations() {{
            joinPoint.getArgs();
            result = new UserInfoModel();
            joinPoint.getSignature().getName();
            result = "methodName";
            MDC.get("requestId");
            result = "requestIdValue";
        }};
        controllerLoggingAdvice.logBefore(joinPoint);
    }

    @Test
    public void testLogAfter() {
        UserInfoModel userInfoModel = new UserInfoModel();
        new Expectations() {{
            joinPoint.getSignature().getName();
            result = "methodName";
            MDC.get("requestId");
            result = "requestIdValue";
        }};
        controllerLoggingAdvice.logAfter(joinPoint, userInfoModel);
    }
}
