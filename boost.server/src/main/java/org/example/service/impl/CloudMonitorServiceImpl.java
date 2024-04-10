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

import com.aliyun.cms20190101.models.DescribeMetricListRequest;
import com.aliyun.cms20190101.models.DescribeMetricListResponse;
import com.aliyun.tea.TeaException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.adapter.CloudMonitorClient;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.model.MetricDatasModel;
import org.example.common.model.MetricMetaDataModel;
import org.example.common.model.MetricMetaInfoModel;
import org.example.common.model.ServiceInstanceModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.GetServiceInstanceParam;
import org.example.common.param.ListMetricsParam;
import org.example.common.utils.JsonUtil;
import org.example.common.utils.OpenAPIErrorMessageUtil;
import org.example.service.CloudMonitorService;
import org.example.service.ServiceInstanceLifecycleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CloudMonitorServiceImpl implements CloudMonitorService {

    @Value("${cloud-monitor.metric-info}")
    private String metricInfo;

    private List<MetricMetaInfoModel> listMetricMetaInfoModels;

    private final ServiceInstanceLifecycleService serviceInstanceLifecycleService;

    private final CloudMonitorClient cloudMonitorClient;

    public CloudMonitorServiceImpl(ServiceInstanceLifecycleService serviceInstanceLifecycleService, CloudMonitorClient cloudMonitorClient) {
        this.serviceInstanceLifecycleService = serviceInstanceLifecycleService;
        this.cloudMonitorClient = cloudMonitorClient;
    }

    @PostConstruct
    private void init() {
        this.listMetricMetaInfoModels = JsonUtil.parseJsonList(metricInfo, new TypeToken<List<MetricMetaInfoModel>>() {
        }.getType());
    }

    @Override
    public ListResult<MetricMetaDataModel> listMetricMetaDatas() {
        List<MetricMetaDataModel> listModelMetricMetaData = this.listMetricMetaInfoModels.stream()
                .map(metricMetaInfoModel -> {
                    MetricMetaDataModel metricMetaDataModel = new MetricMetaDataModel();
                    BeanUtils.copyProperties(metricMetaInfoModel, metricMetaDataModel);
                    return metricMetaDataModel;
                })
                .collect(Collectors.toList());
        return ListResult.genSuccessListResult(listModelMetricMetaData, listModelMetricMetaData.size());
    }

    @Override
    public BaseResult<MetricDatasModel> listMetrics(UserInfoModel userInfoModel, ListMetricsParam listMetricsParam) {
        if (StringUtils.isBlank(userInfoModel.getAid())) {
            throw new BizException(ErrorInfo.VERIFY_FAILED.getStatusCode(), ErrorInfo.VERIFY_FAILED.getCode(), ErrorInfo.VERIFY_FAILED.getMessage());
        }
        GetServiceInstanceParam getServiceInstanceParam = new GetServiceInstanceParam(listMetricsParam.getServiceInstanceId());
        ServiceInstanceModel serviceInstanceModel = serviceInstanceLifecycleService.getServiceInstance(userInfoModel, getServiceInstanceParam).getData();
        if (serviceInstanceModel == null) {
            throw new BizException(ErrorInfo.RESOURCE_NOT_FOUND.getStatusCode(), ErrorInfo.RESOURCE_NOT_FOUND.getCode(), ErrorInfo.RESOURCE_NOT_FOUND.getMessage());
        }
        DescribeMetricListRequest metricRequest = new DescribeMetricListRequest();
        metricRequest.setNamespace(getNameSpaceFromMetricName(listMetricsParam.getMetricName()))
                .setMetricName(listMetricsParam.getMetricName())
                .setDimensions(JsonUtil.toJsonString(new Resources(getGroupIdFromServiceInstanceResponse(serviceInstanceModel.getResources()))))
                .setStartTime(listMetricsParam.getStartTime())
                .setEndTime(listMetricsParam.getEndTime());
        try {
            DescribeMetricListResponse metricResponse = cloudMonitorClient.getMetricList(metricRequest);
            MetricDatasModel metricDatasModel = new MetricDatasModel(metricResponse.getBody().getDatapoints());
            return BaseResult.success(metricDatasModel);
        } catch (TeaException e) {
            throw new BizException(e.getStatusCode(), e.getCode(), OpenAPIErrorMessageUtil.getErrorMessageFromComputeNestError(e.getMessage()));
        } catch (Exception e) {
            throw new BizException(ErrorInfo.SERVER_UNAVAILABLE.getStatusCode(), ErrorInfo.SERVER_UNAVAILABLE.getCode(), ErrorInfo.SERVER_UNAVAILABLE.getMessage());
        }
    }

    private String getNameSpaceFromMetricName(String metricName) {
        Optional<MetricMetaInfoModel> metricMetaInfosOptional = listMetricMetaInfoModels.stream().
                filter(metricMetaInfoModel -> metricName.equals(metricMetaInfoModel.getMetricName()))
                .findFirst();
        if (metricMetaInfosOptional.isPresent()) {
            return metricMetaInfosOptional.get().getNameSpace();
        } else {
            throw new BizException(ErrorInfo.RESOURCE_NOT_FOUND.getStatusCode(), ErrorInfo.RESOURCE_NOT_FOUND.getCode(), ErrorInfo.RESOURCE_NOT_FOUND.getMessage());
        }
    }

    private String getGroupIdFromServiceInstanceResponse(String resources) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        try {
            root = mapper.readTree(resources);
            return String.valueOf(root.get("CloudMonitorGroupId").asLong());
        } catch (Exception e) {
            log.error("Unable to correctly resolve the GroupId of this ServiceInstance. Resources = {}", resources);
            throw new BizException(ErrorInfo.RESOURCE_NOT_FOUND.getStatusCode(), ErrorInfo.RESOURCE_NOT_FOUND.getCode(), ErrorInfo.RESOURCE_NOT_FOUND.getMessage());
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Resources {
        private String groupId;
    }
}
