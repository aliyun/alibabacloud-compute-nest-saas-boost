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
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.dataobject.CommoditySpecificationDO;
import org.example.common.dto.CommoditySpecificationDTO;
import org.example.common.helper.ots.BaseOtsHelper;
import org.example.common.helper.ots.CommoditySpecificationOtsHelper;
import org.example.common.model.UserInfoModel;
import org.example.common.param.commodity.specification.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import org.example.common.constant.PayPeriodUnit;
import org.example.common.constant.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommoditySpecificationServiceImplTest {

    @Tested
    private CommoditySpecificationServiceImpl commoditySpecificationService;

    @Injectable
    private CommoditySpecificationOtsHelper commoditySpecificationOtsHelper;

    @Injectable
    private UserInfoModel userInfoModel;

    @Test
    void testCreateCommoditySpecification() {
        CreateCommoditySpecificationParam createParam = new CreateCommoditySpecificationParam();
        createParam.setPayPeriodUnit(PayPeriodUnit.Month);
        createParam.setPayPeriods(Arrays.asList(1L, 3L, 6L, 12L));
        createParam.setUnitPrice(9999L);
        createParam.setCurrency(Currency.CNY);

        new Expectations() {{
            commoditySpecificationOtsHelper.createCommoditySpecification((CommoditySpecificationDO) any);
        }};

        Assertions.assertDoesNotThrow(()->commoditySpecificationService.createCommoditySpecification(userInfoModel, createParam));

    }

    @Test
    void testListAllSpecifications() {
        ListCommoditySpecificationParam listParam = new ListCommoditySpecificationParam();
        listParam.setSpecificationName("Basic Plan");
        listParam.setCommodityCode("COMMODITY1");

        List<CommoditySpecificationDTO> specificationDTOList = Arrays.asList(new CommoditySpecificationDTO());
        ListResult<CommoditySpecificationDTO> expectedResponse = ListResult.genSuccessListResult(specificationDTOList, 1);

        new Expectations() {{
            commoditySpecificationOtsHelper.listCommoditySpecifications(
                    anyString, (List<BaseOtsHelper.OtsFilter>) any, null);
            result = expectedResponse;
        }};

        ListResult<CommoditySpecificationDTO> result = commoditySpecificationService.listAllSpecifications(userInfoModel, listParam);

        assertEquals(expectedResponse, result);
    }

    @Test
    void testUpdateCommoditySpecification() {
        UpdateCommoditySpecificationParam updateParam = new UpdateCommoditySpecificationParam();
        updateParam.setPayPeriodUnit(PayPeriodUnit.Month);
        updateParam.setPayPeriods(Arrays.asList(1L, 3L, 6L, 12L));
        updateParam.setUnitPrice(10999L);

        new Expectations() {{
            commoditySpecificationOtsHelper.updateCommoditySpecification((CommoditySpecificationDO) any);
            result = true;
        }};

        BaseResult result = commoditySpecificationService.updateCommoditySpecification(userInfoModel, updateParam);

    }

    @Test
    void testDeleteCommoditySpecification() {
        CommoditySpecificationParam deleteParam = new CommoditySpecificationParam();
        deleteParam.setCommodityCode("COMMODITY1");
        deleteParam.setSpecificationName("Basic Plan");

        new Expectations() {{
            commoditySpecificationOtsHelper.deleteCommoditySpecification(
                    deleteParam.getCommodityCode(), deleteParam.getSpecificationName());
            result = true;
        }};

        BaseResult result = commoditySpecificationService.deleteCommoditySpecification(userInfoModel, deleteParam);

    }
}

