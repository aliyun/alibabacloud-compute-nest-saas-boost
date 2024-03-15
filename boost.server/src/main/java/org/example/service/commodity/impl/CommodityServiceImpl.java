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

import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.constant.CommoditySpecificationOtsConstant;
import org.example.common.dataobject.CommodityDO;
import org.example.common.dto.CommodityDTO;
import org.example.common.dto.CommoditySpecificationDTO;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.helper.BaseOtsHelper.OtsFilter;
import org.example.common.helper.CommodityOtsHelper;
import org.example.common.helper.SpiTokenHelper;
import org.example.common.helper.WalletHelper;
import org.example.common.model.CommodityPriceModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.commodity.CommodityBaseParam;
import org.example.common.param.commodity.CreateCommodityParam;
import org.example.common.param.commodity.GetCommodityParam;
import org.example.common.param.commodity.ListAllCommoditiesParam;
import org.example.common.param.commodity.UpdateCommodityParam;
import org.example.common.param.commodity.specification.CommoditySpecificationParam;
import org.example.common.param.commodity.specification.GetCommodityPriceParam;
import org.example.common.param.commodity.specification.ListCommoditySpecificationParam;
import org.example.service.commodity.CommodityService;
import org.example.service.commodity.CommoditySpecificationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.example.common.utils.UuidUtil.generateCommodityCode;

@Service
public class CommodityServiceImpl implements CommodityService {

    @Resource
    private CommodityOtsHelper commodityOtsHelper;

    @Resource
    private CommoditySpecificationService commoditySpecificationService;

    @Resource
    private WalletHelper walletHelper;

    @Resource
    private SpiTokenHelper spiTokenHelper;

    @Value("${service.admin.aid}")
    private String adminAid;

    private static final String ARRAY_REGEX = "\\s*,\\s*";

    @Override
    public BaseResult<CommodityDTO> createCommodity(UserInfoModel userInfoModel, CreateCommodityParam param) {
        String commodityCode = generateCommodityCode();
        CommodityDO commodityDO = new CommodityDO();
        BeanUtils.copyProperties(param, commodityDO);
        commodityDO.setCommodityCode(commodityCode);
        commodityDO.setOwnerId(userInfoModel.getAid());
        return BaseResult.success(commodityOtsHelper.createCommodity(commodityDO));
    }

    @Override
    public ListResult<CommodityDTO> listAllCommodities(UserInfoModel userInfoModel, ListAllCommoditiesParam param) {
        List<OtsFilter> matchFilters = new ArrayList<>();
        OtsFilter commodityCodeMatchFilter = OtsFilter.createMatchFilter(CommoditySpecificationOtsConstant.OWNER_ID, adminAid);
        matchFilters.add(commodityCodeMatchFilter);

        return commodityOtsHelper.listCommodities(param.getNextToken(), matchFilters, null);
    }

    @Override
    public BaseResult<Void> updateCommodity(UserInfoModel userInfoModel, UpdateCommodityParam param) {
        CommodityDO commodityDO = new CommodityDO();
        BeanUtils.copyProperties(param, commodityDO);
        commodityOtsHelper.updateCommodity(commodityDO);
        return BaseResult.success();
    }

    @Override
    public BaseResult<Void> deleteCommodity(UserInfoModel userInfoModel, CommodityBaseParam param) {
        ListCommoditySpecificationParam listCommoditySpecificationParam = new ListCommoditySpecificationParam();
        listCommoditySpecificationParam.setCommodityCode(param.getCommodityCode());
        ListResult<CommoditySpecificationDTO> commoditySpecificationResult = commoditySpecificationService.listAllSpecifications(userInfoModel,
                listCommoditySpecificationParam);
        if (commoditySpecificationResult.getData() != null && !commoditySpecificationResult.getData().isEmpty()) {
            List<CommoditySpecificationDTO> commoditySpecifications = commoditySpecificationResult.getData();
            for (CommoditySpecificationDTO commoditySpecification : commoditySpecifications) {
                CommoditySpecificationParam deleteParam = new CommoditySpecificationParam();
                deleteParam.setCommodityCode(param.getCommodityCode());
                deleteParam.setSpecificationName(commoditySpecification.getSpecificationName());
                commoditySpecificationService.deleteCommoditySpecification(userInfoModel, deleteParam);
            }
        }
        commodityOtsHelper.deleteCommodity(param.getCommodityCode());
        return BaseResult.success();
    }

    @Override
    public BaseResult<CommodityPriceModel> getCommodityPrice(GetCommodityPriceParam param) {
        if (!spiTokenHelper.checkSpiToken(param, param.getToken())) {
            return BaseResult.fail(ErrorInfo.SPI_TOKEN_VALIDATION_FAILED);
        }

        String commodityCode = param.getCommodityCode();
        String specificationName = param.getSpecificationName();
        CommodityPriceModel commodityCost = walletHelper.getCommodityCost(commodityCode, specificationName, param.getPayPeriod());
        return BaseResult.success(commodityCost);
    }

    @Override
    public BaseResult<CommodityDTO> getCommodity(GetCommodityParam param) {
        if (!spiTokenHelper.checkSpiToken(param, param.getToken())) {
            return BaseResult.fail(ErrorInfo.SPI_TOKEN_VALIDATION_FAILED);
        }
        CommodityDTO commodity = commodityOtsHelper.getCommodity(param.getCommodityCode());
        ListCommoditySpecificationParam listCommoditySpecificationParam = new ListCommoditySpecificationParam();
        listCommoditySpecificationParam.setCommodityCode(param.getCommodityCode());
        ListResult<CommoditySpecificationDTO> commoditySpecifications = commoditySpecificationService.
                listAllSpecifications(null, listCommoditySpecificationParam);
        Map<String, List<String>> allowedPaymentDurations = new HashMap<>();
        if (commoditySpecifications.getData() != null && !commoditySpecifications.getData().isEmpty()) {
            for (CommoditySpecificationDTO commoditySpecification : commoditySpecifications.getData()) {
                String payPeriodsStr = commoditySpecification.getPayPeriods();
                String specificationName = commoditySpecification.getSpecificationName();
                String payPeriodUnit = commoditySpecification.getPayPeriodUnit();
                List<Integer> payPeriods = Arrays.stream(payPeriodsStr.split(ARRAY_REGEX))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                for (Integer payPeriod : payPeriods) {
                    List<String> allowedPaymentDuration = allowedPaymentDurations.getOrDefault(specificationName, new ArrayList<>());
                    allowedPaymentDuration.add(payPeriod+":"+payPeriodUnit);
                    allowedPaymentDurations.put(specificationName, allowedPaymentDuration);
                }
            }
            commodity.setAllowedPaymentDurations(allowedPaymentDurations);
        }
        return BaseResult.success(commodity);
    }
}

