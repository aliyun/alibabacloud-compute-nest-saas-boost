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
package org.example.service;

import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.dto.CommodityDTO;
import org.example.common.dto.CommoditySpecificationDTO;
import org.example.common.param.commodity.specification.CreateCommoditySpecificationParam;
import org.example.common.param.commodity.CommodityBaseParam;
import org.example.common.param.commodity.specification.CommoditySpecificationParam;
import org.example.common.param.commodity.CreateCommodityParam;
import org.example.common.param.commodity.UpdateCommodityParam;

public interface CommodityService {

    BaseResult<CommodityDTO> createCommodity(CreateCommodityParam commodityDTO);


    ListResult<CommodityDTO> listAllCommodities(CommodityBaseParam commodityBaseParam);


    BaseResult<CommodityDTO> updateCommodity(UpdateCommodityParam param);

    BaseResult deleteCommodity(CommodityBaseParam commodityCode);

    BaseResult<CommoditySpecificationDTO> createCommoditySpecification(CreateCommoditySpecificationParam specificationDTO);

    ListResult<CommoditySpecificationDTO> listAllSpecifications(CommodityBaseParam commodityBaseParam);

    BaseResult<CommoditySpecificationDTO> updateCommoditySpecification(String commodityCode, CommoditySpecificationDTO specificationDTO);

    BaseResult deleteCommoditySpecification(CommoditySpecificationParam specificationName);

}
