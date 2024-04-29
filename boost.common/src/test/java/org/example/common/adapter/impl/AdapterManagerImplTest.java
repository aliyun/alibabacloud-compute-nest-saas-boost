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

package org.example.common.adapter.impl;

import java.util.HashMap;
import java.util.Map;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.example.common.adapter.AcsApiCaller;
import org.example.common.adapter.BaseAlipayClient;
import org.example.common.adapter.CloudMonitorClient;
import org.example.common.adapter.ComputeNestSupplierClient;
import org.example.common.adapter.OosClient;
import org.example.common.adapter.OssClient;
import org.example.common.adapter.OtsClient;
import org.example.common.config.AliyunConfig;
import org.example.common.config.BoostAlipayConfig;
import org.example.common.config.OosParamConfig;
import org.example.common.constant.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.ApplicationArguments;
import org.springframework.test.util.ReflectionTestUtils;

class AdapterManagerImplTest {

    @Injectable
    private AliyunConfig aliyunConfig;

    @Injectable
    private BoostAlipayConfig boostAlipayConfig;

    @Injectable
    private OosClient oosClient;

    @Injectable
    private OtsClient otsClient;

    @Injectable
    private OssClient ossClient;

    @Injectable
    private CloudMonitorClient cloudMonitorClient;

    @Injectable
    private ComputeNestSupplierClient computeNestSupplierClient;

    @Injectable
    private BaseAlipayClient baseAlipayClient;

    @Injectable
    private OosParamConfig oosSecretParamConfig;

    @Injectable
    private String deployType;

    @Tested
    private AdapterManagerImpl adapterManager;

    @Injectable
    private AcsApiCaller acsApiCaller;

    private Map<String, String> headers;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        headers = new HashMap<>();
        headers.put(Constants.FC_ACCESS_KEY_ID, "test_access_key_id");
        headers.put(Constants.FC_ACCESS_KEY_SECRET, "test_access_key_secret");
        headers.put(Constants.FC_SECURITY_TOKEN, "test_security_token");
    }

    @Test
    public void testClientInjection() throws Exception {
        ReflectionTestUtils.setField(adapterManager, "deployType", "ecs");
        adapterManager.clientInjection(null);
        new Verifications() {{
            oosClient.createClient(aliyunConfig); times = 1;
            oosSecretParamConfig.init(); times = 1;
            otsClient.createClient(aliyunConfig); times = 1;
            cloudMonitorClient.createClient(aliyunConfig); times = 1;
            computeNestSupplierClient.createClient(aliyunConfig); times = 1;
            baseAlipayClient.createClient(boostAlipayConfig); times = 1;
        }};
    }

    @Test
    public void testRun(@Mocked ApplicationArguments applicationArguments) throws Exception {
        ReflectionTestUtils.setField(adapterManager, "deployType", "ecs");
        new Expectations() {{
            adapterManager.run(applicationArguments);
        }};
        Assertions.assertDoesNotThrow(() -> {
            adapterManager.run(applicationArguments);
        });
    }

    @Test
    public void testClientInjectionWithHeaders() throws Exception {
        adapterManager.clientInjection(headers);
        new Verifications() {{
            oosClient.createClient(anyString, anyString, anyString); times = 1;
            oosSecretParamConfig.init(); times = 1;
            otsClient.createClient(anyString, anyString, anyString); times = 1;
            computeNestSupplierClient.createClient(anyString, anyString, anyString); times = 1;
            baseAlipayClient.createClient(boostAlipayConfig); times = 1;
        }};
    }

    @Test
    public void testUpdateClient() throws Exception {
        ReflectionTestUtils.setField(adapterManager, "deployType", "ecs");
        adapterManager.updateClient();
        new Verifications() {{
            adapterManager.updateClient();times=1;
        }};
    }

}