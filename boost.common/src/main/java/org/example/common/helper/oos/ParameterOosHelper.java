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

import com.aliyun.oos20190601.models.GetParametersResponse;
import com.aliyun.oos20190601.models.GetParametersResponseBody;
import com.aliyun.oos20190601.models.GetParametersResponseBody.GetParametersResponseBodyParameters;
import com.aliyun.oos20190601.models.GetSecretParametersResponse;
import com.aliyun.oos20190601.models.GetSecretParametersResponseBody;
import com.aliyun.oos20190601.models.GetSecretParametersResponseBody.GetSecretParametersResponseBodyParameters;
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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.common.BaseResult;
import org.example.common.adapter.BaseAlipayClient;
import org.example.common.adapter.BaseWechatPayClient;
import org.example.common.adapter.OosClient;
import org.example.common.constant.Constants;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.model.ConfigParameterModel;
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

    public List<ConfigParameterModel> listSecretParameters(List<String> nameList) {
        try {
            List<ConfigParameterModel> secretParameterList = new ArrayList<>();
            if (CollectionUtils.isEmpty(nameList)) {
                return secretParameterList;
            }
            GetSecretParametersResponse response = oosClient.listSecretParameters(nameList);
            Optional<List<GetSecretParametersResponseBodyParameters>> optionalValue = Optional.ofNullable(response)
                    .map(GetSecretParametersResponse::getBody)
                    .map(GetSecretParametersResponseBody::getParameters);
            optionalValue.orElseThrow(() -> new BizException(ErrorInfo.PARAMETER_NOT_FOUND.getStatusCode(),
                    ErrorInfo.PARAMETER_NOT_FOUND.getCode(), String.format(ErrorInfo.PARAMETER_NOT_FOUND.getMessage(), nameList)));
            for (GetSecretParametersResponseBodyParameters secretParameter : optionalValue.get()) {
                ConfigParameterModel secretParameterModel = extractSecretParameterDetails(secretParameter);
                secretParameterList.add(secretParameterModel);
            }
            return secretParameterList;
        } catch (Exception e) {
            log.error("ParameterOosHelper.listSecretParameters request:{}, throw Exception",
                    JsonUtil.toJsonString(nameList), e);
            throw new BizException(ErrorInfo.PARAMETER_NOT_FOUND.getStatusCode(), ErrorInfo.PARAMETER_NOT_FOUND.getCode(),
                    String.format(ErrorInfo.PARAMETER_NOT_FOUND.getMessage(), nameList), e);
        }
    }

    public List<ConfigParameterModel> listParameters(List<String> nameList) {
        try {
            List<ConfigParameterModel> parameterList = new ArrayList<>();
            if (CollectionUtils.isEmpty(nameList)) {
                return parameterList;
            }
            GetParametersResponse response = oosClient.listParameters(nameList);
            Optional<List<GetParametersResponseBodyParameters>> optionalValue = Optional.ofNullable(response)
                    .map(GetParametersResponse::getBody)
                    .map(GetParametersResponseBody::getParameters);

            optionalValue.orElseThrow(() -> new BizException(ErrorInfo.PARAMETER_NOT_FOUND.getStatusCode(),
                    ErrorInfo.PARAMETER_NOT_FOUND.getCode(), String.format(ErrorInfo.PARAMETER_NOT_FOUND.getMessage(),
                    nameList)));
            for (GetParametersResponseBodyParameters parameters : optionalValue.get()) {
                ConfigParameterModel parameterModel = extractParameterDetails(parameters);
                parameterList.add(parameterModel);
            }
            return parameterList;
        } catch (Exception e) {
            log.error("ParameterOosHelper.listParameters request:{}, throw Exception",
                    JsonUtil.toJsonString(nameList), e);
            throw new BizException(ErrorInfo.PARAMETER_NOT_FOUND.getStatusCode(), ErrorInfo.PARAMETER_NOT_FOUND.getCode(),
                    String.format(ErrorInfo.PARAMETER_NOT_FOUND.getMessage(), nameList), e);
        }
    }


    private ConfigParameterModel extractSecretParameterDetails(GetSecretParametersResponseBodyParameters
            secretParameter) {
        ConfigParameterModel configParameterModel = new ConfigParameterModel();
        configParameterModel.setName(secretParameter.getName());
        configParameterModel.setType(secretParameter.getType());
        configParameterModel.setValue(secretParameter.getValue());
        return configParameterModel;
    }

    private ConfigParameterModel extractParameterDetails(GetParametersResponseBodyParameters parameter) {
        ConfigParameterModel configParameterModel = new ConfigParameterModel();
        configParameterModel.setName(parameter.getName());
        configParameterModel.setType(parameter.getType());
        configParameterModel.setValue(parameter.getValue());
        return configParameterModel;
    }
}
