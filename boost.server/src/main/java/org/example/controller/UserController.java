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
import org.example.common.model.AuthConfigurationModel;
import org.example.common.model.AuthTokenModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.GetAuthTokenParam;
import org.example.service.LoginService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@Api(value="user",tags={"user"})
@RequestMapping("/api")
public class UserController {

    @Resource
    private LoginService loginService;

    @ApiOperation(value = "获取指定用户的授权Token", nickname = "getAuthToken")
    @RequestMapping(path = "/getAuthToken",method = RequestMethod.GET)
    public BaseResult<AuthTokenModel> getAuthToken(@Valid @APIParameterConvert GetAuthTokenParam getAuthTokenParam) {
        return loginService.getAuthToken(getAuthTokenParam);
    }

    @ApiOperation(value = "获取已授权用户的信息", nickname = "getUserInfo")
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult<UserInfoModel> getUserInfo(@ApiIgnore @AuthenticationPrincipal UserInfoModel userInfoModel) {
        return loginService.getUserInfo(userInfoModel);
    }

    @ApiOperation(value = "获取认证配置", nickname = "getAuthConfiguration")
    @RequestMapping(value = "/getAuthConfiguration", method = RequestMethod.GET)
    @ResponseBody
    public BaseResult<AuthConfigurationModel> getAuthConfiguration(@ApiIgnore @AuthenticationPrincipal UserInfoModel userInfoModel) {
        return loginService.getAuthConfiguration(userInfoModel);
    }
}
