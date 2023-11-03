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

import org.example.common.adapter.BaseAlipayClient;
import org.example.common.adapter.ComputeNestSupplierClient;
import org.example.common.adapter.OosClient;
import org.example.common.adapter.OtsClient;
import org.example.common.config.AlipayConfig;
import org.example.common.config.OosSecretParamConfig;
import org.example.common.constant.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AdapterManagerFcServiceImplTest {

    @Mock
    private AlipayConfig alipayConfig;

    @Mock
    private OosClient oosClient;

    @Mock
    private OtsClient otsClient;

    @Mock
    private ComputeNestSupplierClient computeNestSupplierClient;

    @Mock
    private BaseAlipayClient baseAlipayClient;

    @Mock
    private OosSecretParamConfig oosSecretParamConfig;

    @InjectMocks
    private AdapterManagerFcServiceImpl adapterManagerFcService;

    private Map<String, String> headers;

    @Before
    public void setup() {
        headers = new HashMap<>();
        headers.put(Constants.FC_ACCESS_KEY_ID, "test_access_key_id");
        headers.put(Constants.FC_ACCESS_KEY_SECRET, "test_access_key_secret");
        headers.put(Constants.FC_SECURITY_TOKEN, "test_security_token");
    }

    @Test
    public void testClientInjection() throws Exception {
        adapterManagerFcService.clientInjection(headers);
        verify(oosClient, times(1)).createClient(anyString(), anyString(), anyString());
        verify(otsClient, times(1)).createClient(anyString(), anyString(), anyString());
        verify(computeNestSupplierClient, times(1)).createClient(anyString(), anyString(), anyString());
        verify(baseAlipayClient, times(1)).createClient(alipayConfig);
    }
}
