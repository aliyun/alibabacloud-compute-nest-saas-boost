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

import mockit.Expectations;
import mockit.Mocked;
import org.example.common.adapter.BaseAlipayClient;
import org.example.common.adapter.CloudMonitorClient;
import org.example.common.adapter.ComputeNestSupplierClient;
import org.example.common.adapter.OosClient;
import org.example.common.adapter.OtsClient;
import org.example.common.config.AlipayConfig;
import org.example.common.config.AliyunConfig;
import org.example.common.config.OosSecretParamConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.ApplicationArguments;

import static org.mockito.Mockito.verify;

public class AdapterManagerServiceImplTest {

    @Mock
    private AliyunConfig aliyunConfig;

    @Mock
    private AlipayConfig alipayConfig;

    @Mock
    private OosClient oosClient;

    @Mock
    private OtsClient otsClient;

    @Mock
    private CloudMonitorClient cloudMonitorClient;

    @Mock
    private ComputeNestSupplierClient computeNestSupplierClient;

    @Mock
    private BaseAlipayClient baseAlipayClient;

    @Mock
    private OosSecretParamConfig oosSecretParamConfig;

    @InjectMocks
    private AdapterManagerServiceImpl adapterManagerService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testClientInjection() throws Exception {
        adapterManagerService.clientInjection();

        verify(oosClient).createClient(aliyunConfig);
        verify(oosSecretParamConfig).init();
        verify(otsClient).createClient(aliyunConfig);
        verify(cloudMonitorClient).createClient(aliyunConfig);
        verify(computeNestSupplierClient).createClient(aliyunConfig);
        verify(baseAlipayClient).createClient(alipayConfig);
    }

    @Test
    public void testRun(@Mocked ApplicationArguments applicationArguments) throws Exception {
        new Expectations(){{
            adapterManagerService.run(applicationArguments);
        }};
        Assertions.assertDoesNotThrow(()->adapterManagerService.run(applicationArguments));
    }
}
