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
import org.example.common.model.UserInfoModel;
import org.example.common.param.GetServiceCostParam;
import org.example.service.base.ServiceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServiceManagerControllerTest {

    @Mock
    private ServiceManager serviceManager;

    @InjectMocks
    private ServiceManagerController serviceUnderTest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetServiceCost() {
        UserInfoModel userInfo = new UserInfoModel();
        GetServiceCostParam param = new GetServiceCostParam();
        BaseResult<Double> expectedResult = new BaseResult<>();
        expectedResult.setData(100.0);

        when(serviceManager.getServiceCost(userInfo, param))
                .thenReturn(expectedResult);
        BaseResult<Double> result = serviceUnderTest.getServiceCost(userInfo, param);
        assertEquals(expectedResult, result);
        verify(serviceManager).getServiceCost(userInfo, param);
    }
}