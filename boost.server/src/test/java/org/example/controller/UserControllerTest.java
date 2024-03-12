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


import org.example.common.BaseResult;
import org.example.common.model.AuthTokenModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.GetAuthTokenParam;
import org.example.service.base.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class UserControllerTest {

    @Mock
    private LoginService mockLoginService;

    @InjectMocks
    private UserController userControllerUnderTest;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void testGetAuthToken() {
        GetAuthTokenParam getAuthTokenParam = createGetAuthTokenParam();
        BaseResult<AuthTokenModel> expectedResult = new BaseResult<>("200","OK",
                new AuthTokenModel("idToken", "refreshToken", "expiresIn"),"aaa");
        BaseResult<AuthTokenModel> result1 = new BaseResult<>(new AuthTokenModel("idToken", "refreshToken", "expiresIn"));
        GetAuthTokenParam getAuthTokenParam1 = createGetAuthTokenParam();
        when(mockLoginService.getAuthToken(getAuthTokenParam1)).thenReturn(result1);
        BaseResult<AuthTokenModel> result = userControllerUnderTest.getAuthToken(getAuthTokenParam);
        result.setRequestId("aaa");
        assertEquals(expectedResult, result);
    }
    private GetAuthTokenParam createGetAuthTokenParam(){
        GetAuthTokenParam getAuthTokenParam = new GetAuthTokenParam();
        getAuthTokenParam.setCode("code");
        getAuthTokenParam.setState("state");
        getAuthTokenParam.setSessionState("sessionState");
        getAuthTokenParam.setRedirectUri("redirectUri");
        return getAuthTokenParam;
    }

    @Test
    void testGetUserInfo() {
        UserInfoModel userInfoModel = createUserInfo();
        final BaseResult<UserInfoModel> expectedResult = new BaseResult<>(userInfoModel);
        expectedResult.setRequestId("aaa");
        final UserInfoModel userInfoModel1 = createUserInfo();
        final BaseResult<UserInfoModel> userInfoBaseResult = new BaseResult<>(userInfoModel1);
        when(mockLoginService.getUserInfo(userInfoModel1)).thenReturn(userInfoBaseResult);
        final BaseResult<UserInfoModel> result = userControllerUnderTest.getUserInfo(userInfoModel1);
        result.setRequestId("aaa");
        assertEquals(expectedResult, result);
    }
    private UserInfoModel createUserInfo(){
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setSub("userId");
        userInfoModel.setName("name");
        userInfoModel.setLoginName("aliYunLoginName");
        userInfoModel.setAid("aliYunId");
        userInfoModel.setUid("aliYunUserId");
        return userInfoModel;
    }

}
