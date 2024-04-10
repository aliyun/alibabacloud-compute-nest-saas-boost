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
import com.aliyun.cms20190101.models.DescribeMetricListResponseBody;
import com.aliyun.tea.TeaException;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.adapter.CloudMonitorClient;
import org.example.common.exception.BizException;
import org.example.common.model.MetricDatasModel;
import org.example.common.model.MetricMetaDataModel;
import org.example.common.model.MetricMetaInfoModel;
import org.example.common.model.ServiceInstanceModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.GetServiceInstanceParam;
import org.example.common.param.ListMetricsParam;
import org.example.service.ServiceInstanceLifecycleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CloudMonitorServiceImplTest {

    private CloudMonitorServiceImpl cloudMonitorService;

    @Mock
    private ServiceInstanceLifecycleService serviceInstanceLifecycleService;

    @Mock
    private CloudMonitorClient cloudMonitorClient;

    private String resource = "{\n" +
            "  \"Resources\": null,\n" +
            "  \"CloudMonitorGroupId\": 111111111111\n" +
            "}";

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        cloudMonitorService = new CloudMonitorServiceImpl(serviceInstanceLifecycleService, cloudMonitorClient);
        List<MetricMetaInfoModel> mockMetricMetaInfoModels = new ArrayList<>();
        mockMetricMetaInfoModels.add(new MetricMetaInfoModel("nameSpace1", "metricName1", "metricName1", "metricName1", Arrays.asList("metricName1")));
        mockMetricMetaInfoModels.add(new MetricMetaInfoModel("nameSpace2", "metricName2", "metricName1", "metricName1", Arrays.asList("metricName1")));
        ReflectionTestUtils.setField(cloudMonitorService, "listMetricMetaInfoModels", mockMetricMetaInfoModels);
    }

    @Test
    void testListMetricMetaDatas() {
        ListResult<MetricMetaDataModel> result = cloudMonitorService.listMetricMetaDatas();
        assertEquals(2, result.getCount());
    }

    @Test
    void testListMetrics() throws Exception {
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setAid("11111");
        ListMetricsParam listMetricsParam = new ListMetricsParam("metricName1", "metricName", "123456789", "987654321");
        ServiceInstanceModel mockServiceInstanceModel = new ServiceInstanceModel();
        String resource = "{\n" +
                "  \"Resources\": null,\n" +
                "  \"CloudMonitorGroupId\": 111111111111\n" +
                "}";
        mockServiceInstanceModel.setResources(resource);
        when(serviceInstanceLifecycleService.getServiceInstance(any(UserInfoModel.class), any(GetServiceInstanceParam.class)))
                .thenReturn(BaseResult.success(mockServiceInstanceModel));

        DescribeMetricListResponse mockMetricListResponse = new DescribeMetricListResponse();
        mockMetricListResponse.setBody(new DescribeMetricListResponseBody());
        mockMetricListResponse.getBody().setDatapoints("dataPoints");
        when(cloudMonitorClient.getMetricList(any(DescribeMetricListRequest.class)))
                .thenReturn(mockMetricListResponse);

        BaseResult<MetricDatasModel> result = cloudMonitorService.listMetrics(userInfoModel, listMetricsParam);
        Assertions.assertNotNull(result.getData());

    }

    @Test
    void testListMetricsThrowsException() throws Exception {
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setAid("11111");
        ListMetricsParam listMetricsParam = new ListMetricsParam("metricName1", "metricName", "123456789", "987654321");
        ServiceInstanceModel mockServiceInstanceModel = new ServiceInstanceModel();
        mockServiceInstanceModel.setResources(resource);
        when(serviceInstanceLifecycleService.getServiceInstance(any(UserInfoModel.class), any(GetServiceInstanceParam.class)))
                .thenReturn(BaseResult.success(mockServiceInstanceModel));

        DescribeMetricListResponse mockMetricListResponse = new DescribeMetricListResponse();
        mockMetricListResponse.setBody(new DescribeMetricListResponseBody());
        mockMetricListResponse.getBody().setDatapoints("dataPoints");
        TeaException teaException = new TeaException();
        teaException.setStatusCode(111);
        teaException.setMessage("test");
        teaException.setCode("code");
        when(cloudMonitorClient.getMetricList(any(DescribeMetricListRequest.class)))
                .thenThrow(teaException);
        assertThrows(BizException.class, () -> cloudMonitorService.listMetrics(userInfoModel, listMetricsParam));
        when(cloudMonitorClient.getMetricList(any(DescribeMetricListRequest.class)))
                .thenThrow(new RuntimeException());
        assertThrows(BizException.class, () -> cloudMonitorService.listMetrics(userInfoModel, listMetricsParam));
    }

    @Test
    void testListMetricsThrowsResourceException() throws Exception {
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setAid("11111");
        ListMetricsParam listMetricsParam = new ListMetricsParam("metricName1", "metricName", "123456789", "987654321");
        ServiceInstanceModel mockServiceInstanceModel = new ServiceInstanceModel();
        mockServiceInstanceModel.setResources("resource");
        when(serviceInstanceLifecycleService.getServiceInstance(any(UserInfoModel.class), any(GetServiceInstanceParam.class)))
                .thenReturn(BaseResult.success(mockServiceInstanceModel));
        when(cloudMonitorClient.getMetricList(any(DescribeMetricListRequest.class)))
                .thenThrow(BizException.class);
        assertThrows(BizException.class, () -> cloudMonitorService.listMetrics(userInfoModel, listMetricsParam));
    }


    @Test
    void testGetNameSpaceFromMetricName_existingMetricName() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        CloudMonitorServiceImpl cloudMonitorService = new CloudMonitorServiceImpl(null, null);

        List<MetricMetaInfoModel> mockMetricMetaInfoModels = new ArrayList<>();
        mockMetricMetaInfoModels.add(new MetricMetaInfoModel("nameSpace1", "metricName1", "metricName1", "metricName1", Arrays.asList("metricName1")));
        mockMetricMetaInfoModels.add(new MetricMetaInfoModel("nameSpace2", "metricName2", "metricName1", "metricName1", Arrays.asList("metricName1")));
        ReflectionTestUtils.setField(cloudMonitorService, "listMetricMetaInfoModels", mockMetricMetaInfoModels);
        Method privateMethod = CloudMonitorServiceImpl.class.getDeclaredMethod("getNameSpaceFromMetricName", String.class);
        privateMethod.setAccessible(true);

        String metricName = "metricName1";
        String result = (String) privateMethod.invoke(cloudMonitorService, metricName);

        assertEquals("nameSpace1", result);
    }
}

