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

import com.aliyun.computenestsupplier20210521.models.ContinueDeployServiceInstanceRequest;
import com.aliyun.computenestsupplier20210521.models.ContinueDeployServiceInstanceResponse;
import com.aliyun.computenestsupplier20210521.models.CreateServiceInstanceRequest;
import com.aliyun.computenestsupplier20210521.models.CreateServiceInstanceResponse;
import com.aliyun.computenestsupplier20210521.models.GetServiceInstanceRequest;
import com.aliyun.computenestsupplier20210521.models.GetServiceInstanceResponse;
import com.aliyun.computenestsupplier20210521.models.GetServiceInstanceResponseBody;
import com.aliyun.computenestsupplier20210521.models.GetServiceInstanceResponseBody.GetServiceInstanceResponseBodyServiceServiceInfos;
import com.aliyun.computenestsupplier20210521.models.GetServiceRequest;
import com.aliyun.computenestsupplier20210521.models.GetServiceResponse;
import com.aliyun.computenestsupplier20210521.models.GetServiceResponseBody;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesRequest;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesRequest.ListServiceInstancesRequestFilter;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesResponse;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesResponseBody;
import com.aliyun.computenestsupplier20210521.models.ListServiceInstancesResponseBody.ListServiceInstancesResponseBodyServiceInstances;
import com.aliyun.tea.TeaException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.adapter.ComputeNestSupplierClient;
import org.example.common.config.SpecificationConfig;
import org.example.common.constant.ComputeNestConstants;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.helper.WalletHelper;
import org.example.common.model.ServiceInstanceModel;
import org.example.common.model.ServiceMetadataModel;
import org.example.common.model.ServiceModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.GetServiceCostParam;
import org.example.common.param.GetServiceInstanceParam;
import org.example.common.param.GetServiceMetadataParam;
import org.example.common.param.ListServiceInstancesParam;
import org.example.common.utils.HttpUtil;
import org.example.common.utils.JsonUtil;
import org.example.common.utils.OpenAPIErrorMessageUtil;
import org.example.common.utils.UuidUtil;
import org.example.common.utils.YamlUtil;
import org.example.service.ServiceInstanceLifecycleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.example.common.constant.ComputeNestConstants.PAY_PERIOD;
import static org.example.common.constant.ComputeNestConstants.PAY_PERIOD_UNIT;
import static org.example.common.constant.ComputeNestConstants.PREDEFINED_PARAMETERS;
import static org.example.common.constant.ComputeNestConstants.TEMPLATE_CONFIGS;
import static org.example.common.constant.ComputeNestConstants.TEMPLATE_NAME;
import static org.example.common.constant.ComputeNestConstants.TEMPLATE_NAME_PREFIX;
import static org.example.common.constant.ComputeNestConstants.TEMPLATE_URL;



@Service
@Slf4j
public class ServiceInstanceLifecycleServiceImpl implements ServiceInstanceLifecycleService {

    private final ComputeNestSupplierClient computeNestSupplierClient;

    @Resource
    private WalletHelper walletHelper;

    @Resource
    private SpecificationConfig specificationConfig;

    @Value("${service.id}")
    private String serviceId;

    @Value("${service.region-id}")
    private String regionId;

    private static final String PREFIX = "saas-boost";

    public ServiceInstanceLifecycleServiceImpl(ComputeNestSupplierClient computeNestSupplierClient) {
        this.computeNestSupplierClient = computeNestSupplierClient;
    }

