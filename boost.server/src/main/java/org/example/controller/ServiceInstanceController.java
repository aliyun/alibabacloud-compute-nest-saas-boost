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
import org.example.common.APIParameterConvert;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.model.ServiceInstanceModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.GetServiceInstanceParam;
import org.example.common.param.ListServiceInstancesParam;
import org.example.service.ServiceInstanceLifecycleService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api")
@Api(value="serviceInstance",tags={"serviceInstance"})
public class ServiceInstanceController {

    @Resource
    private ServiceInstanceLifecycleService serviceInstanceLifecycleService;

    @ApiOperation(value = "获取该用户下的全部服务实例列表", nickname = "listServiceInstances")
    @RequestMapping(path = "/listServiceInstances",method = RequestMethod.GET)
    public ListResult<ServiceInstanceModel> listServiceInstances(@ApiIgnore @AuthenticationPrincipal UserInfoModel userInfoModel, @APIParameterConvert ListServiceInstancesParam listServiceInstancesParam) {
        return serviceInstanceLifecycleService.listServiceInstances(userInfoModel, listServiceInstancesParam);
    }

    @ApiOperation(value = "获取指定ID的服务实例详情", nickname = "getServiceInstance")
    @RequestMapping(path = "/getServiceInstance",method = RequestMethod.GET)
    public BaseResult<ServiceInstanceModel> getServiceInstance(@ApiIgnore @AuthenticationPrincipal UserInfoModel userInfoModel, @APIParameterConvert GetServiceInstanceParam getServiceInstanceParam) {
        return serviceInstanceLifecycleService.getServiceInstance(userInfoModel, getServiceInstanceParam);
    }
}
