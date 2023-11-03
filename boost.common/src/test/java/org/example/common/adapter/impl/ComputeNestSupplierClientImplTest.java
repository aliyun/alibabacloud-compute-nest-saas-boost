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

import com.aliyun.computenestsupplier20210521.Client;
import com.aliyun.computenestsupplier20210521.models.ContinueDeployServiceInstanceRequest;
import com.aliyun.computenestsupplier20210521.models.ContinueDeployServiceInstanceResponse;
import com.aliyun.computenestsupplier20210521.models.CreateServiceInstanceRequest;
import com.aliyun.computenestsupplier20210521.models.CreateServiceInstanceResponse;
import com.aliyun.computenestsupplier20210521.models.DeleteServiceInstancesRequest;
import com.aliyun.computenestsupplier20210521.models.DeleteServiceInstancesResponse;
import com.aliyun.computenestsupplier20210521.models.GetServiceInstanceRequest;
import com.aliyun.computenestsupplier20210521.models.GetServiceInstanceResponse;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesRequest;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.extern.slf4j.Slf4j;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.internal.reflection.FieldReflection;
import org.example.common.config.AliyunConfig;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

@Slf4j
class ComputeNestSupplierClientImplTest {
    private static Config config;

    private static AliyunConfig aliyunConfig;

    private static Client client;

    static {
        config = new Config();
        config.setAccessKeyId("ak");
        config.setAccessKeySecret("secret");
        config.setRegionId("cn-hangzhou");
        aliyunConfig = new AliyunConfig();
        try {
            client = new Client(config);
        } catch (Exception e) {
            log.error("client create error", e);
        }
    }

    @Tested
    private ComputeNestSupplierClientImpl computeNestSupplierClientImpl = new ComputeNestSupplierClientImpl();

    ComputeNestSupplierClientImplTest() throws Exception {
    }

    @BeforeEach
    void createClient() throws NoSuchFieldException {
        Field configField = ComputeNestSupplierClientImpl.class.getDeclaredField("client");
        configField.setAccessible(true);
        FieldReflection.setFieldValue(configField, computeNestSupplierClientImpl, client);
    }

    @Test
    void testCreateComputeNestClient(@Injectable AliyunConfig aliyunConfig) {
        Assertions.assertDoesNotThrow(() -> computeNestSupplierClientImpl.createClient(aliyunConfig));
    }

    @Test
    void testListServiceInstances(@Mocked Client computeNestClient) throws Exception {
        new Expectations() {{
            RuntimeOptions runtime = new RuntimeOptions();
            computeNestClient.listServiceInstancesWithOptions(withAny(new ListServiceInstancesRequest()), withAny(runtime));
            result = new ListServiceInstancesResponse();
        }};
        Assertions.assertDoesNotThrow(() -> computeNestSupplierClientImpl.listServiceInstances(new ListServiceInstancesRequest()));
    }

    @Test
    void testGetServiceInstance(@Mocked Client computeNestClient) throws Exception {
        new Expectations() {{
            RuntimeOptions runtime = new RuntimeOptions();
            computeNestClient.getServiceInstanceWithOptions(withAny(new GetServiceInstanceRequest()), withAny(runtime));
            result = new GetServiceInstanceResponse();
        }};
        Assertions.assertDoesNotThrow(() -> computeNestSupplierClientImpl.getServiceInstance(new GetServiceInstanceRequest()));
    }

    @Test
    void testCreateServiceInstance(@Mocked Client computeNestClient) throws Exception {
        new Expectations() {{
            RuntimeOptions runtime = new RuntimeOptions();
            computeNestClient.createServiceInstanceWithOptions(withAny(new CreateServiceInstanceRequest()), withAny(runtime));
            result = new CreateServiceInstanceResponse();
        }};
        Assertions.assertDoesNotThrow(() -> computeNestSupplierClientImpl.createServiceInstance(new CreateServiceInstanceRequest()));
    }

    @Test
    void testCreateServiceInstanceFailed(@Mocked Client computeNestClient) throws Exception {
        new Expectations() {{
            RuntimeOptions runtime = new RuntimeOptions();
            computeNestClient.createServiceInstanceWithOptions(withAny(new CreateServiceInstanceRequest()), withAny(runtime));
            result = new BizException(ErrorInfo.SERVER_UNAVAILABLE);
        }};
        Assertions.assertThrows(BizException.class, () -> computeNestSupplierClientImpl.createServiceInstance(new CreateServiceInstanceRequest()));
    }

    @Test
    void testContinueDeployServiceInstance(@Mocked Client computeNestClient) throws Exception {
        new Expectations() {{
            RuntimeOptions runtime = new RuntimeOptions();
            computeNestClient.continueDeployServiceInstanceWithOptions(withAny(new ContinueDeployServiceInstanceRequest()), withAny(runtime));
            result = new ContinueDeployServiceInstanceResponse();
        }};
        Assertions.assertDoesNotThrow(() -> computeNestSupplierClientImpl.continueDeployServiceInstance(new ContinueDeployServiceInstanceRequest()));
    }

    @Test
    void testDeleteServiceInstance(@Mocked Client computeNestClient) throws Exception {
        new Expectations() {{
            RuntimeOptions runtime = new RuntimeOptions();
            computeNestClient.deleteServiceInstancesWithOptions(withAny(new DeleteServiceInstancesRequest()), withAny(runtime));
            result = new DeleteServiceInstancesResponse();
        }};
        Assertions.assertDoesNotThrow(() -> computeNestSupplierClientImpl.deleteServiceInstance(new DeleteServiceInstancesRequest()));
    }

    @Test
    void testDeleteServiceInstanceFailed(@Mocked Client computeNestClient) throws Exception {
        new Expectations() {{
            RuntimeOptions runtime = new RuntimeOptions();
            computeNestClient.deleteServiceInstancesWithOptions(withAny(new DeleteServiceInstancesRequest()), withAny(runtime));
            result = new Exception();
        }};
        Assertions.assertDoesNotThrow(() -> computeNestSupplierClientImpl.deleteServiceInstance(new DeleteServiceInstancesRequest()));
    }
}
