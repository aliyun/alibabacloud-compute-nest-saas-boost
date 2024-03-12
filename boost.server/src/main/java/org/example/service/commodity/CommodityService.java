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
package org.example.service.commodity;

import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.dto.CommodityDTO;
import org.example.common.model.CommodityPriceModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.commodity.CommodityBaseParam;
import org.example.common.param.commodity.CreateCommodityParam;
import org.example.common.param.commodity.GetCommodityParam;
import org.example.common.param.commodity.ListAllCommoditiesParam;
import org.example.common.param.commodity.UpdateCommodityParam;
import org.example.common.param.commodity.specification.GetCommodityPriceParam;

import javax.validation.Valid;

public interface CommodityService {

    BaseResult<CommodityDTO> createCommodity(UserInfoModel userInfoModel, @Valid CreateCommodityParam param);


    ListResult<CommodityDTO> listAllCommodities(UserInfoModel userInfoModel, ListAllCommoditiesParam param);


    BaseResult<Void> updateCommodity(UserInfoModel userInfoModel, @Valid UpdateCommodityParam param);

    BaseResult<Void> deleteCommodity(UserInfoModel userInfoModel, @Valid CommodityBaseParam param);

    CommodityPriceModel getCommodityPrice(GetCommodityPriceParam param);

    CommodityDTO getCommodity(GetCommodityParam param);
}
