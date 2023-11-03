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

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.example.common.BaseResult;
import org.example.common.config.OauthConfig;
import org.example.common.constant.Constants;
import org.example.common.errorinfo.ErrorInfo;
import org.example.common.exception.BizException;
import org.example.common.model.AuthTokenModel;
import org.example.common.model.UserInfoModel;
import org.example.common.param.GetAuthTokenParam;
import org.example.common.utils.HttpUtil;
import org.example.common.utils.JsonUtil;
import org.example.service.LoginService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OauthLoginServiceImpl implements LoginService {

    @Resource
    private OauthConfig oauthConfig;

    @Override
    public BaseResult<UserInfoModel> getUserInfo(UserInfoModel userInfoModel) {
        if (null != userInfoModel && !StringUtils.isEmpty(userInfoModel.getSub())) {
            return BaseResult.success(userInfoModel);
        }
        return BaseResult.fail("get User Info fail");
    }

    @Override
    public BaseResult<AuthTokenModel> getAuthToken(GetAuthTokenParam getAuthTokenParam) {
        List<NameValuePair> params = createHttpParameters(getAuthTokenParam);
        String response = HttpUtil.doPost(oauthConfig.getAuthServerUrl(), params);
        if (!StringUtils.isEmpty(response)) {
            AuthTokenModel authTokenModel = JsonUtil.parseObjectByJackson(response, AuthTokenModel.class);
            return BaseResult.success(authTokenModel);
        }
        log.error("code to token error, response from Aliyun is {}", response);
        throw new BizException(ErrorInfo.SERVER_UNAVAILABLE.getStatusCode(), ErrorInfo.SERVER_UNAVAILABLE.getCode(), ErrorInfo.SERVER_UNAVAILABLE.getMessage());
    }

    private List<NameValuePair> createHttpParameters(GetAuthTokenParam getAuthTokenParam) {
        System.out.println(oauthConfig.getOauthClientId());
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(Constants.GRANT_TYPE, Constants.GRANT_TYPE_AUTHORIZATION));
        params.add(new BasicNameValuePair(Constants.CODE, getAuthTokenParam.getCode()));
        params.add(new BasicNameValuePair(Constants.CLIENT_ID, oauthConfig.getOauthClientId()));
        params.add(new BasicNameValuePair(Constants.CLIENT_SECRET, oauthConfig.getOauthClientSecret()));
        params.add(new BasicNameValuePair(Constants.REDIRECT_URI, getAuthTokenParam.getRedirectUri()));
        if (!StringUtils.isEmpty(getAuthTokenParam.getState())) {
            params.add(new BasicNameValuePair(Constants.STATE, getAuthTokenParam.getState()));
        }
        if (!StringUtils.isEmpty(getAuthTokenParam.getSessionState())) {
            params.add(new BasicNameValuePair(Constants.SESSION_STATE, getAuthTokenParam.getSessionState()));
        }
        params.add(new BasicNameValuePair(Constants.SCOPE, Constants.SCOPE_OPENID));
        return params;
    }
}
