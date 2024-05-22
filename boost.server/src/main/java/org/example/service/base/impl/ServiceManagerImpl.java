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
package org.example.service.base.impl;

import com.aliyun.computenestsupplier20210521.models.GetServiceRequest;
import com.aliyun.computenestsupplier20210521.models.GetServiceResponse;
import com.aliyun.computenestsupplier20210521.models.GetServiceResponseBody;
import com.aliyun.computenestsupplier20210521.models.GetServiceTemplateParameterConstraintsRequest;
import com.aliyun.computenestsupplier20210521.models.GetServiceTemplateParameterConstraintsResponse;
import com.aliyun.computenestsupplier20210521.models.GetServiceTemplateParameterConstraintsResponseBody;
import com.aliyun.computenestsupplier20210521.models.GetServiceTemplateParameterConstraintsResponseBody.GetServiceTemplateParameterConstraintsResponseBodyParameterConstraints;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.adapter.ComputeNestSupplierClient;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.helper.WalletHelper;
import org.example.common.model.LicenseMetadataModel;
import org.example.common.model.ServiceMetadataModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.GetServiceCostParam;
import org.example.common.param.service.GetServiceMetadataParam;
import org.example.common.param.service.GetServiceTemplateParameterConstraintsParam;
import org.example.common.utils.HttpUtil;
import org.example.common.utils.JsonUtil;
import org.example.common.utils.YamlUtil;
import org.example.service.base.ServiceManager;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.common.constant.ComputeNestConstants.ALLOWED_REGIONS;
import static org.example.common.constant.ComputeNestConstants.DEFAULT_REGION_ID;
import static org.example.common.constant.ComputeNestConstants.PREDEFINED_PARAMETERS;
import static org.example.common.constant.ComputeNestConstants.TEMPLATE_CONFIGS;
import static org.example.common.constant.ComputeNestConstants.TEMPLATE_NAME;
import static org.example.common.constant.ComputeNestConstants.TEMPLATE_URL;

@Service
@Slf4j
public class ServiceManagerImpl implements ServiceManager {

    @Resource
    private ComputeNestSupplierClient computeNestSupplierClient;

    @Resource
    private WalletHelper walletHelper;

    @Override
    public BaseResult<ServiceMetadataModel> getServiceMetadata(UserInfoModel userInfoModel, GetServiceMetadataParam getServiceMetadataParam) {
        GetServiceRequest request = new GetServiceRequest();
        request.setServiceId(getServiceMetadataParam.getServiceId());
        try {
            GetServiceResponse serviceResponse = computeNestSupplierClient.getService(request);
            GetServiceResponseBody responseBody = serviceResponse.getBody();
            ServiceMetadataModel getServiceMetadataModel = new ServiceMetadataModel();
            String deployMetadata = responseBody.getDeployMetadata();
            getServiceMetadataModel.setStatus(responseBody.getStatus());
            getServiceMetadataModel.setVersion(responseBody.getVersion());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode deployMetadataRootNode;
            String licenseMetadata = responseBody.getLicenseMetadata();
            LicenseMetadataModel licenseMetadataModel = JsonUtil.parseObjectUpperCamelCase(licenseMetadata, LicenseMetadataModel.class);
            deployMetadataRootNode = mapper.readTree(deployMetadata);
            if (deployMetadataRootNode == null || deployMetadataRootNode.get(TEMPLATE_CONFIGS) == null) {
                return BaseResult.fail(ErrorInfo.SPECIFICATION_NOT_EXIST);
            }
            JsonNode templateConfigJsonNode = deployMetadataRootNode.get(TEMPLATE_CONFIGS).get(0);
            String templateName = templateConfigJsonNode.get(TEMPLATE_NAME).asText();
            String url = templateConfigJsonNode.get(TEMPLATE_URL).asText();
            JsonNode specificationsJsonNode = templateConfigJsonNode.get(PREDEFINED_PARAMETERS);
            JsonNode allowedRegions = templateConfigJsonNode.get(ALLOWED_REGIONS);
            String templateConfigData = HttpUtil.doGet(url);
            if (!JsonUtil.isJson(templateConfigData)) {
                templateConfigData = YamlUtil.convertYamlToJson(templateConfigData);
            }
            JsonNode rosTemplateJsonNode = mapper.readTree(templateConfigData);
            String parameterMetadata = rosTemplateJsonNode.toString();

            getServiceMetadataModel.setParameterMetadata(parameterMetadata);
            getServiceMetadataModel.setTemplateName(templateName);
            getServiceMetadataModel.setSpecifications(specificationsJsonNode.toString());
            getServiceMetadataModel.setAllowedRegions(allowedRegions.toString());
            getServiceMetadataModel.setCommodityCode(responseBody.getCommodityCode());
            if (licenseMetadataModel != null) {
                getServiceMetadataModel.setRetentionDays(licenseMetadataModel.getRetentionDays());
            }
            return BaseResult.success(getServiceMetadataModel);

        } catch (Exception e) {
            log.error("Parse deployMetadata failed", e);
        }
        return BaseResult.success();
    }

    @Override
    public BaseResult<Double> getServiceCost(UserInfoModel userInfoModel, GetServiceCostParam param) {
        return BaseResult.success(walletHelper.getServiceCost(param.getServiceId(), param.getSpecificationName(), param.getPayPeriod(), param.getPayPeriodUnit()));
    }

    @Override
    public ListResult<GetServiceTemplateParameterConstraintsResponseBodyParameterConstraints> getServiceTemplateParameterConstraints(GetServiceTemplateParameterConstraintsParam constraintsParam) {
        GetServiceTemplateParameterConstraintsRequest request = new GetServiceTemplateParameterConstraintsRequest();
        BeanUtils.copyProperties(constraintsParam, request);
        List<GetServiceTemplateParameterConstraintsRequest.GetServiceTemplateParameterConstraintsRequestParameters> convertedParameters = new ArrayList<>();

        if (constraintsParam.getParameters() != null) {
            convertedParameters = constraintsParam.getParameters().stream()
                    .map(param -> {
                        GetServiceTemplateParameterConstraintsRequest.GetServiceTemplateParameterConstraintsRequestParameters getServiceTemplateParameterConstraintsRequestParameters
                                = new GetServiceTemplateParameterConstraintsRequest.GetServiceTemplateParameterConstraintsRequestParameters();
                        BeanUtils.copyProperties(param, getServiceTemplateParameterConstraintsRequestParameters);
                        return getServiceTemplateParameterConstraintsRequestParameters;
                    }).collect(Collectors.toList());
        }
        request.setParameters(convertedParameters);
        request.setRegionId(DEFAULT_REGION_ID);
        GetServiceTemplateParameterConstraintsResponse serviceTemplateParameterConstraints = computeNestSupplierClient.getServiceTemplateParameterConstraints(request);
        GetServiceTemplateParameterConstraintsResponseBody responseBody = serviceTemplateParameterConstraints.getBody();
        List<GetServiceTemplateParameterConstraintsResponseBodyParameterConstraints> parameterConstraints = responseBody.getParameterConstraints();
        return ListResult.genSuccessListResult(parameterConstraints, parameterConstraints.size());
    }
}
