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
import org.example.common.dto.CommoditySpecificationDTO;
import org.example.common.model.UserInfoModel;
import org.example.common.param.commodity.specification.CommoditySpecificationParam;
import org.example.common.param.commodity.specification.CreateCommoditySpecificationParam;
import org.example.common.param.commodity.specification.ListCommoditySpecificationParam;
import org.example.common.param.commodity.specification.UpdateCommoditySpecificationParam;
import org.example.service.commodity.CommoditySpecificationService;
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
@Api(value="specification",tags={"specification"})
public class CommoditySpecificationController {

    private final CommoditySpecificationService commoditySpecificationService;

    @Autowired
    public CommoditySpecificationController(CommoditySpecificationService commoditySpecificationService) {
        this.commoditySpecificationService = commoditySpecificationService;
    }

    @ApiOperation(value = "查询商品规格", nickname = "getCommoditySpecification")
    @RequestMapping(path = "/getCommoditySpecification", method = RequestMethod.POST)
    public BaseResult<CommoditySpecificationDTO> getCommoditySpecification(@ApiIgnore @AuthenticationPrincipal UserInfoModel userInfoModel,
                                                         CommoditySpecificationParam param) {
        return commoditySpecificationService.getCommoditySpecification(userInfoModel, param);
    }

    @AdminAPI
    @ApiOperation(value = "创建商品规格", nickname = "createCommoditySpecification")
    @RequestMapping(path = "/createCommoditySpecification", method = RequestMethod.POST)
    public BaseResult<Void> createCommoditySpecification(@ApiIgnore @AuthenticationPrincipal UserInfoModel userInfoModel,
                                                         @Valid @RequestBody CreateCommoditySpecificationParam param) {
        return commoditySpecificationService.createCommoditySpecification(userInfoModel, param);
    }

    @AdminAPI
    @ApiOperation(value = "获取所有商品规格", nickname = "listAllSpecifications")
    @RequestMapping(path = "/listAllSpecifications", method = RequestMethod.POST)
    public ListResult<CommoditySpecificationDTO> listAllSpecifications(@ApiIgnore @AuthenticationPrincipal UserInfoModel userInfoModel,
                                                                       ListCommoditySpecificationParam param) {
        return commoditySpecificationService.listAllSpecifications(userInfoModel, param);
    }

    @AdminAPI
    @ApiOperation(value = "更新商品规格信息", nickname = "updateCommoditySpecification")
    @RequestMapping(path = "/updateCommoditySpecification", method = RequestMethod.PUT)
    public BaseResult<Void> updateCommoditySpecification(@ApiIgnore @AuthenticationPrincipal UserInfoModel userInfoModel,
                                                         @RequestBody @Valid UpdateCommoditySpecificationParam param) {
        return commoditySpecificationService.updateCommoditySpecification(userInfoModel, param);
    }

    @AdminAPI
    @ApiOperation(value = "删除商品规格", nickname = "deleteCommoditySpecification")
    @RequestMapping(path = "/deleteCommoditySpecification", method = RequestMethod.DELETE)
    public BaseResult deleteCommoditySpecification(@ApiIgnore @AuthenticationPrincipal UserInfoModel userInfoModel,
                                                   @Valid CommoditySpecificationParam param) {
        return commoditySpecificationService.deleteCommoditySpecification(userInfoModel, param);
    }
}
