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
import org.example.common.dto.CommodityDTO;
import org.example.common.model.CommodityPriceModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.CommodityBaseParam;
import org.example.common.param.commodity.CreateCommodityParam;
import org.example.common.param.commodity.GetCommodityParam;
import org.example.common.param.commodity.ListAllCommoditiesParam;
import org.example.common.param.commodity.UpdateCommodityParam;
import org.example.common.param.commodity.specification.GetCommodityPriceParam;
import org.example.service.commodity.CommodityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


class CommodityControllerTest {
    @Mock
    private CommodityService commodityService;

    @InjectMocks
    private CommodityController commodityController;

    private UserInfoModel userInfo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreateCommodity() {
        CreateCommodityParam createCommodityParam = new CreateCommodityParam();
        UserInfoModel userInfo = new UserInfoModel();
        CommodityDTO expectedCommodityDTO = new CommodityDTO();
        BaseResult<CommodityDTO> serviceResponse = new BaseResult<>("code", "message", expectedCommodityDTO, "requestId");

        when(commodityService.createCommodity(any(), any(CreateCommodityParam.class)))
                .thenReturn(serviceResponse);

        BaseResult<CommodityDTO> result = commodityController.createCommodity(userInfo, createCommodityParam);

        Assertions.assertEquals(expectedCommodityDTO, result.getData());
        Assertions.assertEquals("code", result.getCode());
        Assertions.assertEquals("message", result.getMessage());
        Assertions.assertEquals("requestId", result.getRequestId());
    }

    @Test
    void testUpdateCommodity() {
        UpdateCommodityParam updateCommodityParam = new UpdateCommodityParam();
        BaseResult<Void> serviceResponse = new BaseResult<>("code", "message", null, "requestId");

        when(commodityService.updateCommodity(any(), any()))
                .thenReturn(serviceResponse);

        BaseResult<Void> result = commodityController.updateCommodity(userInfo, updateCommodityParam);

        Assertions.assertEquals("code", result.getCode());
        Assertions.assertEquals("message", result.getMessage());
        Assertions.assertEquals("requestId", result.getRequestId());
    }

    @Test
    void testDeleteCommodity() {
        CommodityBaseParam commodityBaseParam = new CommodityBaseParam();
        BaseResult<Void> serviceResponse = new BaseResult<>("code", "message", null, "requestId");

        when(commodityService.deleteCommodity(any(), any()))
                .thenReturn(serviceResponse);

        BaseResult<Void> result = commodityController.deleteCommodity(userInfo, commodityBaseParam);

        Assertions.assertEquals("code", result.getCode());
        Assertions.assertEquals("message", result.getMessage());
        Assertions.assertEquals("requestId", result.getRequestId());
    }

    @Test
    void testListAllCommodities() {
        ListAllCommoditiesParam listAllCommoditiesParam = new ListAllCommoditiesParam();
        ListResult<CommodityDTO> serviceResponse = ListResult.genSuccessListResult(Arrays.asList(new CommodityDTO()), 1);

        when(commodityService.listAllCommodities(any(), any()))
                .thenReturn(serviceResponse);

        ListResult<CommodityDTO> result = commodityController.listAllCommodities(userInfo, listAllCommoditiesParam);

        Assertions.assertEquals(1L, result.getCount());
        Assertions.assertEquals(serviceResponse.getData(), result.getData());
    }

    @Test
    void testGetCommodity() {
        GetCommodityParam getCommodityParam = new GetCommodityParam();
        CommodityDTO expectedCommodityDTO = new CommodityDTO();

        when(commodityService.getCommodity(any(GetCommodityParam.class)))
                .thenReturn(expectedCommodityDTO);

        CommodityDTO result = commodityController.getCommodity(getCommodityParam);

        Assertions.assertEquals(expectedCommodityDTO, result);
    }

    @Test
    void testGetCommodityPrice() {
        GetCommodityPriceParam getCommodityPriceParam = new GetCommodityPriceParam();
        CommodityPriceModel expectedCommodityPriceModel = new CommodityPriceModel();

        when(commodityService.getCommodityPrice(any(GetCommodityPriceParam.class)))
                .thenReturn(expectedCommodityPriceModel);

        CommodityPriceModel result = commodityController.getCommodityPrice(getCommodityPriceParam);

        Assertions.assertEquals(expectedCommodityPriceModel, result);

    }
}