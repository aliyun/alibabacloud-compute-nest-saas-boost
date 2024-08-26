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
import com.aliyun.computenestsupplier20210521.models.ListServicesRequest;
import com.aliyun.computenestsupplier20210521.models.ListServicesRequest.ListServicesRequestFilter;
import com.aliyun.computenestsupplier20210521.models.ListServicesResponse;
import com.aliyun.computenestsupplier20210521.models.ListServicesResponseBody;
import com.aliyun.computenestsupplier20210521.models.ListServicesResponseBody.ListServicesResponseBodyServicesServiceInfos;
import com.aliyun.computenestsupplier20210521.models.UpdateServiceRequest;
import com.aliyun.computenestsupplier20210521.models.UpdateServiceRequest.UpdateServiceRequestCommodity;
import com.aliyun.computenestsupplier20210521.models.UpdateServiceResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.adapter.ComputeNestSupplierClient;
import org.example.common.dto.CommodityDTO;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.helper.WalletHelper;
import org.example.common.helper.ots.CommodityOtsHelper;
import org.example.common.model.LicenseMetadataModel;
import org.example.common.model.SaasBoostConfigModel;
import org.example.common.model.ServiceMetadataModel;
import org.example.common.model.ServiceVersionModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.GetServiceCostParam;
import org.example.common.param.service.GetServiceMetadataParam;
import org.example.common.param.service.GetServiceTemplateParameterConstraintsParam;
import org.example.common.param.service.ListServicesParam;
import org.example.common.utils.JsonUtil;
import org.example.service.base.ServiceManager;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.common.constant.ComputeNestConstants.ALLOWED_REGIONS;
import static org.example.common.constant.ComputeNestConstants.DEFAULT_REGION_ID;
import static org.example.common.constant.ComputeNestConstants.ONLINE;
import static org.example.common.constant.ComputeNestConstants.PREDEFINED_PARAMETERS;
import static org.example.common.constant.ComputeNestConstants.PREDEFINED_PARAMETERS_NAME;
import static org.example.common.constant.ComputeNestConstants.SERVICE_ID;
import static org.example.common.constant.ComputeNestConstants.STATUS;
import static org.example.common.constant.ComputeNestConstants.TEMPLATE_CONFIGS;
import static org.example.common.constant.ComputeNestConstants.TEMPLATE_NAME;

@Service
@Slf4j
public class ServiceManagerImpl implements ServiceManager {

    @Resource
    private ComputeNestSupplierClient computeNestSupplierClient;

    @Resource
    private WalletHelper walletHelper;

    @Resource
    private CommodityOtsHelper commodityOtsHelper;

    @Override
    public BaseResult<ServiceMetadataModel> getServiceMetadata(UserInfoModel userInfoModel, GetServiceMetadataParam getServiceMetadataParam) {
        String serviceId = getServiceMetadataParam.getServiceId();
        String commodityCode = getServiceMetadataParam.getCommodityCode();
        GetServiceRequest request = new GetServiceRequest();
        if (StringUtils.isBlank(serviceId) && StringUtils.isBlank(commodityCode)) {
            throw new BizException(ErrorInfo.PARAMETER_MISSING.getStatusCode(), ErrorInfo.PARAMETER_MISSING.getCode(),
                    String.format(ErrorInfo.PARAMETER_MISSING.getMessage(), "serviceId or commodityCode"));
        }
        if (StringUtils.isNotBlank(getServiceMetadataParam.getCommodityCode())) {
            CommodityDTO commodity = commodityOtsHelper.getCommodity(commodityCode);
            serviceId = commodity.getServiceId();
            request.setServiceVersion(commodity.getServiceVersion());
        }
        request.setServiceId(serviceId);
        GetServiceResponse serviceResponse = computeNestSupplierClient.getService(request);
        GetServiceResponseBody responseBody = serviceResponse.getBody();
        ServiceMetadataModel getServiceMetadataModel = new ServiceMetadataModel();
        parseDeployMetadata(responseBody.getDeployMetadata(), getServiceMetadataModel);
        getServiceMetadataModel.setStatus(responseBody.getStatus());
        getServiceMetadataModel.setVersion(responseBody.getVersion());
        String licenseMetadata = responseBody.getLicenseMetadata();
        LicenseMetadataModel licenseMetadataModel = JsonUtil.parseObjectUpperCamelCase(licenseMetadata, LicenseMetadataModel.class);
        getServiceMetadataModel.setCommodityCode(responseBody.getCommodityCode());
        if (licenseMetadataModel != null) {
            getServiceMetadataModel.setRetentionDays(licenseMetadataModel.getRetentionDays());
        }
        return BaseResult.success(getServiceMetadataModel);
    }

