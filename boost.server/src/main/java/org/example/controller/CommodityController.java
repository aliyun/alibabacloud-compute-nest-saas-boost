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

import io.swagger.annotations.ApiOperation;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.dto.CommodityDTO;
import org.example.common.dto.CommoditySpecificationDTO;
import org.example.common.param.commodity.specification.CreateCommoditySpecificationParam;
import org.example.common.param.commodity.CommodityBaseParam;
import org.example.common.param.commodity.specification.CommoditySpecificationParam;
import org.example.common.param.commodity.CreateCommodityParam;
import org.example.common.param.commodity.UpdateCommodityParam;
import org.example.service.CommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/commodity")
public class CommodityController {
    private final CommodityService commodityService;

    @Autowired
    public CommodityController(CommodityService commodityService) {
        this.commodityService = commodityService;
    }

    @ApiOperation(value = "创建商品", nickname = "createCommodity")
    @RequestMapping(path = "/createCommodity", method = RequestMethod.POST)
    public BaseResult<CommodityDTO> createCommodity(CreateCommodityParam param) {
        return commodityService.createCommodity(param);
    }

    @ApiOperation(value = "创建商品规格", nickname = "createCommoditySpecification")
    @RequestMapping(path = "/createCommoditySpecification", method = RequestMethod.POST)
    public BaseResult<CommoditySpecificationDTO> createCommoditySpecification(CreateCommoditySpecificationParam param) {
        return commodityService.createCommoditySpecification(param);
    }

    @ApiOperation(value = "获取所有商品", nickname = "listAllCommodities")
    @RequestMapping(path = "/listAllCommodities", method = RequestMethod.POST)
    public ListResult<CommodityDTO> listAllCommodities(CommodityBaseParam commodityBaseParam) {
        return commodityService.listAllCommodities(commodityBaseParam);
    }

    @ApiOperation(value = "获取所有商品规格", nickname = "listAllSpecifications")
    @RequestMapping(path = "/listAllSpecifications", method = RequestMethod.POST)
    public ListResult<CommoditySpecificationDTO> listAllSpecifications(CommodityBaseParam commodityBaseParam) {
        return commodityService.listAllSpecifications(commodityBaseParam);
    }

    @ApiOperation(value = "更新商品信息", nickname = "updateCommodity")
    @RequestMapping(path = "/updateCommodity}", method = RequestMethod.PUT)
    public BaseResult<CommodityDTO> updateCommodity(UpdateCommodityParam param) {
        return commodityService.updateCommodity(param);
    }

    @ApiOperation(value = "删除商品", nickname = "deleteCommodity")
    @RequestMapping(path = "/deleteCommodity", method = RequestMethod.DELETE)
    public BaseResult<Void> deleteCommodity(CommodityBaseParam commodityBaseParam) {
        commodityService.deleteCommodity(commodityBaseParam);
        return BaseResult.success();
    }

    @ApiOperation(value = "更新商品规格信息", nickname = "updateCommoditySpecification")
    @RequestMapping(path = "/updateCommoditySpecification", method = RequestMethod.PUT)
    public BaseResult<CommoditySpecificationDTO> updateCommoditySpecification(
            @PathVariable String specificationName, @RequestBody CommoditySpecificationDTO specificationDTO) {
        return commodityService.updateCommoditySpecification(specificationName, specificationDTO);
    }

    @ApiOperation(value = "删除商品规格", nickname = "deleteCommoditySpecification")
    @RequestMapping(path = "/deleteCommoditySpecification", method = RequestMethod.DELETE)
    public BaseResult<Void> deleteCommoditySpecification(CommoditySpecificationParam param) {
        return commodityService.deleteCommoditySpecification(param);
    }
}
