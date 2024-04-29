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
import javax.annotation.Resource;
import javax.validation.Valid;
import org.example.common.AdminAPI;
import org.example.common.BaseResult;
import org.example.common.ListResult;
import org.example.common.model.ConfigParameterModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.parameter.ListConfigParametersParam;
import org.example.common.param.parameter.UpdateConfigParameterParam;
import org.example.service.parameter.ParameterManagerService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author mengjunwei.mjw
 */
@RestController
@RequestMapping("/api")
@Api(value="parameterManager",tags={"parameterManager"})
public class ParameterManagerController {
    @Resource
    private ParameterManagerService parameterManagerService;

    @ApiOperation(value = "根据填报表批量查询参数", nickname = "listConfigParameters")
    @RequestMapping(path = "/listConfigParameters",method = RequestMethod.POST)
    public ListResult<ConfigParameterModel> listConfigParameters(@ApiIgnore @AuthenticationPrincipal UserInfoModel userInfoModel,
                                                                 @Valid @RequestBody ListConfigParametersParam listConfigParametersParam) {
        return parameterManagerService.listConfigParameters(userInfoModel, listConfigParametersParam);
    }

    @AdminAPI
    @ApiOperation(value = "根据输入的参数名更新参数", nickname = "updateConfigParameter")
    @RequestMapping(path = "/updateConfigParameter",method = RequestMethod.POST)
    public BaseResult<Void> updateConfigParameter(@ApiIgnore @AuthenticationPrincipal UserInfoModel userInfoModel,
                                                  @Valid @RequestBody UpdateConfigParameterParam updateConfigParameterParam) {
        return parameterManagerService.updateConfigParameter(userInfoModel, updateConfigParameterParam);
    }
}
