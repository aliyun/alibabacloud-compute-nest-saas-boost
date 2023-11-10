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

import com.aliyun.computenestsupplier20210521.models.GetServiceTemplateParameterConstraintsRequest;
import com.aliyun.computenestsupplier20210521.models.GetServiceTemplateParameterConstraintsResponseBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.common.APIParameterConvert;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.model.ServiceMetadataModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.GetServiceCostParam;
import org.example.common.param.GetServiceMetadataParam;
import org.example.service.ServiceManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api")
@Api(value="serviceManager",tags={"serviceManager"})
public class ServiceManagerController {

    @Resource
    private ServiceManager serviceManager;

    @ApiOperation(value = "获取服务支付金额", nickname = "getServiceCost")
    @RequestMapping(path = "/getServiceCost",method = RequestMethod.GET)
    public BaseResult<Double> getServiceCost(@ApiIgnore @AuthenticationPrincipal UserInfoModel userInfoModel, @APIParameterConvert GetServiceCostParam getServiceCostParam) {
        return serviceManager.getServiceCost(userInfoModel, getServiceCostParam);
    }

    @ApiOperation(value = "获取服务元数据数据", nickname = "getServiceMetadata")
    @RequestMapping(path = "/getServiceMetadata",method = RequestMethod.GET)
    public BaseResult<ServiceMetadataModel> getServiceMetadata(@ApiIgnore @AuthenticationPrincipal UserInfoModel userInfoModel, @APIParameterConvert GetServiceMetadataParam getServiceMetadataParam) {
        return serviceManager.getServiceMetadata(userInfoModel, getServiceMetadataParam);
    }

    @ApiOperation(value = "获取服务模版参数限制信息", nickname = "getServiceTemplateParameterConstraints")
    @RequestMapping(path = "/getServiceTemplateParameterConstraints",method = RequestMethod.GET)
    public ListResult<GetServiceTemplateParameterConstraintsResponseBody.GetServiceTemplateParameterConstraintsResponseBodyParameterConstraints> getServiceTemplateParameterConstraints(GetServiceTemplateParameterConstraintsRequest request) {
        return serviceManager.getServiceTemplateParameterConstraints(request);
    }
}
