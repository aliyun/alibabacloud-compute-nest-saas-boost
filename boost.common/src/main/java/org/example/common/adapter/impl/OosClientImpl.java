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

import com.aliyun.oos20190601.Client;
import com.aliyun.oos20190601.models.GetParametersRequest;
import com.aliyun.oos20190601.models.GetParametersResponse;
import com.aliyun.oos20190601.models.GetSecretParametersRequest;
import com.aliyun.oos20190601.models.GetSecretParametersResponse;
import com.aliyun.oos20190601.models.UpdateParameterRequest;
import com.aliyun.oos20190601.models.UpdateParameterResponse;
import com.aliyun.oos20190601.models.UpdateSecretParameterRequest;
import com.aliyun.oos20190601.models.UpdateSecretParameterResponse;
import com.aliyun.teaopenapi.models.Config;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.example.common.adapter.OosClient;
import org.example.common.config.AliyunConfig;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OosClientImpl implements OosClient {

    private Client client;

    @Value("${service.region-id}")
    private String regionId;

    @Override
    public GetSecretParametersResponse listSecretParameters(List<String> nameList) {
        try {
            String names = JsonUtil.toJsonString(nameList);
            GetSecretParametersRequest getSecretParametersRequest = new GetSecretParametersRequest()
                    .setRegionId(regionId)
                    .setNames(names)
                    .setWithDecryption(Boolean.TRUE);
            return client.getSecretParameters(getSecretParametersRequest);
        } catch (Exception e) {
            log.error("oosClient.listSecretParameters request:{}, throw Exception", JsonUtil.toJsonString(nameList), e);
            throw new BizException(ErrorInfo.RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public UpdateSecretParameterResponse updateSecretParameter(String name, String value) {
        try {
            UpdateSecretParameterRequest updateSecretParameterRequest = new UpdateSecretParameterRequest()
                    .setRegionId(regionId)
                    .setName(name)
                    .setValue(value);
            return client.updateSecretParameter(updateSecretParameterRequest);
        } catch (Exception e) {
            log.error("oosClient.updateSecretParameter request:{}{}, throw Exception", JsonUtil.toJsonString(name), JsonUtil.toJsonString(value), e);
            throw new BizException(ErrorInfo.RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public GetParametersResponse listParameters(List<String> nameList) {
        try {
            String names = JsonUtil.toJsonString(nameList);
            GetParametersRequest getParametersRequest = new GetParametersRequest()
                    .setRegionId(regionId)
                    .setNames(names);
            return client.getParameters(getParametersRequest);
        } catch (Exception e) {
            log.error("oosClient.listParameters request:{}, throw Exception", JsonUtil.toJsonString(nameList), e);
            throw new BizException(ErrorInfo.RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public UpdateParameterResponse updateParameter(String name, String value) {
        try {
            UpdateParameterRequest updateParameterRequest = new UpdateParameterRequest()
                    .setRegionId(regionId)
                    .setName(name)
                    .setValue(value);
            return client.updateParameter(updateParameterRequest);
        } catch (Exception e) {
            log.error("oosClient.updateParameter request:{}, throw Exception", JsonUtil.toJsonString(name), e);
            throw new BizException(ErrorInfo.RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public void createClient(AliyunConfig aliyunConfig) throws Exception {
        this.client = new Client(new Config()
                .setEndpoint(String.format("oos.%s.aliyuncs.com", regionId))
                .setCredential(aliyunConfig.getClient()));
    }

    @Override
    public void createClient(String accessKeyId, String accessKeySecret, String securityToken) throws Exception {
        this.client = new Client(new Config().setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret).setSecurityToken(securityToken).setEndpoint(String.format("oos.%s.aliyuncs.com", regionId)));
    }

    @Override
    public void createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config().setAccessKeyId(accessKeyId).setAccessKeySecret(accessKeySecret);
        config.endpoint = String.format("oos.%s.aliyuncs.com", regionId);
        this.client = new Client(config);
    }
}
