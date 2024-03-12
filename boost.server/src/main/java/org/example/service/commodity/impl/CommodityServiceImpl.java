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
import org.example.common.dataobject.CommodityDO;
import org.example.common.dto.CommodityDTO;
import org.example.common.dto.CommoditySpecificationDTO;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.helper.CommodityOtsHelper;
import org.example.common.helper.CommoditySpecificationOtsHelper;
import org.example.common.helper.SpiTokenHelper;
import org.example.common.helper.WalletHelper;
import org.example.common.model.CommodityPriceModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.commodity.CommodityBaseParam;
import org.example.common.param.commodity.CreateCommodityParam;
import org.example.common.param.commodity.GetCommodityParam;
import org.example.common.param.commodity.ListAllCommoditiesParam;
import org.example.common.param.commodity.UpdateCommodityParam;
import org.example.common.param.commodity.specification.GetCommodityPriceParam;
import org.example.service.commodity.CommodityService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static org.example.common.utils.UuidUtil.generateCommodityCode;

@Service
public class CommodityServiceImpl implements CommodityService {

    @Resource
    private CommodityOtsHelper commodityOtsHelper;

    @Resource
    private CommoditySpecificationOtsHelper commoditySpecificationOtsHelper;

    @Resource
    private WalletHelper walletHelper;

    @Resource
    private SpiTokenHelper spiTokenHelper;

    @Override
    public BaseResult<CommodityDTO> createCommodity(UserInfoModel userInfoModel, CreateCommodityParam param) {
        String commodityCode = generateCommodityCode();
        CommodityDO commodityDO = new CommodityDO();
        BeanUtils.copyProperties(param, commodityDO);
        commodityDO.setCommodityCode(commodityCode);
        return BaseResult.success(commodityOtsHelper.createCommodity(commodityDO));
    }

    @Override
    public ListResult<CommodityDTO> listAllCommodities(UserInfoModel userInfoModel, ListAllCommoditiesParam listAllCommoditiesParam) {
        return commodityOtsHelper.listCommodities(listAllCommoditiesParam.getNextToken(), null, null);
    }

    @Override
    public BaseResult<CommodityDTO> updateCommodity(UserInfoModel userInfoModel, UpdateCommodityParam param) {
        CommodityDTO commodity = commodityOtsHelper.getCommodity(param.getCommodityCode());
        BeanUtils.copyProperties(param, commodity);
        commodityOtsHelper.updateCommodity(convertToCommodityDO(commodity));
        return BaseResult.success(commodity);
    }

    @Override
    public BaseResult deleteCommodity(UserInfoModel userInfoModel, CommodityBaseParam param) {
        Boolean deleted = commodityOtsHelper.deleteCommodity(param.getCommodityCode());
        return BaseResult.success(deleted);
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

        return BaseResult.success(commodityOtsHelper.getCommodity(param.getCommodityCode()));
    }

    private CommodityDO convertToCommodityDO(CommodityDTO commodityDTO) {
        CommodityDO commodityDO = new CommodityDO();
        BeanUtils.copyProperties(commodityDTO, commodityDO);
        return commodityDO;
    }
}