    @Override
    public ListResult<ServiceInstanceModel> listServiceInstances(UserInfoModel userInfoModel, ListServiceInstancesParam listServiceInstancesParam) {
        if (StringUtils.isBlank(userInfoModel.getAid())) {
            throw new BizException(ErrorInfo.VERIFY_FAILED.getStatusCode(), ErrorInfo.VERIFY_FAILED.getCode(), ErrorInfo.VERIFY_FAILED.getMessage());
        }
        ListServiceInstancesRequest request = convertToRequest(listServiceInstancesParam);
        request.setFilter(setFilterForListServiceInstance(listServiceInstancesParam, userInfoModel));
        try {
            ListServiceInstancesResponse response = computeNestSupplierClient.listServiceInstances(request);
            return convertFromListServiceInstancesResponse(response);
        } catch (TeaException e) {
            throw new BizException(e.getStatusCode(), e.getCode(), OpenAPIErrorMessageUtil.getErrorMessageFromComputeNestError(e.getMessage()));
        } catch (Exception e) {
            log.error("listServiceInstances error.", e);
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE.getStatusCode(), ErrorInfo.SERVER_UNAVAILABLE.getCode(), ErrorInfo.SERVER_UNAVAILABLE.getMessage());
        }
    }

    private List<ListServiceInstancesRequestFilter> setFilterForListServiceInstance(ListServiceInstancesParam listServiceInstancesParam, UserInfoModel userInfoModel) {
        ListServiceInstancesRequestFilter filter = new ListServiceInstancesRequestFilter()
                .setName(ComputeNestConstants.USER_ID)
                .setValue(Arrays.asList(userInfoModel.getAid()));
        ListServiceInstancesRequestFilter filterServiceType = new ListServiceInstancesRequestFilter()
                .setName(ComputeNestConstants.SERVICE_TYPE_PARAMETER)
                .setValue(Arrays.asList(ComputeNestConstants.MANAGED_SERVICE_TYPE));
        ListServiceInstancesRequestFilter filterServiceId = new ListServiceInstancesRequestFilter()
                .setName(ComputeNestConstants.SERVICE_ID)
                .setValue(Arrays.asList(serviceId));
        List<ListServiceInstancesRequestFilter> filterList = new ArrayList<>();
        filterList.add(filterServiceType);
        filterList.add(filterServiceId);
        filterList.add(filter);
        if (!StringUtils.isEmpty(listServiceInstancesParam.getServiceInstanceId())) {
            ListServiceInstancesRequestFilter filterServiceInstanceId = new ListServiceInstancesRequestFilter()
                    .setName(ComputeNestConstants.SERVICE_INSTANCE_ID)
                    .setValue(Arrays.asList(listServiceInstancesParam.getServiceInstanceId()));
            filterList.add(filterServiceInstanceId);
        }
        if (!StringUtils.isEmpty(listServiceInstancesParam.getServiceInstanceName())) {
            ListServiceInstancesRequestFilter filterServiceInstanceName = new ListServiceInstancesRequestFilter()
                    .setName(ComputeNestConstants.SERVICE_INSTANCE_NAME)
                    .setValue(Arrays.asList(listServiceInstancesParam.getServiceInstanceName()));
            filterList.add(filterServiceInstanceName);
        }

        if (!StringUtils.isEmpty(listServiceInstancesParam.getStatus())) {
            ListServiceInstancesRequestFilter filterStatus = new ListServiceInstancesRequestFilter()
                    .setName(ComputeNestConstants.SERVICE_INSTANCE_STATUS)
                    .setValue(Arrays.asList(listServiceInstancesParam.getStatus()));
            filterList.add(filterStatus);
        }
        return filterList;
    }

    @Override
    public BaseResult<ServiceInstanceModel> getServiceInstance(UserInfoModel userInfoModel, GetServiceInstanceParam getServiceInstanceParam) {
        if (StringUtils.isBlank(userInfoModel.getAid())) {
            throw new BizException(ErrorInfo.VERIFY_FAILED.getStatusCode(), ErrorInfo.VERIFY_FAILED.getCode(), ErrorInfo.VERIFY_FAILED.getMessage());
        }
        GetServiceInstanceRequest request = new GetServiceInstanceRequest();
        request.setServiceInstanceId(getServiceInstanceParam.getServiceInstanceId());
        try {
            GetServiceInstanceResponse response = computeNestSupplierClient.getServiceInstance(request);
            GetServiceInstanceResponseBody responseBody = filterServiceInstanceResponseWithAid(userInfoModel, response);
            if (responseBody == null) {
                log.info("The current logged-in user with aid={} does not have permission to view ServiceInstanceId={}.", userInfoModel.getAid(), response.getBody().getServiceInstanceId());
                return BaseResult.success(null);
            }
            ServiceInstanceModel serviceInstanceModel = convertFromServiceInstanceResponse(responseBody);
            return BaseResult.success(serviceInstanceModel);
        } catch (TeaException e) {
            throw new BizException(e.getStatusCode(), e.getCode(), OpenAPIErrorMessageUtil.getErrorMessageFromComputeNestError(e.getMessage()));
        } catch (Exception e) {
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE.getStatusCode(), ErrorInfo.SERVER_UNAVAILABLE.getCode(), ErrorInfo.SERVER_UNAVAILABLE.getMessage());
        }
    }

    @Override
    public CreateServiceInstanceResponse createServiceInstance(UserInfoModel userInfoModel, Map<String, Object> map, boolean dryRun) {
        CreateServiceInstanceRequest request = new CreateServiceInstanceRequest();
        request.setUserId(userInfoModel.getAid());
        request.setServiceId(serviceId);
        request.setClientToken(UuidUtil.generateUuid(PREFIX));
        request.setRegionId(ComputeNestConstants.DEFAULT_REGION_ID);
        map.put(ComputeNestConstants.REGION_ID, ComputeNestConstants.DEFAULT_REGION_ID);
        map.remove(PAY_PERIOD);
        map.remove(PAY_PERIOD_UNIT);
        Object specificationName = map.remove(ComputeNestConstants.SPECIFICATION_NAME);
        if (specificationName != null && !ComputeNestConstants.DEFAULT_SPECIFICATION_NAME.equals(String.valueOf(specificationName))) {
            request.setSpecificationName(String.valueOf(specificationName));
        }
        request.setParameters(map);
        request.setDryRun(dryRun);
        request.setTemplateName(String.valueOf(map.remove(TEMPLATE_NAME_PREFIX)));
        return computeNestSupplierClient.createServiceInstance(request);
    }

    @Override
    public ContinueDeployServiceInstanceResponse continueDeployServiceInstance(ContinueDeployServiceInstanceRequest request) {
        return computeNestSupplierClient.continueDeployServiceInstance(request);
    }

    @Override
    public BaseResult<Double> getServiceCost(UserInfoModel userInfoModel, GetServiceCostParam param) {
        return BaseResult.success(walletHelper.getServiceCost(param.getServiceId(), param.getSpecificationName(), param.getPayPeriod(), param.getPayPeriodUnit()));
    }

    @Cacheable(value = "serviceInstanceModelCache", key = "targetClass + methodName+#getServiceMetadataParam.serviceId")
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
            String templateConfigData = HttpUtil.doGet(url);
            if (!JsonUtil.isJson(templateConfigData)) {
                templateConfigData = YamlUtil.convertYamlToJson(templateConfigData);
            }
            JsonNode rosTemplateJsonNode = mapper.readTree(templateConfigData);
            String parameterMetadata = rosTemplateJsonNode.toString();

            getServiceMetadataModel.setParameterMetadata(parameterMetadata);
            getServiceMetadataModel.setTemplateName(templateName);
            getServiceMetadataModel.setSpecifications(specificationsJsonNode.toString());
        } catch (JsonProcessingException e) {
            log.error("Parse deployMetadata failed", e);
        }
        return BaseResult.success(getServiceMetadataModel);
    }

    private GetServiceInstanceResponseBody filterServiceInstanceResponseWithAid(UserInfoModel userInfoModel, GetServiceInstanceResponse response) {
        GetServiceInstanceResponseBody responseBody = response.getBody();
        if (responseBody.getUserId().toString().equals(userInfoModel.getAid())) {
            return responseBody;
        } else {
            return null;
        }
    }

    private ListServiceInstancesRequest convertToRequest(ListServiceInstancesParam param) {
        return new ListServiceInstancesRequest().setMaxResults(param.getMaxResults())
                .setNextToken(param.getNextToken()).setRegionId(regionId)
                .setShowDeleted(false).setTag(null);
    }

    private ListResult<ServiceInstanceModel> convertFromListServiceInstancesResponse(ListServiceInstancesResponse response) {
        ListServiceInstancesResponseBody responseBody = response.getBody();
        List<ListServiceInstancesResponseBodyServiceInstances> listInstances = responseBody.getServiceInstances();
        List<ServiceInstanceModel> serviceInstanceModelList = listInstances.stream()
                .map(instanceResponseBody -> new ServiceInstanceModel(instanceResponseBody.getServiceInstanceId(),
                        instanceResponseBody.getName(),
                        instanceResponseBody.getCreateTime(),
                        instanceResponseBody.getUpdateTime(),
                        instanceResponseBody.getStatus(),
                        instanceResponseBody.getProgress(),
                        instanceResponseBody.getService().getServiceInfos().get(0).getName(),
                        buildServiceForList(instanceResponseBody),
                        instanceResponseBody.getParameters(), null, null))
                .collect(Collectors.toList());
        return ListResult.genSuccessListResult(serviceInstanceModelList, responseBody.getTotalCount(), responseBody.getNextToken());
    }

    private ServiceModel buildServiceForList(ListServiceInstancesResponseBodyServiceInstances instanceResponseBody) {
        return new ServiceModel(instanceResponseBody.getService().getServiceId(),
                instanceResponseBody.getService().getServiceInfos().get(0).getName(),
                instanceResponseBody.getService().getServiceInfos().get(0).getShortDescription(),
                instanceResponseBody.getService().getServiceInfos().get(0).getImage());
    }

    private ServiceInstanceModel convertFromServiceInstanceResponse(GetServiceInstanceResponseBody responseBody) {
        return new ServiceInstanceModel(responseBody.getServiceInstanceId(),
                responseBody.getName(),
                responseBody.getCreateTime(),
                responseBody.getUpdateTime(),
                responseBody.getStatus(),
                responseBody.getProgress(),
                responseBody.getService().getServiceInfos().get(0).getName(),
                buildService(responseBody),
                responseBody.getParameters(),
                responseBody.getOutputs(),
                responseBody.getResources());
    }

    private ServiceModel buildService(GetServiceInstanceResponseBody responseBody) {
        GetServiceInstanceResponseBodyServiceServiceInfos serviceInfo = responseBody.getService().getServiceInfos().get(0);
        return new ServiceModel(responseBody.getService().getServiceId(),
                serviceInfo.getName(),
                serviceInfo.getShortDescription(),
                serviceInfo.getImage());
    }
}
