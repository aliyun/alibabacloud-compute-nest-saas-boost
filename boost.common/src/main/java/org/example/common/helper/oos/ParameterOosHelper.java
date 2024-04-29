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
import com.aliyun.oos20190601.models.GetParameterResponseBody;
import com.aliyun.oos20190601.models.GetParameterResponseBody.GetParameterResponseBodyParameter;
import com.aliyun.oos20190601.models.GetSecretParameterResponse;
import com.aliyun.oos20190601.models.GetSecretParameterResponseBody;
import com.aliyun.oos20190601.models.GetSecretParameterResponseBody.GetSecretParameterResponseBodyParameter;
import com.aliyun.oos20190601.models.UpdateParameterResponse;
import com.aliyun.oos20190601.models.UpdateParameterResponseBody;
import com.aliyun.oos20190601.models.UpdateParameterResponseBody.UpdateParameterResponseBodyParameter;
import com.aliyun.oos20190601.models.UpdateSecretParameterResponse;
import com.aliyun.oos20190601.models.UpdateSecretParameterResponseBody;
import com.aliyun.oos20190601.models.UpdateSecretParameterResponseBody.UpdateSecretParameterResponseBodyParameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.adapter.BaseAlipayClient;
import org.example.common.adapter.BaseWechatPayClient;
import org.example.common.adapter.OosClient;
import org.example.common.constant.Constants;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.model.ConfigParameterModel;
import org.example.common.model.ConfigParameterQueryModel;
import org.example.common.param.parameter.ListConfigParametersParam;
import org.example.common.param.parameter.UpdateConfigParameterParam;
import org.example.common.utils.JsonUtil;
import org.springframework.stereotype.Component;

/**
 * @author mengjunwei.mjw
 */
@Component
@Slf4j
public class ParameterOosHelper {
    private final OosClient oosClient;

    @Resource
    private BaseAlipayClient baseAlipayClient;

    @Resource
    private BaseWechatPayClient baseWechatPayClient;

    public ParameterOosHelper(OosClient oosClient) {
        this.oosClient = oosClient;
    }

    public BaseResult<Void> updateConfigParameter(UpdateConfigParameterParam updateConfigParameterParam){
        try {
            if (updateConfigParameterParam.getEncrypted().equals(Boolean.TRUE)) {
                UpdateSecretParameterResponse updateSecretParameterResponse = oosClient.updateSecretParameter
                        (updateConfigParameterParam.getName(), StringUtils.trim(updateConfigParameterParam.getValue()));

                Optional<String> parameterIdOptional = Optional.ofNullable(updateSecretParameterResponse.getBody())
                        .map(UpdateSecretParameterResponseBody::getParameter)
                        .map(UpdateSecretParameterResponseBodyParameter::getId);
                return handleUpdateResponse(updateConfigParameterParam, parameterIdOptional);
            } else {

                UpdateParameterResponse updateParameterResponse = oosClient.updateParameter
                        (updateConfigParameterParam.getName(), StringUtils.trim(updateConfigParameterParam.getValue()));

                Optional<String> parameterIdOptional = Optional.ofNullable(updateParameterResponse.getBody())
                        .map(UpdateParameterResponseBody::getParameter)
                        .map(UpdateParameterResponseBodyParameter::getId);
                return handleUpdateResponse(updateConfigParameterParam, parameterIdOptional);
            }
        } catch (Exception e) {
            log.error("ParameterOosHelper.updateConfigParameter request:{}, throw Exception",
                    JsonUtil.toJsonString(updateConfigParameterParam), e);
            throw new BizException(ErrorInfo.PARAMETER_NOT_FOUND.getStatusCode(), ErrorInfo.PARAMETER_NOT_FOUND.getCode(),
                    String.format(ErrorInfo.PARAMETER_NOT_FOUND.getMessage(), updateConfigParameterParam.getName()), e);
        }
    }

    private BaseResult<Void> handleUpdateResponse(UpdateConfigParameterParam updateConfigParameterParam,
                                                  Optional<String> parameterIdOptional) throws Exception {
        if (parameterIdOptional.isPresent() && !parameterIdOptional.get().isEmpty()) {
            if (updateConfigParameterParam.getTag().equals(Constants.ALIPAY_TAG)) {
                baseAlipayClient.updateClient(updateConfigParameterParam.getName(), updateConfigParameterParam.getValue());
            } else if (updateConfigParameterParam.getTag().equals(Constants.WECHATPAY_TAG)) {
                baseWechatPayClient.updateClient(updateConfigParameterParam.getName(), StringUtils.trim(updateConfigParameterParam.getValue()));
            }
            return BaseResult.success();
        } else {
            return BaseResult.fail("The parameter in the response is an empty dictionary.");
        }
    }

