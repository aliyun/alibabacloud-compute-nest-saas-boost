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

import mockit.Injectable;
import org.example.common.BaseResult;
import org.example.common.model.UserInfoModel;
import org.example.common.param.GetServiceCostParam;
import org.example.service.ServiceInstanceLifecycleService;
import org.example.service.ServiceManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ServiceManagerControllerTest {

    @InjectMocks
    ServiceManagerController serviceManagerController;

    @Injectable
    private ServiceManager serviceManager;

    @Test
    void testGetServiceCost() {
        when(serviceManager.getServiceCost(any(), any())).thenReturn(new BaseResult<Double>("code", "message", Double.valueOf(0), "requestId"));
        BaseResult<Double> result = serviceManagerController.getServiceCost(new UserInfoModel("sub", "name", "loginName", "aid", "uid"), new GetServiceCostParam());
        Assertions.assertEquals(new BaseResult<Double>("code", "message", Double.valueOf(0), "requestId"), result);
    }
}