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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.constant.CommoditySpecificationOtsConstant;
import org.example.common.dataobject.CommoditySpecificationDO;
import org.example.common.dto.CommoditySpecificationDTO;
import org.example.common.helper.ots.BaseOtsHelper.OtsFilter;
import org.example.common.helper.ots.CommoditySpecificationOtsHelper;
import org.example.common.model.UserInfoModel;
import org.example.common.param.commodity.specification.CommoditySpecificationParam;
import org.example.common.param.commodity.specification.CreateCommoditySpecificationParam;
import org.example.common.param.commodity.specification.ListCommoditySpecificationParam;
import org.example.common.param.commodity.specification.UpdateCommoditySpecificationParam;
import org.example.service.commodity.CommoditySpecificationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CommoditySpecificationServiceImpl implements CommoditySpecificationService {

    @Resource
    private CommoditySpecificationOtsHelper commoditySpecificationOtsHelper;

    @Override
    public BaseResult createCommoditySpecification(UserInfoModel userInfoModel, CreateCommoditySpecificationParam param) {
        CommoditySpecificationDO specificationDO = new CommoditySpecificationDO();
        BeanUtils.copyProperties(param, specificationDO);
        specificationDO.setPayPeriods(param.getPayPeriods().toString());
        commoditySpecificationOtsHelper.createCommoditySpecification(specificationDO);
        return BaseResult.success();
    }

    @Override
    public ListResult<CommoditySpecificationDTO> listAllSpecifications(UserInfoModel userInfoModel, ListCommoditySpecificationParam param) {
        List<OtsFilter> matchFilters = new ArrayList<>();
        if (StringUtils.isNotEmpty(param.getCommodityCode())) {
            OtsFilter commodityCodeMatchFilter = OtsFilter.createMatchFilter(CommoditySpecificationOtsConstant.COMMODITY_CODE, param.getCommodityCode());
            matchFilters.add(commodityCodeMatchFilter);
        }

        if (StringUtils.isNotEmpty(param.getSpecificationName())) {
            OtsFilter specificationNameMatchFilter = OtsFilter.createMatchFilter(CommoditySpecificationOtsConstant.SPECIFICATION_NAME, param.getSpecificationName());
            matchFilters.add(specificationNameMatchFilter);
        }

        return commoditySpecificationOtsHelper.listCommoditySpecifications(param.getNextToken(), matchFilters, null);
    }

    @Override
    public BaseResult updateCommoditySpecification(UserInfoModel userInfoModel, UpdateCommoditySpecificationParam param) {
        CommoditySpecificationDO commoditySpecificationDO = new CommoditySpecificationDO();
        BeanUtils.copyProperties(param, commoditySpecificationDO);
        commoditySpecificationDO.setPayPeriods(param.getPayPeriods().toString());
        boolean updated = commoditySpecificationOtsHelper.updateCommoditySpecification(commoditySpecificationDO);
        return BaseResult.success(updated);
    }

    @Override
    public BaseResult deleteCommoditySpecification(UserInfoModel userInfoModel, CommoditySpecificationParam param) {
        boolean deleted = commoditySpecificationOtsHelper.deleteCommoditySpecification(param.getCommodityCode(), param.getSpecificationName());
        return BaseResult.success(deleted);
    }
}
