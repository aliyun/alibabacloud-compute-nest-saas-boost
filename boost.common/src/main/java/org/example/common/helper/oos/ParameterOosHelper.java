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

package org.example.common.helper.oos;

import com.aliyun.oos20190601.models.GetParameterResponse;
import com.aliyun.oos20190601.models.GetSecretParameterResponse;
import com.aliyun.oos20190601.models.GetSecretParameterResponseBody;
import com.aliyun.oos20190601.models.UpdateParameterResponse;
import com.aliyun.oos20190601.models.UpdateParameterResponseBody;
import com.aliyun.oos20190601.models.UpdateParameterResponseBody.UpdateParameterResponseBodyParameter;
import com.aliyun.oos20190601.models.UpdateSecretParameterResponse;
import com.aliyun.oos20190601.models.UpdateSecretParameterResponseBody;
import com.aliyun.oos20190601.models.UpdateSecretParameterResponseBody.UpdateSecretParameterResponseBodyParameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.adapter.OosClient;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.model.ConfigParameterModel;
import org.example.common.model.ConfigParameterQueryModel;
import org.example.common.param.parameter.ListConfigParametersParam;
import org.example.common.param.parameter.UpdateConfigParameterParam;
import org.example.common.utils.JsonUtil;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ParameterOosHelper {

    private final OosClient oosClient;

    public ParameterOosHelper(OosClient oosClient) {
        this.oosClient = oosClient;
    }

    public BaseResult<Void> updateConfigParameter(UpdateConfigParameterParam updateConfigParameterParam){
        try {

            if (updateConfigParameterParam.getEncrypted().equals(Boolean.TRUE)) {
                UpdateSecretParameterResponse updateSecretParameterResponse = oosClient.updateSecretParameter(updateConfigParameterParam.getName(), updateConfigParameterParam.getValue());

                Optional<String> parameterIdOptional = Optional.ofNullable(updateSecretParameterResponse.getBody())
                        .map(UpdateSecretParameterResponseBody::getParameter)
                        .map(UpdateSecretParameterResponseBodyParameter::getId);

                if (parameterIdOptional.isPresent() && !parameterIdOptional.get().isEmpty()) {
                    return BaseResult.success();
                } else {
                    return BaseResult.fail("updateConfigParameter::updateSecretParameter fail:");
                }
            } else {
                UpdateParameterResponse updateParameterResponse = oosClient.updateParameter(updateConfigParameterParam.getName(), updateConfigParameterParam.getValue());

                Optional<String> parameterIdOptional = Optional.ofNullable(updateParameterResponse.getBody())
                        .map(UpdateParameterResponseBody::getParameter)
                        .map(UpdateParameterResponseBodyParameter::getId);

                if (parameterIdOptional.isPresent() && !parameterIdOptional.get().isEmpty()) {
                    return BaseResult.success();
                } else {
                    return BaseResult.fail("ParameterOosHelper.updateConfigParameter updateParameter failed:");
                }
            }
        } catch (Exception e) {
            log.error("ParameterOosHelper.updateConfigParameter request:{}, throw Exception", JsonUtil.toJsonString(updateConfigParameterParam), e);
            throw new BizException(ErrorInfo.RESOURCE_NOT_FOUND);
        }
    }

    public ListResult<ConfigParameterModel> listConfigParameters(ListConfigParametersParam listConfigParametersParam) {
        ListResult<ConfigParameterModel> results = new ListResult<ConfigParameterModel>();
        results.setData(new ArrayList<>());

        List<ConfigParameterQueryModel> queries = listConfigParametersParam.getConfigParameterQueryModels();
        if (queries == null || queries.isEmpty()) {
            return (ListResult<ConfigParameterModel>) ListResult.fail("Invalid query: 'encrypted' must not be null and 'name' must not be null or empty");
        }

        for (ConfigParameterQueryModel query : queries) {
            if (query.getEncrypted() == null || query.getName() == null || query.getName().isEmpty()) {
                return (ListResult<ConfigParameterModel>) ListResult.fail("Invalid query: 'encrypted' must not be null and 'name' must not be null or empty");
            }

            try {
                ConfigParameterModel configParameterModel;
                if (query.getEncrypted()) {
                    GetSecretParameterResponse secretResponse = oosClient.getSecretParameter(query.getName());
                    configParameterModel = extractSecretParameterDetails(secretResponse);
                } else {
                    GetParameterResponse parameterResponse = oosClient.getParameter(query.getName());
                    configParameterModel = extractParameterDetails(parameterResponse);
                }
                results.getData().add(configParameterModel);
            } catch (Exception e) {
                log.error("Error fetching config parameter request: {}", JsonUtil.toJsonString(listConfigParametersParam), e);
                throw new BizException(ErrorInfo.RESOURCE_NOT_FOUND);
            }
        }

        return results;
    }

    public String getSecretParameter(String name) {
        try {
            GetSecretParameterResponse response = oosClient.getSecretParameter(name);
            Optional<String> optionalValue = Optional.ofNullable(response)
                    .map(GetSecretParameterResponse::getBody)
                    .map(GetSecretParameterResponseBody::getParameter)
                    .map(GetSecretParameterResponseBody.GetSecretParameterResponseBodyParameter::getValue);
            return optionalValue.orElseThrow(() -> new BizException(ErrorInfo.RESOURCE_NOT_FOUND));
        } catch (Exception e) {
            log.error("ParameterOosHelper.getSecretParameter request:{}, throw Exception", JsonUtil.toJsonString(name), e);
            throw new BizException(ErrorInfo.RESOURCE_NOT_FOUND);
        }
    }


    private ConfigParameterModel extractSecretParameterDetails(GetSecretParameterResponse getSecretParameterResponse) {
        ConfigParameterModel configParameterModel = new ConfigParameterModel();
        configParameterModel.setName(getSecretParameterResponse.getBody().getParameter().getName());
        configParameterModel.setType(getSecretParameterResponse.getBody().getParameter().getType());
        configParameterModel.setId(getSecretParameterResponse.getBody().getParameter().getId());
        return configParameterModel;
    }

    private ConfigParameterModel extractParameterDetails(GetParameterResponse getParameterResponse) {
        ConfigParameterModel configParameterModel = new ConfigParameterModel();
        configParameterModel.setName(getParameterResponse.getBody().getParameter().getName());
        configParameterModel.setType(getParameterResponse.getBody().getParameter().getType());
        configParameterModel.setId(getParameterResponse.getBody().getParameter().getId());
        return configParameterModel;
    }
}
