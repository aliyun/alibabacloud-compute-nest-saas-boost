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
import org.example.common.constant.CallSource;
import org.example.common.constant.ServiceType;
import org.example.common.model.ServiceInstanceModel;
import org.example.common.model.ServiceModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.si.GetServiceInstanceParam;
import org.example.common.param.si.ListServiceInstancesParam;
import org.example.service.base.ServiceInstanceLifecycleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

class ServiceInstanceControllerTest {

    @Mock
    ServiceInstanceLifecycleService serviceInstanceLifecycleService;

    @InjectMocks
    ServiceInstanceController serviceInstanceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    ServiceInstanceModel createCreateServiceInstanceModel() {
        return new ServiceInstanceModel("serviceInstanceId", "serviceInstanceName", "createTime", "updateTime", "status", Long.valueOf(1),
                "serviceName", new ServiceModel("serviceId", "name", "description", "image"), "parameters", "outputs", "resources", CallSource.Supplier, "123", ServiceType.managed, "endtime");
    }

    @Test
    void testListServiceInstances() {
        List<ServiceInstanceModel> serviceInstanceModels = Arrays.asList(createCreateServiceInstanceModel());
        when(serviceInstanceLifecycleService.listServiceInstances(any(), any())).thenReturn(ListResult.genSuccessListResult(serviceInstanceModels, 1, "message"));

        ListResult<ServiceInstanceModel> serviceInstanceModelListResult = serviceInstanceController.listServiceInstances(new UserInfoModel("sub", "name", "loginName", "aid", "uid", Boolean.TRUE), new ListServiceInstancesParam());
        Assertions.assertEquals(serviceInstanceModelListResult.getCount(), 1);
    }

    @Test
    void testGetServiceInstance() {
        when(serviceInstanceLifecycleService.getServiceInstance(any(), any())).thenReturn(new BaseResult<ServiceInstanceModel>("code", "message", createCreateServiceInstanceModel(), "requestId"));
        BaseResult<ServiceInstanceModel> result = serviceInstanceController.getServiceInstance(new UserInfoModel("sub", "name", "loginName", "aid", "uid", Boolean.TRUE), new GetServiceInstanceParam("serviceInstanceId"));
        Assertions.assertEquals(result.getData().getServiceInstanceId(), "serviceInstanceId");
    }


}
