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

import mockit.Injectable;
import mockit.Tested;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BoostAlipayConfigTest {

    @Tested
    private BoostAlipayConfig boostAlipayConfig;

    @Injectable
    private OosParamConfig oosSecretParamConfig;

    @Test
    public void testAlipayConfig() {
        String gateway = "test.gateway";
        String pid = "test.pid";
        String returnUrl = "test.returnUrl";
        String notifyUrl = "test.notifyUrl";

        boostAlipayConfig.setReturnUrl(returnUrl);

        assertEquals(returnUrl, boostAlipayConfig.getReturnUrl());
    }
}