    private void parseDeployMetadata(String deployMetadata, ServiceMetadataModel getServiceMetadataModel) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode deployMetadataRootNode;
        try {
            deployMetadataRootNode = mapper.readTree(deployMetadata);
            if (deployMetadataRootNode == null || deployMetadataRootNode.get(TEMPLATE_CONFIGS) == null) {
                return;
            }
            JsonNode templateConfigJsonNode = deployMetadataRootNode.get(TEMPLATE_CONFIGS).get(0);
            String templateName = templateConfigJsonNode.get(TEMPLATE_NAME).asText();
            JsonNode specificationsJsonNode = templateConfigJsonNode.get(PREDEFINED_PARAMETERS);
            List<String> specifications = new ArrayList<>();
            if (specificationsJsonNode != null && specificationsJsonNode.isArray()) {
                ArrayNode arrayNode = (ArrayNode) specificationsJsonNode;
                arrayNode.forEach(node -> {
                    JsonNode name = node.get(PREDEFINED_PARAMETERS_NAME);
                    specifications.add(name.textValue());
                });
            }
            JsonNode allowedRegions = templateConfigJsonNode.get(ALLOWED_REGIONS);
            getServiceMetadataModel.setTemplateName(templateName);
            getServiceMetadataModel.setAllowedRegions(allowedRegions.toString());
            getServiceMetadataModel.setSpecificationNameList(specifications);
        } catch (Exception e) {
            log.error("Parse deployMetadata failed", e);
        }
    }

    @Override
    public BaseResult<Void> bindCommodity(String serviceId, String commodityCode, String publicAccessUrl, String serviceVersion) {
        UpdateServiceRequest updateServiceRequest = new UpdateServiceRequest();
        updateServiceRequest.setServiceId(serviceId);
        updateServiceRequest.setServiceVersion(serviceVersion);
        UpdateServiceRequestCommodity updateServiceRequestCommodity = new UpdateServiceRequestCommodity();
        SaasBoostConfigModel saasBoostConfigModel = new SaasBoostConfigModel();
        saasBoostConfigModel.setCommodityCode(commodityCode);
        saasBoostConfigModel.setPublicAccessUrl(publicAccessUrl);
        saasBoostConfigModel.setEnabled(true);
        updateServiceRequestCommodity.setSaasBoostConfig(JsonUtil.toJsonStringWithUpperCamelCase(saasBoostConfigModel));
        updateServiceRequest.setCommodity(updateServiceRequestCommodity);
        UpdateServiceResponse updateServiceResponse = computeNestSupplierClient.updateService(updateServiceRequest);
        if (updateServiceResponse.getStatusCode() != 200) {
            String code = String.format(ErrorInfo.INVALID_PARAMETER.getCode(), "serviceId");
            String message = String.format(ErrorInfo.INVALID_PARAMETER.getMessage(), serviceId);
            throw new BizException(ErrorInfo.INVALID_PARAMETER.getStatusCode(), code, message);
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

    @Override
    public List<ServiceVersionModel> listServices(UserInfoModel userInfoModel, ListServicesParam listServicesParam) {
        ListServicesRequest listServicesRequest = new ListServicesRequest();
        List<ListServicesRequestFilter> filters = new ArrayList<>();
        if (StringUtils.isNotBlank(listServicesParam.getServiceId())) {
            ListServicesRequestFilter serviceIdFilter = new ListServicesRequestFilter();
            serviceIdFilter.setName(SERVICE_ID);
            serviceIdFilter.setValue(Arrays.asList(listServicesParam.getServiceId()));
            filters.add(serviceIdFilter);
        }
        ListServicesRequestFilter statusFilter = new ListServicesRequestFilter();
        statusFilter.setName(STATUS);
        statusFilter.setValue(Arrays.asList(ONLINE));
        filters.add(statusFilter);
        listServicesRequest.setFilter(filters);
        ListServicesResponse listServicesResponse = computeNestSupplierClient.listServices(listServicesRequest);
        if (listServicesResponse.getStatusCode() == 200) {
            ListServicesResponseBody body = listServicesResponse.getBody();
            if (body.getServices() != null && !body.getServices().isEmpty()) {
                List<ListServicesResponseBody.ListServicesResponseBodyServices> services = body.getServices();
                return services.stream()
                        .map(service -> {
                            ServiceVersionModel model = new ServiceVersionModel();
                            model.setServiceId(service.getServiceId());
                            model.setServiceVersion(service.getVersion());
                            List<ListServicesResponseBodyServicesServiceInfos> serviceInfos = service.getServiceInfos();
                            if (serviceInfos != null && !serviceInfos.isEmpty()) {
                                ListServicesResponseBody.ListServicesResponseBodyServicesServiceInfos serviceInfo = serviceInfos.get(0);
                                model.setServiceName(serviceInfo.getName());
                            }
                            return model;
                        })
                        .collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }
}
