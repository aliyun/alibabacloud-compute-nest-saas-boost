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
package org.example.service.impl;

import com.aliyun.computenestsupplier20210521.models.GetServiceRequest;
import com.aliyun.computenestsupplier20210521.models.GetServiceResponse;
import com.aliyun.computenestsupplier20210521.models.GetServiceResponseBody;
import com.aliyun.computenestsupplier20210521.models.GetServiceTemplateParameterConstraintsRequest;
import com.aliyun.computenestsupplier20210521.models.GetServiceTemplateParameterConstraintsResponse;
import com.aliyun.computenestsupplier20210521.models.GetServiceTemplateParameterConstraintsResponseBody;
import com.aliyun.computenestsupplier20210521.models.GetServiceTemplateParameterConstraintsResponseBody.GetServiceTemplateParameterConstraintsResponseBodyParameterConstraints;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.adapter.ComputeNestSupplierClient;
import org.example.common.config.SpecificationConfig;
import org.example.common.helper.WalletHelper;
import org.example.common.model.ServiceMetadataModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.GetServiceCostParam;
import org.example.common.param.GetServiceMetadataParam;
import org.example.common.utils.HttpUtil;
import org.example.common.utils.JsonUtil;
import org.example.common.utils.YamlUtil;
import org.example.service.ServiceManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static org.example.common.constant.ComputeNestConstants.ALLOWED_REGIONS;
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
    private SpecificationConfig specificationConfig;

    @Value("${service.id}")
    private String serviceId;

    @Value("${service.region-id}")
    private String regionId;

    @Resource
    private WalletHelper walletHelper;

    @Cacheable(value = "serviceModelCache", key = "targetClass + methodName+#getServiceMetadataParam.serviceId")
    @Override
    public BaseResult<ServiceMetadataModel> getServiceMetadata(UserInfoModel userInfoModel, GetServiceMetadataParam getServiceMetadataParam) {
        GetServiceRequest request = new GetServiceRequest();
        if (StringUtils.isNotEmpty(getServiceMetadataParam.getServiceId())) {
            request.setServiceId(getServiceMetadataParam.getServiceId());
        } else {
            request.setServiceId(serviceId);
        }
        request.setFilterAliUid(true);
        GetServiceResponse serviceResponse = computeNestSupplierClient.getService(request);
        GetServiceResponseBody responseBody = serviceResponse.getBody();
        ServiceMetadataModel getServiceMetadataModel = new ServiceMetadataModel();
        String deployMetadata = responseBody.getDeployMetadata();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode deployMetadataRootNode;
        try {
            deployMetadataRootNode = mapper.readTree(deployMetadata);
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
        } catch (JsonProcessingException e) {
            log.error("Parse deployMetadata failed", e);
        }
        return BaseResult.success(getServiceMetadataModel);
    }

    @Override
    public BaseResult<Double> getServiceCost(UserInfoModel userInfoModel, GetServiceCostParam param) {
        return BaseResult.success(walletHelper.getServiceCost(param.getServiceId(), param.getSpecificationName(), param.getPayPeriod(), param.getPayPeriodUnit()));
    }

    @Override
    public ListResult<GetServiceTemplateParameterConstraintsResponseBodyParameterConstraints> getServiceTemplateParameterConstraints(GetServiceTemplateParameterConstraintsRequest request) {
        GetServiceTemplateParameterConstraintsResponse serviceTemplateParameterConstraints = computeNestSupplierClient.getServiceTemplateParameterConstraints(request);
        GetServiceTemplateParameterConstraintsResponseBody responseBody = serviceTemplateParameterConstraints.getBody();
        List<GetServiceTemplateParameterConstraintsResponseBodyParameterConstraints> parameterConstraints = responseBody.getParameterConstraints();
        return ListResult.genSuccessListResult(parameterConstraints, parameterConstraints.size());
    }
}
