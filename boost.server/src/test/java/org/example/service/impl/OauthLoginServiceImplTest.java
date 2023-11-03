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

package org.example.service.impl;

import mockit.Injectable;
import mockit.MockUp;
import mockit.Tested;
import org.apache.http.NameValuePair;
import org.example.common.BaseResult;
import org.example.common.config.OauthConfig;
import org.example.common.helper.TokenParseHelper;
import org.example.common.model.AuthTokenModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.GetAuthTokenParam;
import org.example.common.utils.HttpUtil;
import org.example.common.utils.JsonUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class OauthLoginServiceImplTest {
    @Tested
    private OauthLoginServiceImpl keycloakLoginService;

    @Injectable
    private TokenParseHelper tokenParseHelper;
    @Mock
    private TokenParseHelper mockTokenParseHelper;

    @InjectMocks
    private OauthLoginServiceImpl keycloakLoginServiceImplUnderTest;

    @Injectable
    private OauthConfig oauthConfig;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void testGetUserInfo() {
        final UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setSub("userId");
        userInfoModel.setName("name");
        userInfoModel.setLoginName("aliYunLoginName");
        userInfoModel.setAid("aliYunId");
        userInfoModel.setUid("aliYunUserId");
        final BaseResult<UserInfoModel> expectedResult = new BaseResult<>(userInfoModel);
        expectedResult.setRequestId("id");
        final UserInfoModel userInfoModel1 = new UserInfoModel("userId", "name", "aliYunLoginName", "aliYunId", "aliYunUserId");
        when(mockTokenParseHelper.getUserInfoFromIdToken(mockTokenParseHelper.parseBearerTokenToToken("token"))).thenReturn(userInfoModel1);

        final BaseResult<UserInfoModel> result = keycloakLoginServiceImplUnderTest.getUserInfo(userInfoModel1);
        result.setRequestId("id");
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetUserInfoFail() {
        when(mockTokenParseHelper.getUserInfoFromIdToken(mockTokenParseHelper.parseBearerTokenToToken("token"))).thenReturn(null);

        final BaseResult<UserInfoModel> result = keycloakLoginServiceImplUnderTest.getUserInfo(null);
        result.setRequestId("id");
        BaseResult<String> stringBaseResult = new BaseResult<>(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), "get User Info fail");
        stringBaseResult.setRequestId("id");
        assertEquals(stringBaseResult, result);
    }


    @Test
    public void testGetAuthToken() {
        new MockUp<HttpUtil>(HttpUtil.class) {
            @mockit.Mock
            public String doPost(String url, List<NameValuePair> list) {
                BaseResult result = BaseResult.success();
                return JsonUtil.toJsonString(result);
            }
        };
        BaseResult<AuthTokenModel> authToken = keycloakLoginService.getAuthToken(new GetAuthTokenParam());
        Assertions.assertTrue(authToken.getCode().equals("200"));
    }
}
