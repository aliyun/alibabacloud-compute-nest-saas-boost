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

package org.example.controller;

import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.model.MetricDatasModel;
import org.example.common.model.MetricMetaDataModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.ListMetricsParam;
import org.example.service.base.CloudMonitorService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

class CloudMonitorControllerTest {
    @Mock
    CloudMonitorService cloudMonitorService;
    @InjectMocks
    CloudMonitorController cloudMonitorController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testListMetricMetaDatas() {
        when(cloudMonitorService.listMetricMetaDatas()).thenReturn(ListResult.genSuccessListResult(new ArrayList<>(), 0));

        ListResult<MetricMetaDataModel> result = cloudMonitorController.listMetricMetaDatas(new UserInfoModel("sub", "name", "loginName", "aid", "uid", Boolean.TRUE));
        Assertions.assertEquals(result.getCount(), 0);
    }

    @Test
    void testListMetrics() {
        when(cloudMonitorService.listMetrics(any(), any())).thenReturn(new BaseResult<MetricDatasModel>("code", "message", new MetricDatasModel("dataPoints"), "requestId"));

        BaseResult<MetricDatasModel> result = cloudMonitorController.listMetrics(new UserInfoModel("sub", "name", "loginName", "aid", "uid", Boolean.TRUE), new ListMetricsParam("metricName", "startTime", "endTime", "serviceInstanceId"));
        Assertions.assertEquals(new BaseResult<MetricDatasModel>("code", "message", new MetricDatasModel("dataPoints"), "requestId"), result);
    }
}

