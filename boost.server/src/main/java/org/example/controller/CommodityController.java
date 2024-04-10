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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.common.AdminAPI;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.SPI;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@Api(value="commodity",tags={"commodity"})
public class CommodityController {

    private final CommodityService commodityService;

    @Autowired
    public CommodityController(CommodityService commodityService) {
        this.commodityService = commodityService;
    }

    @AdminAPI
    @ApiOperation(value = "创建商品", nickname = "createCommodity")
    @RequestMapping(path = "/createCommodity", method = RequestMethod.POST)
    public BaseResult<CommodityDTO> createCommodity(@ApiIgnore @AuthenticationPrincipal UserInfoModel userInfoModel,
                                                    @Valid CreateCommodityParam param) {
        return commodityService.createCommodity(userInfoModel, param);
    }

    @AdminAPI
    @ApiOperation(value = "更新商品信息", nickname = "updateCommodity")
    @RequestMapping(path = "/updateCommodity}", method = RequestMethod.PUT)
    public BaseResult<Void> updateCommodity(@ApiIgnore @AuthenticationPrincipal UserInfoModel userInfoModel,
                                            @Valid UpdateCommodityParam param) {
        return commodityService.updateCommodity(userInfoModel, param);
    }

    @AdminAPI
    @ApiOperation(value = "删除商品", nickname = "deleteCommodity")
    @RequestMapping(path = "/deleteCommodity", method = RequestMethod.DELETE)
    public BaseResult<Void> deleteCommodity(@ApiIgnore @AuthenticationPrincipal UserInfoModel userInfoModel,
                                            @Valid CommodityBaseParam commodityBaseParam) {
        return commodityService.deleteCommodity(userInfoModel, commodityBaseParam);
    }

    @ApiOperation(value = "获取所有商品", nickname = "listAllCommodities")
    @RequestMapping(path = "/listAllCommodities", method = RequestMethod.POST)
    public ListResult<CommodityDTO> listAllCommodities(@ApiIgnore @AuthenticationPrincipal UserInfoModel userInfoModel,
                                                       ListAllCommoditiesParam param) {
        return commodityService.listAllCommodities(userInfoModel, param);
    }

    @ApiOperation(value = "获取商品价格", nickname = "getCommodityPrice")
    @SPI(value = GetCommodityPriceParam.class)
    @RequestMapping(path = "/spi/getCommodityPrice", method = RequestMethod.POST)
    public CommodityPriceModel getCommodityPrice(@Valid @RequestBody GetCommodityPriceParam param) {
        return commodityService.getCommodityPrice(param);
    }

    @ApiOperation(value = "获取商品信息", nickname = "getCommodity")
    @SPI(value = GetCommodityParam.class)
    @RequestMapping(path = "/spi/getCommodity", method = RequestMethod.POST)
    public CommodityDTO getCommodity(@Valid @RequestBody GetCommodityParam param) {
        return commodityService.getCommodity(param);
    }
}
