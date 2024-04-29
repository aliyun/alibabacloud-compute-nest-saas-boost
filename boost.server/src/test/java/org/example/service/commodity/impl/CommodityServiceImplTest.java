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

package org.example.service.commodity.impl;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.Verifications;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.config.OosParamConfig;
import org.example.common.dataobject.CommodityDO;
import org.example.common.dto.CommodityDTO;
import org.example.common.dto.CommoditySpecificationDTO;
import org.example.common.helper.WalletHelper;
import org.example.common.helper.ots.BaseOtsHelper;
import org.example.common.helper.ots.CommodityOtsHelper;
import org.example.common.model.CommodityPriceModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.CommodityBaseParam;
import org.example.common.param.commodity.CreateCommodityParam;
import org.example.common.param.commodity.GetCommodityParam;
import org.example.common.param.commodity.ListAllCommoditiesParam;
import org.example.common.param.commodity.UpdateCommodityParam;
import org.example.common.param.commodity.specification.CommoditySpecificationParam;
import org.example.common.param.commodity.specification.GetCommodityPriceParam;
import org.example.common.param.commodity.specification.ListCommoditySpecificationParam;
import org.example.service.commodity.CommoditySpecificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommodityServiceImplTest {

    @Tested
    private CommodityServiceImpl commodityService;

    @Injectable
    private CommodityOtsHelper commodityOtsHelper;

    @Injectable
    private CommoditySpecificationService commoditySpecificationService;

    @Injectable
    private UserInfoModel userInfoModel;

    @Injectable
    private WalletHelper walletHelper;

    @Injectable
    private OosParamConfig oosSecretParamConfig;

    @Test
    void testCreateCommodity() {
        CreateCommodityParam createParam = new CreateCommodityParam();
        createParam.setCommodityName("Test Commodity");

        new Expectations() {{
            commodityOtsHelper.createCommodity((CommodityDO) any);
            result = new CommodityDTO();
        }};

        BaseResult<CommodityDTO> result = commodityService.createCommodity(userInfoModel, createParam);
    }

    @Test
    void testListAllCommodities() {
        ListAllCommoditiesParam listParam = new ListAllCommoditiesParam();

        ListResult<CommodityDTO> expectedResponse = ListResult.genSuccessListResult(new ArrayList<>(), 0);

        new Expectations() {{
            commodityOtsHelper.listCommodities(anyString, (List<BaseOtsHelper.OtsFilter>) any, null);
            result = expectedResponse;
        }};

        ListResult<CommodityDTO> result = commodityService.listAllCommodities(userInfoModel, listParam);
        assertEquals(expectedResponse, result);
    }

    @Test
    void testUpdateCommodity() {
        UpdateCommodityParam updateParam = new UpdateCommodityParam();
        updateParam.setCommodityCode("COMMODITY1");
        updateParam.setCommodityName("Test Commodity");

        new Expectations() {{
            commodityOtsHelper.updateCommodity((CommodityDO) any);
        }};

        BaseResult<Void> result = commodityService.updateCommodity(userInfoModel, updateParam);
    }

    @Test
    void testDeleteCommodity() {
        CommodityBaseParam deleteParam = new CommodityBaseParam();
        deleteParam.setCommodityCode("COMMODITY1");

        ListResult<CommoditySpecificationDTO> commoditySpecificationsResponse = ListResult.genSuccessListResult(new ArrayList<>(), 0);

        new Expectations() {{
            commoditySpecificationService.listAllSpecifications(userInfoModel, (ListCommoditySpecificationParam) any);
            result = commoditySpecificationsResponse;

            commodityOtsHelper.deleteCommodity(anyString);
        }};

        Assertions.assertDoesNotThrow(()->{
            commodityService.deleteCommodity(userInfoModel, deleteParam);
        });
    }

    @Test
    void testDeleteCommodityAndAllSpecifications() {
        UserInfoModel userInfoModel = new UserInfoModel();
        CommodityBaseParam param = new CommodityBaseParam();
        param.setCommodityCode("COMMODITY1");
        CommoditySpecificationDTO specificationA = new CommoditySpecificationDTO();
        CommoditySpecificationDTO specificationB = new CommoditySpecificationDTO();
        specificationA.setSpecificationName("A");
        specificationA.setCommodityCode("COMMODITY1");
        specificationB.setSpecificationName("B");
        specificationB.setCommodityCode("COMMODITY1");
        List<CommoditySpecificationDTO> specificationsList = Arrays.asList(
                specificationA, specificationB
        );

        new Expectations() {{
            commoditySpecificationService.listAllSpecifications(userInfoModel, (ListCommoditySpecificationParam) any);
            result = ListResult.genSuccessListResult(specificationsList, specificationsList.size());
            commodityOtsHelper.deleteCommodity(param.getCommodityCode());
        }};

        Assertions.assertDoesNotThrow(()->{
            commodityService.deleteCommodity(userInfoModel, param);
        });

        new Verifications() {{
            List<CommoditySpecificationParam> deleteParams = new ArrayList<>();
            commoditySpecificationService.deleteCommoditySpecification(userInfoModel, withCapture(deleteParams));
            times = specificationsList.size();

            for (int i = 0; i < specificationsList.size(); i++) {
                assertEquals("COMMODITY1", deleteParams.get(i).getCommodityCode());
                assertEquals(deleteParams.get(i).getSpecificationName(), specificationsList.get(i).getSpecificationName());
            }
        }};
    }

    @Test
    void testGetCommodityPrice() {
        GetCommodityPriceParam getPriceParam = new GetCommodityPriceParam();
        getPriceParam.setCommodityCode("COMMODITY1");
        getPriceParam.setSpecificationName("Basic");
        getPriceParam.setPayPeriod(12L);
        CommodityDTO commodityDTO = new CommodityDTO();
        CommodityPriceModel expectedCommodityPriceModel = new CommodityPriceModel();
        expectedCommodityPriceModel.setTotalAmount(1200L);

        new Expectations() {{
            walletHelper.getCommodityCost(anyString, anyString, anyLong);
            result = expectedCommodityPriceModel;
        }};

        CommodityPriceModel result = commodityService.getCommodityPrice(getPriceParam);
        assertEquals(result.getTotalAmount(), 1200L);
    }

    @Test
    void testGetCommodityWithNoSpecification() {
        GetCommodityParam getParam = new GetCommodityParam();
        getParam.setCommodityCode("COMMODITY1");
        CommodityDTO expectedCommodityDTO = new CommodityDTO();
        expectedCommodityDTO.setCommodityName("Test Commodity");
        expectedCommodityDTO.setCommodityCode("COMMODITY1");

        ListResult<CommoditySpecificationDTO> commoditySpecificationsResponse = ListResult.genSuccessListResult(
                Collections.emptyList(), 0);

        new Expectations() {{
            commodityOtsHelper.getCommodity(anyString);
            result = expectedCommodityDTO;

            commoditySpecificationService.listAllSpecifications((UserInfoModel) any, (ListCommoditySpecificationParam) any);
            result = commoditySpecificationsResponse;
        }};
        CommodityDTO commodityDTO = commodityService.getCommodity(getParam);

        List<String> MONTHS = IntStream.rangeClosed(1, 12)
                .mapToObj(i -> i + ":Month")
                .collect(Collectors.toList());
        Map<String, List<String>> allowedPaymentDurations = commodityDTO.getAllowedPaymentDurations();
        assertEquals(expectedCommodityDTO, commodityDTO);
        assertEquals(MONTHS, allowedPaymentDurations.get("COMMODITY1"));
    }

    @Test
    void testGetCommodityWhenHasSpecifications() {
        GetCommodityParam getParam = new GetCommodityParam();
        getParam.setCommodityCode("COMMODITY1");

        CommodityDTO expectedCommodityDTO = new CommodityDTO();
        expectedCommodityDTO.setCommodityCode("COMMODITY1");

        CommoditySpecificationDTO specificationDTO = new CommoditySpecificationDTO();
        specificationDTO.setCommodityCode("COMMODITY1");
        specificationDTO.setSpecificationName("J");
        specificationDTO.setPayPeriods("[1,2,3]");
        specificationDTO.setPayPeriodUnit("Month");
        ArrayList<CommoditySpecificationDTO> commoditySpecificationList = new ArrayList<>();
        commoditySpecificationList.add(specificationDTO);
        new Expectations() {{
            commodityOtsHelper.getCommodity(getParam.getCommodityCode());
            result = expectedCommodityDTO;

            commoditySpecificationService.listAllSpecifications((UserInfoModel) any, (ListCommoditySpecificationParam) any);
            result = ListResult.genSuccessListResult(commoditySpecificationList, 1);
        }};

        CommodityDTO commodityDTO = commodityService.getCommodity(getParam);
        Map<String, List<String>> allowedPaymentDurations = commodityDTO.getAllowedPaymentDurations();
        assertEquals(expectedCommodityDTO, commodityDTO);
        List<String> list = Arrays.asList("1:Month", "2:Month", "3:Month");
        assertEquals(list, allowedPaymentDurations.get("J"));
    }
}
