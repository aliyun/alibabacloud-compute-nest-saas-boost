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
import com.aliyun.oos20190601.models.GetParameterRequest;
import com.aliyun.oos20190601.models.GetParameterResponse;
import com.aliyun.oos20190601.models.GetSecretParameterRequest;
import com.aliyun.oos20190601.models.GetSecretParameterResponse;
import com.aliyun.oos20190601.models.UpdateParameterRequest;
import com.aliyun.oos20190601.models.UpdateParameterResponse;
import com.aliyun.oos20190601.models.UpdateSecretParameterRequest;
import com.aliyun.oos20190601.models.UpdateSecretParameterResponse;
import com.aliyun.teaopenapi.models.Config;
import lombok.extern.slf4j.Slf4j;
import org.example.common.adapter.OosClient;
import org.example.common.config.AliyunConfig;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OosClientImpl implements OosClient {

    private Client client;

    @Value("${service.region-id}")
    private String regionId;

    @Override
    public GetSecretParameterResponse getSecretParameter(String name) {
        try {
            GetSecretParameterRequest getSecretParameterRequest = new GetSecretParameterRequest()
                    .setRegionId(regionId)
                    .setName(name)
                    .setWithDecryption(Boolean.TRUE);
            return client.getSecretParameter(getSecretParameterRequest);
        } catch (Exception e) {
            log.error("getSecretParameter error", e);
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
            log.error("updateSecretParameter error", e);
            throw new BizException(ErrorInfo.RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public GetParameterResponse getParameter(String name) {
        try {
            GetParameterRequest getParameterRequest = new GetParameterRequest()
                    .setRegionId(regionId)
                    .setName(name);
            return client.getParameter(getParameterRequest);
        } catch (Exception e) {
            log.error("getParameter error", e);
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
            log.error("updateParameter error", e);
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