    public ListResult<ConfigParameterModel> listConfigParameters(ListConfigParametersParam listConfigParametersParam) {
        ListResult<ConfigParameterModel> results = new ListResult<>();
        results.setData(new ArrayList<>());

        List<ConfigParameterQueryModel> queries = listConfigParametersParam.getConfigParameterQueryModels();
        if (queries == null || queries.isEmpty()) {
            results.setMessage("Invalid query: 'encrypted' must not be null and 'name' must not be null or empty");
            return results;
        }

        for (ConfigParameterQueryModel query : queries) {
            if (query.getEncrypted() == null || query.getName() == null || query.getName().isEmpty()) {
                results.setMessage("Invalid query: 'encrypted' must not be null and 'name' must not be null or empty");
                return results;
            }

            try {
                ConfigParameterModel configParameterModel;
                if (query.getEncrypted()) {
                    GetSecretParameterResponse secretResponse = oosClient.getSecretParameter(query.getName());
                    Optional<String> optionalValue = Optional.ofNullable(secretResponse)
                            .map(GetSecretParameterResponse::getBody)
                            .map(GetSecretParameterResponseBody::getParameter)
                            .map(GetSecretParameterResponseBodyParameter::getValue);
                    if (optionalValue.isPresent() && !optionalValue.get().isEmpty()) {
                        configParameterModel = extractSecretParameterDetails(secretResponse);
                    } else {
                        results.setMessage("The parameter in the response is an empty dictionary.");
                        return results;
                    }
                } else {
                    GetParameterResponse parameterResponse = oosClient.getParameter(query.getName());
                    Optional<String> optionalValue = Optional.ofNullable(parameterResponse)
                            .map(GetParameterResponse::getBody)
                            .map(GetParameterResponseBody::getParameter)
                            .map(GetParameterResponseBodyParameter::getValue);
                    if (optionalValue.isPresent() && !optionalValue.get().isEmpty()) {
                        configParameterModel = extractParameterDetails(parameterResponse);
                    } else {
                        results.setMessage("The parameter in the response is an empty dictionary.");
                        return results;
                    }
                }
                results.getData().add(configParameterModel);
            } catch (Exception e) {
                log.error("Error fetching config parameter request: {}", JsonUtil.toJsonString(listConfigParametersParam), e);
                throw new BizException(ErrorInfo.PARAMETER_NOT_FOUND.getStatusCode(), ErrorInfo.PARAMETER_NOT_FOUND.getCode(),
                        String.format(ErrorInfo.PARAMETER_NOT_FOUND.getMessage(), listConfigParametersParam.getConfigParameterQueryModels()), e);
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
            return optionalValue.orElseThrow(() -> new BizException(ErrorInfo.PARAMETER_NOT_FOUND.getStatusCode(),
                    ErrorInfo.PARAMETER_NOT_FOUND.getCode(), String.format(ErrorInfo.PARAMETER_NOT_FOUND.getMessage(), name)));
        } catch (Exception e) {
            log.error("ParameterOosHelper.getSecretParameter request:{}, throw Exception",
                    JsonUtil.toJsonString(name), e);
            throw new BizException(ErrorInfo.PARAMETER_NOT_FOUND.getStatusCode(), ErrorInfo.PARAMETER_NOT_FOUND.getCode(),
                    String.format(ErrorInfo.PARAMETER_NOT_FOUND.getMessage(), name), e);
        }
    }

    public String getParameter(String name) {
        try {
            GetParameterResponse response = oosClient.getParameter(name);
            Optional<String> optionalValue = Optional.ofNullable(response)
                    .map(GetParameterResponse::getBody)
                    .map(GetParameterResponseBody::getParameter)
                    .map(GetParameterResponseBody.GetParameterResponseBodyParameter::getValue);
            return optionalValue.orElseThrow(() -> new BizException(ErrorInfo.PARAMETER_NOT_FOUND.getStatusCode(),
                    ErrorInfo.PARAMETER_NOT_FOUND.getCode(), String.format(ErrorInfo.PARAMETER_NOT_FOUND.getMessage(), name)));
        } catch (Exception e) {
            log.error("ParameterOosHelper.getParameter request:{}, throw Exception",
                    JsonUtil.toJsonString(name), e);
            throw new BizException(ErrorInfo.PARAMETER_NOT_FOUND.getStatusCode(), ErrorInfo.PARAMETER_NOT_FOUND.getCode(),
                    String.format(ErrorInfo.PARAMETER_NOT_FOUND.getMessage(), name), e);
        }
    }


    private ConfigParameterModel extractSecretParameterDetails(GetSecretParameterResponse getSecretParameterResponse) {
        ConfigParameterModel configParameterModel = new ConfigParameterModel();
        configParameterModel.setName(getSecretParameterResponse.getBody().getParameter().getName());
        configParameterModel.setType(getSecretParameterResponse.getBody().getParameter().getType());
        configParameterModel.setValue(getSecretParameterResponse.getBody().getParameter().getValue());
        return configParameterModel;
    }

    private ConfigParameterModel extractParameterDetails(GetParameterResponse getParameterResponse) {
        ConfigParameterModel configParameterModel = new ConfigParameterModel();
        configParameterModel.setName(getParameterResponse.getBody().getParameter().getName());
        configParameterModel.setType(getParameterResponse.getBody().getParameter().getType());
        configParameterModel.setValue(getParameterResponse.getBody().getParameter().getValue());
        return configParameterModel;
    }
}
