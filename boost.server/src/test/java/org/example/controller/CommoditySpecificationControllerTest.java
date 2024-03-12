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
import org.example.common.dto.CommoditySpecificationDTO;
import org.example.common.model.UserInfoModel;
import org.example.common.param.commodity.specification.CommoditySpecificationParam;
import org.example.common.param.commodity.specification.CreateCommoditySpecificationParam;
import org.example.common.param.commodity.specification.ListCommoditySpecificationParam;
import org.example.common.param.commodity.specification.UpdateCommoditySpecificationParam;
import org.example.service.commodity.CommoditySpecificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CommoditySpecificationControllerTest {
    @Mock
    private CommoditySpecificationService commoditySpecificationService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CommoditySpecificationController commoditySpecificationController;

    private UserInfoModel userInfo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userInfo = new UserInfoModel();
        when(authentication.getPrincipal()).thenReturn(userInfo);
    }

    @Test
    void testCreateCommoditySpecification() {
        CreateCommoditySpecificationParam param = new CreateCommoditySpecificationParam();
        BaseResult<Void> expectedResponse = new BaseResult<>("code", "message", null, "requestId");

        when(commoditySpecificationService.createCommoditySpecification(any(UserInfoModel.class), any(CreateCommoditySpecificationParam.class)))
                .thenReturn(expectedResponse);

        BaseResult<Void> result = commoditySpecificationController.createCommoditySpecification(userInfo, param);

        assertEquals(expectedResponse, result);
    }

    @Test
    void testListAllSpecifications() {
        ListCommoditySpecificationParam param = new ListCommoditySpecificationParam();
        ListResult<CommoditySpecificationDTO> expectedResponse = ListResult.genSuccessListResult(Arrays.asList(new CommoditySpecificationDTO()), 1);

        when(commoditySpecificationService.listAllSpecifications(any(UserInfoModel.class), any(ListCommoditySpecificationParam.class)))
                .thenReturn(expectedResponse);

        ListResult<CommoditySpecificationDTO> result = commoditySpecificationController.listAllSpecifications(userInfo, param);

        assertEquals(expectedResponse, result);
    }

    @Test
    void testUpdateCommoditySpecification() {
        UpdateCommoditySpecificationParam param = new UpdateCommoditySpecificationParam();
        BaseResult<Void> expectedResponse = new BaseResult<>("code", "message", null, "requestId");

        when(commoditySpecificationService.updateCommoditySpecification(any(UserInfoModel.class), any(UpdateCommoditySpecificationParam.class)))
                .thenReturn(expectedResponse);

        BaseResult<Void> result = commoditySpecificationController.updateCommoditySpecification(userInfo, param);

        assertEquals(expectedResponse, result);
    }

    @Test
    void testDeleteCommoditySpecification() {
        CommoditySpecificationParam param = new CommoditySpecificationParam();
        BaseResult<Void> expectedResponse = new BaseResult<>("code", "message", null, "requestId");

        when(commoditySpecificationService.deleteCommoditySpecification(any(UserInfoModel.class), any(CommoditySpecificationParam.class)))
                .thenReturn(expectedResponse);

        BaseResult<Void> result = commoditySpecificationController.deleteCommoditySpecification(userInfo, param);

        assertEquals(expectedResponse, result);
    }
}