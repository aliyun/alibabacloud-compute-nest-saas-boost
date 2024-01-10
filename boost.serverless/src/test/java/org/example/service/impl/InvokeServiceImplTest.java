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

package org.example.service.impl;

import org.example.common.adapter.AdapterManager;
import org.example.service.OrderFcService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

class InvokeServiceImplTest {

    @Mock
    private OrderFcService orderFcService;

    @Mock
    private AdapterManager adapterManagerFcService;

    @InjectMocks
    private InvokeServiceImpl invokeServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testInvoke() throws Exception {
        String payload = "{\n" +
                "  \"payload\":\"REFUND_ORDERS\"\n" +
                "}";
        String result = invokeServiceImpl.invoke(new HashMap<String, String>() {{
            put("x-fc-access-key-id", "String");
            put("x-fc-access-key-secret", "String");
            put("x-fc-security-token", "String");
        }}, payload);
        Assertions.assertEquals("success", result);
        payload = "{\n" +
                "  \"payload\":\"CLOSE_EXPIRED_ORDERS\"\n" +
                "}";
        result = invokeServiceImpl.invoke(new HashMap<String, String>() {{
            put("x-fc-access-key-id", "String");
            put("x-fc-access-key-secret", "String");
            put("x-fc-security-token", "String");
        }}, payload);
        Assertions.assertEquals("success", result);

        payload = "{\n" +
                "  \"payload\":\"DELETE_SERVICE_INSTANCES\"\n" +
                "}";
        result = invokeServiceImpl.invoke(new HashMap<String, String>() {{
            put("String", "String");
        }}, payload);
        Assertions.assertEquals("success", result);
    }
}
