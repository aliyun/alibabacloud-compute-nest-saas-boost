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
import com.aliyun.computenestsupplier20210521.models.GetServiceRequest;
import com.aliyun.computenestsupplier20210521.models.GetServiceResponse;
import com.aliyun.computenestsupplier20210521.models.GetServiceTemplateParameterConstraintsRequest;
import com.aliyun.computenestsupplier20210521.models.GetServiceTemplateParameterConstraintsResponse;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesRequest;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesResponse;
import com.aliyun.computenestsupplier20210521.models.UpdateServiceInstanceAttributeRequest;
import com.aliyun.computenestsupplier20210521.models.UpdateServiceInstanceAttributeResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.extern.slf4j.Slf4j;
import org.example.common.adapter.ComputeNestSupplierClient;
import org.example.common.config.AliyunConfig;
import org.example.common.constant.ComputeNestConstants;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ComputeNestSupplierClientImpl implements ComputeNestSupplierClient {

    private Client client;

    @Value("${service.region-id}")
    private String regionId;

    @Override
    public ListServiceInstancesResponse listServiceInstances(ListServiceInstancesRequest request) throws Exception {
        RuntimeOptions runtime = new RuntimeOptions();
        return client.listServiceInstancesWithOptions(request, runtime);
    }

    @Override
    public GetServiceInstanceResponse getServiceInstance(GetServiceInstanceRequest request) throws Exception {
        RuntimeOptions runtime = new RuntimeOptions();
        return client.getServiceInstanceWithOptions(request, runtime);
    }

    @Override
    public CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest request) {
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            return client.createServiceInstanceWithOptions(request, runtime);
        } catch (Exception e) {
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE, e);
        }
    }

    @Override
    public ContinueDeployServiceInstanceResponse continueDeployServiceInstance(ContinueDeployServiceInstanceRequest request) {
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            return client.continueDeployServiceInstanceWithOptions(request, runtime);
        } catch (Exception e) {
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE, e);
        }
    }

    @Override
    public DeleteServiceInstancesResponse deleteServiceInstance(DeleteServiceInstancesRequest deleteServiceInstancesRequest) {
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            log.info("delete compute nest service instance request: {}", deleteServiceInstancesRequest.getServiceInstanceId());
            return client.deleteServiceInstancesWithOptions(deleteServiceInstancesRequest, runtime);
        } catch (Exception e) {
            log.error("delete compute nest service instance failed.", e);
        }
        return null;
    }

    @Override
    public void createClient(AliyunConfig aliyunConfig) throws Exception {
        Config config = new Config().setCredential(aliyunConfig.getClient());
        config.endpoint = ComputeNestConstants.SERVICE_ENDPOINT;
        this.client = new Client(config);
    }

    @Override
    public void createClient(String accessKeyId, String accessKeySecret, String securityToken) throws Exception {
        this.client = new Client(new Config().setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret).setSecurityToken(securityToken)
                .setEndpoint(ComputeNestConstants.SERVICE_ENDPOINT));
    }

    @Override
    public void createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config().setAccessKeyId(accessKeyId).setAccessKeySecret(accessKeySecret);
        config.endpoint = ComputeNestConstants.SERVICE_ENDPOINT;
        this.client = new Client(config);
    }

    @Override
    public GetServiceResponse getService(GetServiceRequest request) {
        RuntimeOptions runtimeOptions = new RuntimeOptions();
        try {
            return client.getServiceWithOptions(request, runtimeOptions);
        } catch (Exception e) {
            log.error("get service failed.", e);
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE, e);
        }

    }

    @Override
    public GetServiceTemplateParameterConstraintsResponse getServiceTemplateParameterConstraints(GetServiceTemplateParameterConstraintsRequest request) {
        try {
            return client.getServiceTemplateParameterConstraints(request);
        } catch (Exception e) {
            log.error("get service template parameter constraints failed.", e);
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE, e);
        }
    }

    @Override
    public UpdateServiceInstanceAttributeResponse updateServiceInstanceAttribute(UpdateServiceInstanceAttributeRequest request) {
        RuntimeOptions runtimeOptions = new RuntimeOptions();
        try {
            request.setRegionId(regionId);
            return client.updateServiceInstanceAttributeWithOptions(request, runtimeOptions);
        } catch (Exception e) {
            log.error("update service instance attribute failed.", e);
            return null;
        }
    }
}
