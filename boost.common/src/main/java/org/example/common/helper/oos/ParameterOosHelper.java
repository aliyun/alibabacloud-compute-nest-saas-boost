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
import com.aliyun.oos20190601.models.UpdateSecretParameterResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.example.common.BaseResult;
import org.example.common.adapter.OosClient;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.model.ConfigParameterModel;
import org.example.common.model.ListConfigParametersModel;

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
                if (updateSecretParameterResponse.getBody().getParameter().getId() != null && !updateSecretParameterResponse.getBody().getParameter().getId().isEmpty()) {
                    return BaseResult.success();
                } else {
                    return BaseResult.fail("updateConfigParameter::updateSecretParameter fail:"+ErrorInfo.RESOURCE_NOT_FOUND);
                }
            } else if (updateConfigParameterParam.getEncrypted().equals(Boolean.FALSE)) {
                UpdateParameterResponse updateParameterResponse = oosClient.updateParameter(updateConfigParameterParam.getName(), updateConfigParameterParam.getValue());
                if (updateParameterResponse.getBody().getParameter().getId() != null && !updateParameterResponse.getBody().getParameter().getId().isEmpty()) {
                    return BaseResult.success();
                } else {
                    return BaseResult.fail("ParameterOosHelper.updateConfigParameter updateParameter failed:"+ErrorInfo.RESOURCE_NOT_FOUND);
                }
            } else {
                return BaseResult.fail("updateConfigParameter fail, Either 'encrypted' is an unexpected value:"+ErrorInfo.INVALID_INPUT);
            }
        } catch (Exception e) {
            log.error("ParameterOosHelper.updateConfigParameter request:{}, throw Exception", JsonUtil.toJsonString(updateConfigParameterParam), e);
            throw new BizException(ErrorInfo.RESOURCE_NOT_FOUND);
        }
    }

    public BaseResult<ListConfigParametersModel> listConfigParameters(ListConfigParametersParam listConfigParametersParam) {
        List<String> names = listConfigParametersParam.getName();
        List<Boolean> encrypteds = listConfigParametersParam.getEncrypted();
        if (names == null || encrypteds == null || names.size() != encrypteds.size()) {
            return BaseResult.fail("listConfigParameters: Either 'names' or 'encrypteds' is null, or their size do not match.");
        }

        ListConfigParametersModel listConfigParametersModel = new ListConfigParametersModel();
        List<ConfigParameterModel> configParameterModels = new ArrayList<>();

        IntStream.range(0, names.size())
                .forEach(index -> {
                    try {
                        String nameItem = names.get(index);
                        Boolean encryptedItem = encrypteds.get(index);

                        if (encryptedItem.equals(Boolean.TRUE)) {
                            GetSecretParameterResponse response = oosClient.getSecretParameter(nameItem);
                            ConfigParameterModel configParameterModel = extractSecretParameterDetails(response);
                            configParameterModels.add(configParameterModel);
                        } else if (encryptedItem.equals(Boolean.FALSE)) {
                            GetParameterResponse response = oosClient.getParameter(nameItem);
                            ConfigParameterModel configParameterModel = extractParameterDetails(response);
                            configParameterModels.add(configParameterModel);
                        } else {
                            throw new RuntimeException("listConfigParameters fail, Either 'encrypteds' is an unexpected value.");
                        }
                    } catch (Exception e) {
                        log.error("ParameterOosHelper.listConfigParameters request:{}, throw Exception", JsonUtil.toJsonString(listConfigParametersParam), e);
                        throw new BizException(ErrorInfo.RESOURCE_NOT_FOUND);
                    }
                });
        listConfigParametersModel.setConfigParameterModels(configParameterModels);
        return BaseResult.success(listConfigParametersModel);
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
