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
import com.aliyun.oos20190601.models.GetSecretParameterRequest;
import com.aliyun.oos20190601.models.GetSecretParameterResponse;
import com.aliyun.oos20190601.models.GetSecretParameterResponseBody;
import com.aliyun.oos20190601.models.GetSecretParametersByPathRequest;
import com.aliyun.oos20190601.models.GetSecretParametersByPathResponse;
import com.aliyun.oos20190601.models.GetSecretParametersByPathResponseBody.GetSecretParametersByPathResponseBodyParameters;
import com.aliyun.teaopenapi.models.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.common.adapter.OosClient;
import org.example.common.config.AliyunConfig;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class OosClientImpl implements OosClient {

    private Client client;

    @Value("${service.region-id}")
    private String regionId;

    @Override
    public String getSecretParameter(String name) {
        try {
            GetSecretParameterRequest getSecretParameterRequest = new GetSecretParameterRequest()
                    .setRegionId(regionId)
                    .setName(name)
                    .setWithDecryption(Boolean.TRUE);
            GetSecretParameterResponse response = client.getSecretParameter(getSecretParameterRequest);
            Optional<String> optionalValue = Optional.ofNullable(response)
                    .map(GetSecretParameterResponse::getBody)
                    .map(GetSecretParameterResponseBody::getParameter)
                    .map(GetSecretParameterResponseBody.GetSecretParameterResponseBodyParameter::getValue);
            return optionalValue.orElseThrow(() -> new BizException(ErrorInfo.RESOURCE_NOT_FOUND));
        } catch (Exception e) {
            log.error("List secretParameter error", e);
            throw new BizException(ErrorInfo.RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public Map<String, String> getSecretParametersByPath(String path) {
        GetSecretParametersByPathRequest request = createGetSecretParametersByPathRequest(path);
        Map<String, String> secretMap = new HashMap<>(10, 0.75F);
        try {
            GetSecretParametersByPathResponse response = client.getSecretParametersByPath(request);
            if (response != null && response.body != null) {
                List<GetSecretParametersByPathResponseBodyParameters> parameters = response.body.getParameters();
                populateSecretMap(secretMap, parameters);
                return secretMap;
            }
        } catch (Exception e) {
            log.error("List secretParameter error", e);
        }
        throw new BizException(ErrorInfo.RESOURCE_NOT_FOUND);
    }

    private GetSecretParametersByPathRequest createGetSecretParametersByPathRequest(String path) {
        return new GetSecretParametersByPathRequest()
                .setPath(path)
                .setRegionId(regionId)
                .setWithDecryption(Boolean.TRUE);
    }

    private void populateSecretMap(Map<String, String> secretMap, List<GetSecretParametersByPathResponseBodyParameters> parameters) {
        for (GetSecretParametersByPathResponseBodyParameters param : parameters) {
            if (StringUtils.isNotEmpty(param.getName())) {
                secretMap.put(param.getName(), param.getValue());
            }
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
}
